/*
 * Copyright 2023 teogor (Teodor Grigor)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.teogor.drifter.wallpaper

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Point
import android.service.wallpaper.WallpaperService
import android.view.MotionEvent
import android.view.SurfaceHolder
import dev.teogor.drifter.integration.player.UnityPlayerHolder
import dev.teogor.drifter.integration.player.UnityPlayerInstanceManager
import dev.teogor.drifter.integration.player.UnityPlayerWrapper
import dev.teogor.drifter.integration.player.UnityPlayerWrapperInstantiator
import dev.teogor.drifter.integration.utilities.DebugLog.d
import dev.teogor.drifter.integration.utilities.DebugLog.logStartupMessage
import dev.teogor.drifter.integration.utilities.DebugLog.v
import dev.teogor.drifter.integration.utilities.MultiTapDetector.IMultiTapDetectedListener
import dev.teogor.drifter.integration.utilities.StandardExceptionHandler
import dev.teogor.drifter.integration.utilities.UnityVersionInfo.Companion.updateUnityVersionIfNeeded
import dev.teogor.drifter.wallpaper.LiveWallpaperUnityFacade.Companion.eventsProxy

/**
 * Service representing the live wallpaper with Unity backend.
 */
class UnityWallpaperService : WallpaperService() {
  private val mUnityEventsProxy: UnityEventsProxy = eventsProxy
  private var mUnityPlayerWrapper: UnityPlayerWrapper? = null
  private var mLiveWallpaperUnityFacade: LiveWallpaperUnityFacade? = null
  private var mUnityPlayerInstanceManager: UnityPlayerInstanceManager? = null

  /**
   * Whether at least one wallpaper engine was visible when screen turned off.
   */
  private var mIsWallpaperVisibleBeforeScreenOff = false
  private val mScreenOnOffReceiver: BroadcastReceiver = object : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
      when (intent.action) {
        Intent.ACTION_SCREEN_ON -> onScreenOn()
        Intent.ACTION_SCREEN_OFF -> onScreenOff()
      }
    }
  }

  override fun onCreate() {
    super.onCreate()

    // Update Unity version info
    updateUnityVersionIfNeeded(this)
    logStartupMessage()
    v("UnityWallpaperService.onCreate")

    // Attach the {@code StandardExceptionHandler} to get better exception logs.
    StandardExceptionHandler.instance.attach()

    // Register the screen on/off broadcast listener
    val filter = IntentFilter()
    filter.addAction(Intent.ACTION_SCREEN_ON)
    filter.addAction(Intent.ACTION_SCREEN_OFF)
    registerReceiver(mScreenOnOffReceiver, filter)
    if (mUnityPlayerWrapper == null) {
      mUnityPlayerWrapper = UnityPlayerWrapper(this)
    }
  }

  override fun onDestroy() {
    d("UnityWallpaperService.onDestroy")
    unregisterReceiver(mScreenOnOffReceiver)

    // {@code UnityPlayer} is destroyed together with {@code UnityWallpaperService}.
    // The side effect is that not only {@code UnityWallpaperService} is destroyed,
    // but the whole application process dies.
    val unityPlayerWrapper = UnityPlayerInstanceManager.instance.unityPlayerWrapperInstance
    if (unityPlayerWrapper != null && UnityPlayerInstanceManager.instance.unityPlayerPauseResumeManager.unityPlayerHoldersCount == 0) {
      unityPlayerWrapper.quitPlayer()
    }
  }

  override fun onCreateEngine(): Engine {
    // Even though there can only be a single instance of {@code UnityPlayer},
    // there can be multiple {@code UnityWallpaperEngine} active at same time,
    // for example, when in preview mode.
    return UnityWallpaperEngine()
  }

  /**
   * @return Whether screen on and off events should be accounted for resuming and pausing the Unity Player.
   * Override this to returns false if you run into issues.
   */
  protected fun useScreenOnOffAsVisibilityChangedWorkaround(): Boolean {
    return true
  }

  /**
   * @return Whether to destroy the live wallpaper service if all wallpaper engines got destroyed.
   * For some reason, when Preview screen is started first, the wallpaper service is not destroyed automatically.
   * Override this to return false if you run into issues.
   */
  protected fun useServiceDestroyWorkaround(): Boolean {
    return true
  }

  /**
   * Called when device screen is turned on.
   */
  private fun onScreenOn() {
    if (!useScreenOnOffAsVisibilityChangedWorkaround()) return
    v("UnityWallpaperService: SCREEN_ON")
    val isWallpaperVisibleBeforeScreenOff = mIsWallpaperVisibleBeforeScreenOff
    mIsWallpaperVisibleBeforeScreenOff = false

    // Send fake onVisibilityChanged(true) event to current wallpaper engine, if needed
    if (!isWallpaperVisibleBeforeScreenOff) return
    d("Wallpaper was visible before screen off, sending onVisibilityChanged(true)")
    if (mLiveWallpaperUnityFacade == null) return
    val activeUnityPlayerHolder =
      mUnityPlayerInstanceManager!!.activeUnityPlayerHolder ?: return
    val activeWallpaperEngine = mLiveWallpaperUnityFacade!!.activeWallpaperEngine
    if (activeWallpaperEngine == null || activeWallpaperEngine.unityPlayerHolder != activeUnityPlayerHolder) return
    activeWallpaperEngine.onVisibilityChanged(true)
  }

  /**
   * Called when device screen is turned off.
   */
  private fun onScreenOff() {
    if (!useScreenOnOffAsVisibilityChangedWorkaround() || mUnityPlayerInstanceManager!!.unityPlayerPauseResumeManager.unityPlayerHoldersCount == 0) return
    v("UnityWallpaperService: SCREEN_OFF")

    // Send fake onVisibilityChanged(false) event to current wallpaper engine, if needed
    mIsWallpaperVisibleBeforeScreenOff = false
    if (mLiveWallpaperUnityFacade == null) return
    if (mUnityPlayerInstanceManager!!.unityPlayerPauseResumeManager.visibleUnityPlayerHoldersCount > 0) {
      mIsWallpaperVisibleBeforeScreenOff = true
      val activeUnityPlayerHolder =
        mUnityPlayerInstanceManager!!.activeUnityPlayerHolder ?: return
      val activeWallpaperEngine = mLiveWallpaperUnityFacade!!.activeWallpaperEngine
      if (activeWallpaperEngine == null || activeWallpaperEngine.unityPlayerHolder != activeUnityPlayerHolder) return
      d("Screen off, sending onVisibilityChanged(false)")
      activeWallpaperEngine.onVisibilityChanged(false)
    } else {
      v(
        "Screen off, but visibleUnityPlayerHoldersCount == 0, not sending onVisibilityChanged(false)",
      )
    }
  }

  /**
   * Wallpaper engine that uses Unity.
   */
  inner class UnityWallpaperEngine : Engine(), IMultiTapDetectedListener {
    var unityPlayerHolder: UnityPlayerHolder? = null

    /**
     * Last known `SurfaceHolder` provided to this engine.
     */
    private var mCurrentSurfaceHolder: SurfaceHolder? = null

    init {
      val unityPlayerWrapperInstantiator: UnityPlayerWrapperInstantiator =
        object : UnityPlayerWrapperInstantiator(this@UnityWallpaperService) {
          public override fun isVisible(): Boolean {
            return this@UnityWallpaperEngine.isVisible
          }

          public override fun getSurfaceHolder(): SurfaceHolder? {
            return mCurrentSurfaceHolder
          }

          public override fun onAlreadyInstantiated() {
            mUnityPlayerInstanceManager = UnityPlayerInstanceManager.instance
            mLiveWallpaperUnityFacade =
              LiveWallpaperUnityFacade.instance
          }

          public override fun onUnityPlayerInstanceCreated() {
            onAlreadyInstantiated()
            mLiveWallpaperUnityFacade!!.activeWallpaperEngine =
              this@UnityWallpaperEngine
          }

          public override fun onSetUnityPlayerWrapper(unityPlayerWrapper: UnityPlayerWrapper) {
            mUnityPlayerWrapper = unityPlayerWrapper
          }

          public override fun onSetUnityPlayerHolder(unityPlayerHolder: UnityPlayerHolder) {
            this@UnityWallpaperEngine.unityPlayerHolder = unityPlayerHolder
          }
        }
      unityPlayerWrapperInstantiator.instantiate()

      // Enable event listening
      setTouchEventsEnabled(true)
      setOffsetNotificationsEnabled(true)
    }

    override fun onCreate(surfaceHolder: SurfaceHolder) {
      super.onCreate(surfaceHolder)
      log("UnityWallpaperEngine.onCreate")
    }

    override fun onDestroy() {
      super.onDestroy()
      log("UnityWallpaperEngine.onDestroy")

      // Clear the current engine reference
      if (mLiveWallpaperUnityFacade != null) {
        if (mLiveWallpaperUnityFacade!!.activeWallpaperEngine === this) {
          mLiveWallpaperUnityFacade!!.activeWallpaperEngine = null
        }
        if (mUnityPlayerInstanceManager!!.activeUnityPlayerHolder == unityPlayerHolder) {
          mUnityPlayerInstanceManager!!.activeUnityPlayerHolder = null
        }
      }
      unityPlayerHolder!!.unregister()
      if (useServiceDestroyWorkaround()) {
        val unityPlayerHoldersCount =
          mUnityPlayerInstanceManager!!.unityPlayerPauseResumeManager.unityPlayerHoldersCount
        log("unityPlayerHoldersCount == $unityPlayerHoldersCount", true)
        if (unityPlayerHoldersCount == 0) {
          log("unityPlayerHoldersCount == 0, stopping service", true)
          this@UnityWallpaperService.stopSelf()
        }
      }
    }

    override fun onTouchEvent(motionEvent: MotionEvent) {
      if (mUnityPlayerWrapper == null) return
      if (mLiveWallpaperUnityFacade != null) {
        mLiveWallpaperUnityFacade!!.multiTapDetector.onTouchEvent(motionEvent)
      }

      // Forward touch events to Unity
      mUnityPlayerWrapper!!.injectInputEvent(motionEvent)
    }

    override fun onOffsetsChanged(
      xOffset: Float,
      yOffset: Float,
      xOffsetStep: Float,
      yOffsetStep: Float,
      xPixelOffset: Int,
      yPixelOffset: Int,
    ) {
      // Pass event to C#
      mUnityEventsProxy.offsetsChanged(
        xOffset,
        yOffset,
        xOffsetStep,
        yOffsetStep,
        xPixelOffset,
        yPixelOffset,
      )
    }

    override fun onDesiredSizeChanged(desiredWidth: Int, desiredHeight: Int) {
      // Pass event to C#
      if (mLiveWallpaperUnityFacade == null || mLiveWallpaperUnityFacade?.activeWallpaperEngine === this) {
        mUnityEventsProxy.desiredSizeChanged(desiredWidth, desiredHeight)
      }
    }

    override fun onVisibilityChanged(visible: Boolean) {
      super.onVisibilityChanged(visible)
      log("UnityWallpaperEngine.onVisibilityChanged: $visible")
      if (visible) {
        // Setup
        if (mLiveWallpaperUnityFacade != null) {
          mLiveWallpaperUnityFacade!!.activeWallpaperEngine = this
          mUnityPlayerInstanceManager!!.activeUnityPlayerHolder = unityPlayerHolder
          mLiveWallpaperUnityFacade!!.multiTapDetector.registerMultiTapDetectedListener(this)
          mLiveWallpaperUnityFacade!!.multiTapDetector.screenSize = Point(
            desiredMinimumWidth, desiredMinimumHeight,
          )
        }
        unityPlayerHolder!!.onVisibilityChanged(true, mCurrentSurfaceHolder)

        // Send events to C#
        mUnityEventsProxy.desiredSizeChanged(desiredMinimumWidth, desiredMinimumHeight)
        mUnityEventsProxy.visibilityChanged(true)
        mUnityEventsProxy.isPreviewChanged(isPreview)
      } else {
        if (mLiveWallpaperUnityFacade != null) {
          mLiveWallpaperUnityFacade!!.multiTapDetector.unregisterMultiTapDetectedListener(
            this,
          )
        }

        // Send events to C#.
        // Do this before Unity was paused to avoid them being received in paused state
        if (mLiveWallpaperUnityFacade == null || mUnityPlayerInstanceManager!!.activeUnityPlayerHolder == unityPlayerHolder) {
          mUnityEventsProxy.visibilityChanged(false)
        }
        unityPlayerHolder!!.onVisibilityChanged(false, null)
      }
    }

    override fun onSurfaceChanged(
      surfaceHolder: SurfaceHolder,
      format: Int,
      width: Int,
      height: Int,
    ) {
      super.onSurfaceChanged(surfaceHolder, format, width, height)
      log("UnityWallpaperEngine.onSurfaceChanged", true)

      // Save reference to the current SurfaceHolder
      mCurrentSurfaceHolder = surfaceHolder
      if (mUnityPlayerWrapper == null) return

      // Update the UnityPlayer surface
      mUnityPlayerWrapper!!.setPlayerSurface(surfaceHolder)
    }

    override fun onSurfaceCreated(surfaceHolder: SurfaceHolder) {
      super.onSurfaceCreated(surfaceHolder)
      log("UnityWallpaperEngine.onSurfaceCreated", true)

      // Save reference to the current SurfaceHolder
      mCurrentSurfaceHolder = surfaceHolder
    }

    override fun onSurfaceDestroyed(surfaceHolder: SurfaceHolder) {
      super.onSurfaceDestroyed(surfaceHolder)
      log("UnityWallpaperEngine.onSurfaceDestroyed", true)
      if (mUnityPlayerWrapper == null) return
      mUnityPlayerWrapper!!.handleSurfaceDestroyed(surfaceHolder)
    }

    override fun onMultiTapDetected(finalTapPositionX: Float, finalTapPositionY: Float) {
      mUnityEventsProxy.multiTapDetected(finalTapPositionX, finalTapPositionY)
    }

    private fun log(obj: Any?, isVerbose: Boolean = false) {
      var message: String? = ""
      message += if (isPreview) "preview" else "wallpaper"
      if (BuildConfig.DEBUG) {
        message += "@"
        message += Integer.toHexString(hashCode())
      }
      message += ": "
      message += obj?.toString() ?: "null"
      if (isVerbose) {
        v(message)
      } else {
        d(message)
      }
    }
  }
}
