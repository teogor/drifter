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

package dev.teogor.drifter.compose.components

import android.annotation.SuppressLint
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Point
import android.os.Handler
import android.util.AttributeSet
import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.ViewGroup
import android.widget.FrameLayout
import dev.teogor.drifter.integration.core.UnityDispatcher
import dev.teogor.drifter.integration.player.UnityPlayerHolder
import dev.teogor.drifter.integration.player.UnityPlayerInstanceManager
import dev.teogor.drifter.integration.player.UnityPlayerWrapper
import dev.teogor.drifter.integration.player.UnityPlayerWrapperInstantiator
import dev.teogor.drifter.integration.utilities.MultiTapDetector
import dev.teogor.drifter.integration.utilities.asVector2
import dev.teogor.drifter.wallpaper.LiveWallpaperUnityFacade
import java.util.concurrent.Executors

private const val TAG = "UnityPlayerView"

class UnityPlayerView : FrameLayout {
  constructor(context: Context) : super(context)

  constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

  constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
    context,
    attrs,
    defStyleAttr,
  )

  private val UNITY_EVENT_PLAYER_RESUMED = "UnityPlayerResumed"
  private val UNITY_EVENT_PLAYER_PAUSED = "UnityPlayerPaused"
  private val UNITY_EVENT_ACTIVITY_ONSTART = "UnityActivityOnStart"
  private val UNITY_EVENT_ACTIVITY_ONRESUME = "UnityActivityOnResume"
  private val UNITY_EVENT_ACTIVITY_ONPAUSE = "UnityActivityOnPause"
  private val UNITY_EVENT_ACTIVITY_ONSTOP = "UnityActivityOnStop"

  private val execPlayer = Executors.newFixedThreadPool(1)
  private var runnable: Runnable? = null
  private val mainLooper = Handler(context.mainLooper)

  private var mSurfaceView: SurfaceView? = null
  private var mSurfaceHolder: SurfaceHolder? = null
  private var mUnityPlayerHolder: UnityPlayerHolder? = null
  private var mIsSurfaceCreated = false
  private var mIsVisible = false
  private var mLiveWallpaperUnityFacade: LiveWallpaperUnityFacade? =
    null
  private var mUnityPlayerInstanceManager: UnityPlayerInstanceManager? = null
  private var mUnityPlayerWrapper: UnityPlayerWrapper? = null
  private val mSurfaceHolderCallbackReceiver = UnityPlayerSurfaceHolderCallbackReceiver()
  private var requiresLoading = true
  private val mUnityEventsProxy = LiveWallpaperUnityFacade.eventsProxy
  private val mMultiTapDetectedListener =
    MultiTapDetector.IMultiTapDetectedListener { finalTapPositionX: Float, finalTapPositionY: Float ->
      mUnityEventsProxy.multiTapDetected(
        finalTapPositionX,
        finalTapPositionY,
      )
    }

  fun loadPlayer(
    runnable: Runnable? = null,
    onUnityEngineCreated: () -> Unit,
  ) {
    this.runnable = runnable

    execPlayer.execute {
      mainLooper.postDelayed(
        {
          mSurfaceView = onCreateLayout()
          mSurfaceHolder = mSurfaceView?.holder
          mSurfaceHolder?.addCallback(mSurfaceHolderCallbackReceiver)

          // Instantiate the Unity player
          val unityPlayerWrapperInstantiator: UnityPlayerWrapperInstantiator =
            object : UnityPlayerWrapperInstantiator(context as ContextWrapper) {
              override fun isVisible(): Boolean {
                Log.d(TAG, "isVisible")
                return mIsVisible
              }

              override fun getSurfaceHolder(): SurfaceHolder? {
                Log.d(TAG, "getSurfaceHolder")
                return mSurfaceHolder
              }

              override fun onAlreadyInstantiated() {
                Log.d(TAG, "onAlreadyInstantiated")
                mUnityPlayerInstanceManager = UnityPlayerInstanceManager.instance
                mLiveWallpaperUnityFacade =
                  LiveWallpaperUnityFacade.instance
                resumeUnityPlayer()
                onUnityEngineCreated()
              }

              override fun onUnityPlayerInstanceCreated() {
                Log.d(TAG, "onUnityPlayerInstanceCreated")
                onAlreadyInstantiated()
              }

              override fun onSetUnityPlayerWrapper(unityPlayerWrapper: UnityPlayerWrapper) {
                Log.d(TAG, "onSetUnityPlayerWrapper")
                mUnityPlayerWrapper = unityPlayerWrapper
              }

              override fun onSetUnityPlayerHolder(unityPlayerHolder: UnityPlayerHolder) {
                Log.d(TAG, "onSetUnityPlayerHolder")
                mUnityPlayerHolder = unityPlayerHolder
              }
            }
          Log.d(TAG, "instantiate")
          unityPlayerWrapperInstantiator.instantiate()
        },
        0L,
      )
    }
  }

  private fun onCreateLayout(): SurfaceView {
    removeAllViews()
    // Create SurfaceView for Unity
    val unitySurfaceView = SurfaceView(context)
    val surfaceViewLayoutParams: ViewGroup.LayoutParams = LayoutParams(
      LayoutParams.MATCH_PARENT,
      LayoutParams.MATCH_PARENT,
    )
    unitySurfaceView.layoutParams = surfaceViewLayoutParams

    // Add SurfaceView as child of LinearLayout
    addView(unitySurfaceView)
    return unitySurfaceView
  }

  private inner class UnityPlayerSurfaceHolderCallbackReceiver : SurfaceHolder.Callback {
    override fun surfaceCreated(surfaceHolder: SurfaceHolder) {
      Log.d(TAG, "surfaceCreated")
      mSurfaceHolder = surfaceHolder
      mIsSurfaceCreated = true
      if (mUnityPlayerWrapper == null) return
      resumeUnityPlayer()
      if (requiresLoading && runnable != null) {
        post(runnable)
        requiresLoading = false
      }
    }

    override fun surfaceChanged(
      surfaceHolder: SurfaceHolder,
      format: Int,
      width: Int,
      height: Int,
    ) {
      Log.d(TAG, "surfaceChanged")
      mSurfaceHolder = surfaceHolder
      if (mUnityPlayerWrapper == null) return

      // Update Unity player surface
      if (mUnityPlayerInstanceManager?.activeUnityPlayerHolder === mUnityPlayerHolder) {
        mUnityPlayerWrapper?.setPlayerSurface(mSurfaceHolder)
      }
    }

    override fun surfaceDestroyed(surfaceHolder: SurfaceHolder) {
      Log.d(TAG, "surfaceDestroyed")
      mIsSurfaceCreated = false
      if (mUnityPlayerWrapper == null) return
      pauseUnityPlayer()
      mUnityPlayerWrapper?.handleSurfaceDestroyed(surfaceHolder)
    }
  }

  fun resumeUnityPlayer(): Boolean {
    if (!mIsVisible || !mIsSurfaceCreated) return false
    mUnityPlayerInstanceManager!!.activeUnityPlayerHolder = mUnityPlayerHolder
    val isAppliedChanges = mUnityPlayerHolder!!.onVisibilityChanged(true, mSurfaceHolder)
    if (isAppliedChanges) {
      // Set Unity player Context to this Activity to allow soft input
      dev.teogor.drifter.wallpaper.activities.LiveWallpaperCompatibleUnityPlayerActivity.Companion.updateUnityPlayerActivityContext()
      val width = mSurfaceHolder!!.surfaceFrame.width()
      val height = mSurfaceHolder!!.surfaceFrame.height()

      // Setup MultiTapDetector
      if (mLiveWallpaperUnityFacade != null) {
        mLiveWallpaperUnityFacade!!.multiTapDetector.registerMultiTapDetectedListener(
          mMultiTapDetectedListener,
        )
        mLiveWallpaperUnityFacade!!.multiTapDetector.screenSize =
          Point(width, height)
      }

      // Send events to C#
      mUnityEventsProxy.desiredSizeChanged(width, height)
      mUnityEventsProxy.visibilityChanged(true)
      mUnityEventsProxy.isPreviewChanged(false)
      mUnityEventsProxy.customEventReceived(UNITY_EVENT_PLAYER_RESUMED, "")
    }
    return isAppliedChanges
  }

  fun pauseUnityPlayer(): Boolean {
    requiresLoading = true
    val visibleUnityPlayerHoldersCount =
      UnityPlayerInstanceManager.instance.unityPlayerPauseResumeManager.visibleUnityPlayerHoldersCount
    val isAppliedChanges = visibleUnityPlayerHoldersCount > 0 && mUnityPlayerHolder!!.isVisible
    if (mUnityPlayerInstanceManager!!.activeUnityPlayerHolder === mUnityPlayerHolder) {
      // Send events to C#
      if (isAppliedChanges) {
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

  private enum class UnityPlayerResumeEventType {
    OnActivityStart,
    OnActivityResume,
  }

  private enum class UnityPlayerPauseEventType {
    OnActivityStop,
    OnActivityPause,
  }

  private fun getUnityPlayerResumeEvent(): UnityPlayerResumeEventType {
    return UnityPlayerResumeEventType.OnActivityStart
  }

  private fun getUnityPlayerPauseEvent(): UnityPlayerPauseEventType {
    return UnityPlayerPauseEventType.OnActivityStop
  }

  fun onStart() {
    if (mUnityPlayerWrapper != null) {
      mUnityEventsProxy.customEventReceived(UNITY_EVENT_ACTIVITY_ONSTART, "")
    }
    if (getUnityPlayerResumeEvent() != UnityPlayerResumeEventType.OnActivityStart) return
    Log.d(TAG, "onStart")
    mIsVisible = true
    if (mUnityPlayerWrapper == null) return
    resumeUnityPlayer()
  }

  fun onResume() {
    if (mUnityPlayerWrapper != null) {
      mUnityEventsProxy.customEventReceived(UNITY_EVENT_ACTIVITY_ONRESUME, "")
    }
    if (getUnityPlayerResumeEvent() != UnityPlayerResumeEventType.OnActivityResume) return
    Log.d(TAG, "onResume")
    mIsVisible = true
    if (mUnityPlayerWrapper == null) return
    resumeUnityPlayer()
  }

  fun onPause() {
    if (mUnityPlayerWrapper != null) {
      mUnityEventsProxy.customEventReceived(UNITY_EVENT_ACTIVITY_ONPAUSE, "")
    }
    if (getUnityPlayerPauseEvent() != UnityPlayerPauseEventType.OnActivityPause) return
    Log.d(TAG, "onPause")
    mIsVisible = false
    if (mUnityPlayerWrapper == null) return
    pauseUnityPlayer()
  }

  fun onStop() {
    if (mUnityPlayerWrapper != null) {
      mUnityEventsProxy.customEventReceived(UNITY_EVENT_ACTIVITY_ONSTOP, "")
    }
    if (getUnityPlayerPauseEvent() != UnityPlayerPauseEventType.OnActivityStop) return
    Log.d(TAG, "onStop")
    mIsVisible = false
    if (mUnityPlayerWrapper == null) return
    pauseUnityPlayer()
  }

  fun onDestroy() {
    Log.d(TAG, "onDestroy")
    // updateUnityPlayerActivityContext()

    // Only unregister on when Activity is destroyed, as we want the Unity process to killed
    // when Activity is destroyed, not just hidden. This allows to resume when user puts the Activity to background.
    if (mUnityPlayerHolder != null) {
      mUnityPlayerHolder!!.unregister()
    }
    // if (UnityPlayerWrapper.getUnityPlayerCurrentActivity() === ApplicationManager.getActivity()) {
    //     UnityPlayerWrapper.setUnityPlayerCurrentActivity(null)
    // }

    // {@code UnityPlayer} is destroyed together with {@code LiveWallpaperCompatibleUnityPlayerActivity}.
    // The side effect is that not only {@code LiveWallpaperCompatibleUnityPlayerActivity} is destroyed,
    // but the whole application process dies.
    if (UnityPlayerInstanceManager.instance.unityPlayerWrapperInstance != null &&
      UnityPlayerInstanceManager.instance.unityPlayerPauseResumeManager.unityPlayerHoldersCount == 0
    ) {
      pauseUnityPlayer()
      UnityPlayerInstanceManager.instance.unityPlayerWrapperInstance!!.unityPlayer.unload()
    }
  }

  // Pass any events not handled by (unfocused) views straight to UnityPlayer
  override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
    Log.d(TAG, "onKeyUp")
    return mUnityPlayerWrapper?.unityPlayer?.injectEvent(event) ?: false
  }

  override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
    Log.d(TAG, "onKeyDown")
    return mUnityPlayerWrapper?.unityPlayer?.injectEvent(event) ?: false
  }

  @SuppressLint("ClickableViewAccessibility")
  override fun onTouchEvent(event: MotionEvent): Boolean {
    if (mIsSurfaceCreated) {
      mLiveWallpaperUnityFacade?.multiTapDetector?.onTouchEvent(event)
      mUnityPlayerWrapper?.unityPlayer?.injectEvent(event)
      injectTouchEvent(event)
    }
    when (event.action) {
      MotionEvent.ACTION_DOWN -> {
        return true
      }

      MotionEvent.ACTION_MOVE -> {
        return true
      }

      MotionEvent.ACTION_UP -> {
        return true
      }
    }
    return super.onTouchEvent(event)
  }

  private fun injectTouchEvent(event: MotionEvent) {
    UnityDispatcher(
      gameObject = "AndroidHandler",
      functionName = "Receiver_OnClick",
      functionParameter = event.asVector2,
    )
  }

  override fun onGenericMotionEvent(event: MotionEvent): Boolean {
    Log.d(TAG, "onGenericMotionEvent")
    return mUnityPlayerWrapper!!.unityPlayer.injectEvent(event)
  }
}
