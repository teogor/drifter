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

package dev.teogor.drifter.wallpaper.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.res.Configuration
import android.graphics.PixelFormat
import android.graphics.Point
import android.os.Bundle
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.ViewGroup
import android.widget.LinearLayout
import dev.teogor.drifter.integration.player.UnityPlayerHolder
import dev.teogor.drifter.integration.player.UnityPlayerInstanceManager
import dev.teogor.drifter.integration.player.UnityPlayerWrapper
import dev.teogor.drifter.integration.player.UnityPlayerWrapperInstantiator
import dev.teogor.drifter.integration.utilities.DebugLog
import dev.teogor.drifter.integration.utilities.MultiTapDetector.IMultiTapDetectedListener
import dev.teogor.drifter.integration.utilities.UnityVersionInfo

/**
 * A Unity player Activity that shares the Unity player instance with the live wallpaper service.
 */
abstract class LiveWallpaperCompatibleUnityPlayerActivity : Activity() {
  private val mUnityEventsProxy = dev.teogor.drifter.wallpaper.LiveWallpaperUnityFacade.eventsProxy
  private val mMultiTapDetectedListener =
    IMultiTapDetectedListener { finalTapPositionX, finalTapPositionY ->
      mUnityEventsProxy.multiTapDetected(
        finalTapPositionX,
        finalTapPositionY,
      )
    }
  private val mSurfaceHolderCallbackReceiver: UnityPlayerSurfaceHolderCallbackReceiver =
    UnityPlayerSurfaceHolderCallbackReceiver()

  /**
   * `SurfaceView` for Unity player rendering.
   */
  protected var mSurfaceView: SurfaceView? = null

  /**
   * `SurfaceHolder` that contains the `Surface` used for Unity player rendering.
   */
  protected var mSurfaceHolder: SurfaceHolder? = null
  protected var mUnityPlayerHolder: UnityPlayerHolder? = null

  /**
   * Whether `mSurfaceHolder` currently holds a valid `Surface`.
   */
  private var mIsSurfaceCreated = false

  /**
   * Whether the Activity is currently visible.
   */
  private var mIsVisible = false
  var mUnityPlayerWrapper: UnityPlayerWrapper? = null
    private set
  private var mLiveWallpaperUnityFacade: dev.teogor.drifter.wallpaper.LiveWallpaperUnityFacade? =
    null
  private var mUnityPlayerInstanceManager: UnityPlayerInstanceManager? = null
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    // Update Unity version info
    UnityVersionInfo.updateUnityVersionIfNeeded(this)
    DebugLog.logStartupMessage()

    // Create the Layout for this Activity
    mSurfaceView = onCreateLayout()

    // This makes xperia play happy
    window.setFormat(PixelFormat.RGBX_8888)

    // Get the surface holder. At this point, it should point at null Surface.
    mSurfaceHolder = mSurfaceView!!.holder

    // Install a SurfaceHolder.Callback so we get notified when the
    // underlying surface is created and destroyed.
    mSurfaceHolder!!.addCallback(mSurfaceHolderCallbackReceiver)

    // Instantiate the Unity player
    val unityPlayerWrapperInstantiator: UnityPlayerWrapperInstantiator =
      object : UnityPlayerWrapperInstantiator(this) {
        public override fun isVisible(): Boolean {
          return mIsVisible
        }

        public override fun getSurfaceHolder(): SurfaceHolder {
          return mSurfaceHolder!!
        }

        public override fun onAlreadyInstantiated() {
          mUnityPlayerInstanceManager = UnityPlayerInstanceManager.instance
          mLiveWallpaperUnityFacade = dev.teogor.drifter.wallpaper.LiveWallpaperUnityFacade.instance
        }

        public override fun onUnityPlayerInstanceCreated() {
          onAlreadyInstantiated()
        }

        public override fun onSetUnityPlayerWrapper(unityPlayerWrapper: UnityPlayerWrapper) {
          mUnityPlayerWrapper = unityPlayerWrapper
        }

        public override fun onSetUnityPlayerHolder(unityPlayerHolder: UnityPlayerHolder) {
          mUnityPlayerHolder = unityPlayerHolder
        }
      }
    unityPlayerWrapperInstantiator.instantiate()
  }

  override fun onStart() {
    super.onStart()
    if (mUnityPlayerWrapper != null) {
      mUnityEventsProxy.customEventReceived(UNITY_EVENT_ACTIVITY_ONSTART, "")
    }
    if (unityPlayerResumeEvent != UnityPlayerResumeEventType.OnActivityStart) return
    DebugLog.v("LiveWallpaperCompatibleUnityPlayerActivity: onStart")
    mIsVisible = true
    if (mUnityPlayerWrapper == null) return
    resumeUnityPlayer()
  }

  override fun onResume() {
    super.onResume()
    if (mUnityPlayerWrapper != null) {
      mUnityEventsProxy.customEventReceived(UNITY_EVENT_ACTIVITY_ONRESUME, "")
    }
    if (unityPlayerResumeEvent != UnityPlayerResumeEventType.OnActivityResume) return
    DebugLog.v("LiveWallpaperCompatibleUnityPlayerActivity: onResume")
    mIsVisible = true
    if (mUnityPlayerWrapper == null) return
    resumeUnityPlayer()
  }

  override fun onPause() {
    super.onPause()
    if (mUnityPlayerWrapper != null) {
      mUnityEventsProxy.customEventReceived(UNITY_EVENT_ACTIVITY_ONPAUSE, "")
    }
    if (unityPlayerPauseEvent != UnityPlayerPauseEventType.OnActivityPause) return
    DebugLog.v("LiveWallpaperCompatibleUnityPlayerActivity: onPause")
    mIsVisible = false
    if (mUnityPlayerWrapper == null) return
    pauseUnityPlayer()
  }

  override fun onStop() {
    super.onStop()
    if (mUnityPlayerWrapper != null) {
      mUnityEventsProxy.customEventReceived(UNITY_EVENT_ACTIVITY_ONSTOP, "")
    }
    if (unityPlayerPauseEvent != UnityPlayerPauseEventType.OnActivityStop) return
    DebugLog.v("LiveWallpaperCompatibleUnityPlayerActivity: onStop")
    mIsVisible = false
    if (mUnityPlayerWrapper == null) return
    pauseUnityPlayer()
  }

  override fun onDestroy() {
    super.onDestroy()
    DebugLog.v("LiveWallpaperCompatibleUnityPlayerActivity: onDestroy")
    sCurrentActivity = null
    updateUnityPlayerActivityContext()

    // Only unregister on when Activity is destroyed, as we want the Unity process to killed
    // when Activity is destroyed, not just hidden. This allows to resume when user puts the Activity to background.
    if (mUnityPlayerHolder != null) {
      mUnityPlayerHolder!!.unregister()
    }
    if (UnityPlayerWrapper.unityPlayerCurrentActivity === this) {
      UnityPlayerWrapper.unityPlayerCurrentActivity = null
    }

    // {@code UnityPlayer} is destroyed together with {@code LiveWallpaperCompatibleUnityPlayerActivity}.
    // The side effect is that not only {@code LiveWallpaperCompatibleUnityPlayerActivity} is destroyed,
    // but the whole application process dies.
    if (
      UnityPlayerInstanceManager.instance.unityPlayerPauseResumeManager.unityPlayerHoldersCount == 0
    ) {
      UnityPlayerInstanceManager.instance.unityPlayerWrapperInstance?.quitPlayer()
    }
  }

  override fun isFinishing(): Boolean {
    if (!super.isFinishing()) return false

    // One of the ugliest hacks ever devised by mankind.
    //
    // HACK: Unity is doing something weird, pausing the UnityMain thread if isFinishing() ever returns true.
    // This code avoids this by always returning false when called from any Unity classes.
    // Unfortunately, there doesn't seems to be a way to avoid this in any "correct" way.
    val stacktrace = Thread.currentThread().stackTrace
    for (element in stacktrace) {
      if (element.className.startsWith("com.unity3d.player")) return false
    }
    return true
  }

  // This ensures the layout will be correct.
  override fun onConfigurationChanged(newConfig: Configuration) {
    super.onConfigurationChanged(newConfig)
    if (mUnityPlayerWrapper == null) return
    mUnityPlayerWrapper!!.injectConfigurationChange(newConfig)
  }

  // For some reason the multiple keyevent type is not supported by the ndk.
  // Force event injection by overriding dispatchKeyEvent().
  override fun dispatchKeyEvent(event: KeyEvent): Boolean {
    // todo KeyEvent.ACTION_MULTIPLE deprecated
    return if (mUnityPlayerWrapper == null && event.action == KeyEvent.ACTION_MULTIPLE) {
      mUnityPlayerWrapper!!.injectInputEvent(
        event,
      )
    } else {
      super.dispatchKeyEvent(
        event,
      )
    }
  }

  // Pass any events not handled by (unfocused) views straight to UnityPlayer
  override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
    super.onKeyUp(keyCode, event)
    return if (mUnityPlayerWrapper == null) {
      false
    } else {
      mUnityPlayerWrapper!!.injectInputEvent(
        event,
      )
    }
  }

  override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
    super.onKeyDown(keyCode, event)
    return if (mUnityPlayerWrapper == null) {
      false
    } else {
      mUnityPlayerWrapper!!.injectInputEvent(
        event,
      )
    }
  }

  override fun onTouchEvent(event: MotionEvent): Boolean {
    super.onTouchEvent(event)
    if (mUnityPlayerWrapper == null) return false
    if (mLiveWallpaperUnityFacade != null) {
      mLiveWallpaperUnityFacade!!.multiTapDetector.onTouchEvent(event)
    }
    return mUnityPlayerWrapper!!.injectInputEvent(event)
  }

  /*API12*/
  override fun onGenericMotionEvent(event: MotionEvent): Boolean {
    super.onGenericMotionEvent(event)
    return if (mUnityPlayerWrapper == null) {
      false
    } else {
      mUnityPlayerWrapper!!.injectInputEvent(
        event,
      )
    }
  }

  protected val unityPlayerResumeEvent: UnityPlayerResumeEventType
    /**
     * Determines the event at which the Unity player must be resumed.
     */
    get() = UnityPlayerResumeEventType.OnActivityStart
  protected val unityPlayerPauseEvent: UnityPlayerPauseEventType
    /**
     * Determines the event at which the Unity player must be paused.
     */
    get() = UnityPlayerPauseEventType.OnActivityStop

  /**
   * Override this method to implement custom layout.
   *
   * @return The `SurfaceView` that will contain the Unity player.
   */
  protected fun onCreateLayout(): SurfaceView {
    // Create LinearLayout
    val linearLayout = LinearLayout(this)
    linearLayout.orientation = LinearLayout.VERTICAL
    val linearLayoutParams = LinearLayout.LayoutParams(
      LinearLayout.LayoutParams.MATCH_PARENT,
      LinearLayout.LayoutParams.MATCH_PARENT,
    )
    linearLayout.layoutParams = linearLayoutParams

    // Create SurfaceView for Unity
    val unitySurfaceView = SurfaceView(this)
    val surfaceViewLayoutParams: ViewGroup.LayoutParams =
      LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f)
    unitySurfaceView.layoutParams = surfaceViewLayoutParams

    // Add SurfaceView as child of LinearLayout
    linearLayout.addView(unitySurfaceView)

    // Set LinearLayout as root element of the screen
    setContentView(linearLayout)
    return unitySurfaceView
  }

  protected fun resumeUnityPlayer(): Boolean {
    if (!mIsVisible || !mIsSurfaceCreated) return false
    mUnityPlayerInstanceManager!!.activeUnityPlayerHolder = mUnityPlayerHolder
    val isAppliedChanges = mUnityPlayerHolder!!.onVisibilityChanged(true, mSurfaceHolder)
    if (isAppliedChanges) {
      // Set Unity player Context to this Activity to allow soft input
      sCurrentActivity = this
      updateUnityPlayerActivityContext()
      val width = mSurfaceHolder!!.surfaceFrame.width()
      val height = mSurfaceHolder!!.surfaceFrame.height()

      // Setup MultiTapDetector
      if (mLiveWallpaperUnityFacade != null) {
        mLiveWallpaperUnityFacade!!.multiTapDetector.registerMultiTapDetectedListener(
          mMultiTapDetectedListener,
        )
        mLiveWallpaperUnityFacade!!.multiTapDetector.screenSize = Point(width, height)
      }

      // Send events to C#
      mUnityEventsProxy.desiredSizeChanged(width, height)
      mUnityEventsProxy.visibilityChanged(true)
      mUnityEventsProxy.isPreviewChanged(false)
      mUnityEventsProxy.customEventReceived(UNITY_EVENT_PLAYER_RESUMED, "")
    }
    return isAppliedChanges
  }

  protected fun pauseUnityPlayer(): Boolean {
    val visibleUnityPlayerHoldersCount =
      UnityPlayerInstanceManager.instance.unityPlayerPauseResumeManager.visibleUnityPlayerHoldersCount
    val isAppliedChanges = visibleUnityPlayerHoldersCount > 0 && mUnityPlayerHolder!!.isVisible
    if (mUnityPlayerInstanceManager!!.activeUnityPlayerHolder === mUnityPlayerHolder) {
      // Send events to C#
      if (isAppliedChanges) {
        if (sCurrentActivity === this) {
          sCurrentActivity = null
        }
        // Unregister MultiTapDetector
        if (mLiveWallpaperUnityFacade != null) {
          mLiveWallpaperUnityFacade!!.multiTapDetector.unregisterMultiTapDetectedListener(
            mMultiTapDetectedListener,
          )
        }

        // Send events to C#
        mUnityEventsProxy.visibilityChanged(false)
        mUnityEventsProxy.customEventReceived(UNITY_EVENT_PLAYER_PAUSED, "")
      }

      // Handle pause
      mUnityPlayerHolder!!.onVisibilityChanged(false, null)
      mUnityPlayerInstanceManager!!.activeUnityPlayerHolder = null
    }
    return isAppliedChanges
  }

  protected enum class UnityPlayerResumeEventType {
    OnActivityStart,
    OnActivityResume,
  }

  protected enum class UnityPlayerPauseEventType {
    OnActivityStop,
    OnActivityPause,
  }

  private inner class UnityPlayerSurfaceHolderCallbackReceiver : SurfaceHolder.Callback {
    override fun surfaceCreated(surfaceHolder: SurfaceHolder) {
      DebugLog.v("LiveWallpaperCompatibleUnityPlayerActivity: surfaceCreated")
      mSurfaceHolder = surfaceHolder
      mIsSurfaceCreated = true
      if (mUnityPlayerWrapper == null) return
      resumeUnityPlayer()
    }

    override fun surfaceChanged(
      surfaceHolder: SurfaceHolder,
      format: Int,
      width: Int,
      height: Int,
    ) {
      DebugLog.v("LiveWallpaperCompatibleUnityPlayerActivity: surfaceChanged")
      mSurfaceHolder = surfaceHolder
      if (mUnityPlayerWrapper == null) return

      // Update Unity player surface
      if (mUnityPlayerInstanceManager!!.activeUnityPlayerHolder === mUnityPlayerHolder) {
        mUnityPlayerWrapper!!.setPlayerSurface(mSurfaceHolder)
      }
    }

    override fun surfaceDestroyed(surfaceHolder: SurfaceHolder) {
      DebugLog.v("LiveWallpaperCompatibleUnityPlayerActivity: surfaceDestroyed")
      mIsSurfaceCreated = false
      if (mUnityPlayerWrapper == null) return
      pauseUnityPlayer()
      mUnityPlayerWrapper!!.handleSurfaceDestroyed(surfaceHolder)
    }
  }

  companion object {
    /**
     * Name of the event sent when Unity player is resumed by this Activity.
     */
    const val UNITY_EVENT_PLAYER_RESUMED = "UnityPlayerResumed"

    /**
     * Name of the event sent when Unity player is paused by this Activity.
     */
    const val UNITY_EVENT_PLAYER_PAUSED = "UnityPlayerPaused"

    /**
     * Name of the event sent when Activity.onResume() is called.
     */
    const val UNITY_EVENT_ACTIVITY_ONSTART = "UnityActivityOnStart"

    /**
     * Name of the event sent when Activity.onResume() is called.
     */
    const val UNITY_EVENT_ACTIVITY_ONRESUME = "UnityActivityOnResume"

    /**
     * Name of the event sent when Activity.onPause() is called.
     */
    const val UNITY_EVENT_ACTIVITY_ONPAUSE = "UnityActivityOnPause"

    /**
     * Name of the event sent when Activity.onStop() is called.
     */
    const val UNITY_EVENT_ACTIVITY_ONSTOP = "UnityActivityOnStop"

    @SuppressLint("StaticFieldLeak")
    private var sCurrentActivity: LiveWallpaperCompatibleUnityPlayerActivity? = null

    /**
     * Updates the `Context` used by Unity player internally.
     * Note: Called from C# code.
     */
    @JvmStatic
    fun updateUnityPlayerActivityContext() {
      UnityPlayerInstanceManager.instance.unityPlayerWrapperInstance?.setContext(
        sCurrentActivity,
      )
      UnityPlayerWrapper.unityPlayerCurrentActivity = sCurrentActivity
    }
  }
}
