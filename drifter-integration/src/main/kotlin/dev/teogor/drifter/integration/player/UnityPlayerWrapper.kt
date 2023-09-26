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

package dev.teogor.drifter.integration.player

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration
import android.view.InputEvent
import android.view.Surface
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.unity3d.player.UnityPlayer
import dev.teogor.drifter.integration.initializer.ActivityContextProvider.currentActivity
import dev.teogor.drifter.integration.utilities.DebugLog.d
import dev.teogor.drifter.integration.utilities.DebugLog.v
import dev.teogor.drifter.integration.utilities.UnityVersionInfo.Companion.updateUnityVersionIfNeeded
import java.lang.reflect.Field
import java.util.Locale

/**
 * Provides safe and convenient access to `UnityPlayer`.
 */
class UnityPlayerWrapper(contextWrapper: ContextWrapper) {
  /**
   * Returns the Application `Context`.
   *
   * @return Application `Context`.
   */
  val applicationContext: Context
  private val mUnityPlayer: UnityPlayer
  private var mUnityPlayerContextWrapperField: Field? = null

  /**
   * Current `SurfaceHolder` used by `UnityPlayer`.
   */
  private var mSurfaceHolder: SurfaceHolder? = null

  init {
    updateUnityVersionIfNeeded(contextWrapper)
    applicationContext = contextWrapper.applicationContext
    mUnityPlayer = UnityPlayer(contextWrapper)
    setupUnityPlayerInnerState(contextWrapper)
  }

  /**
   * Resumes the `UnityPlayer`
   */
  fun resumePlayer() {
    d("UnityPlayerWrapper: Resuming")
    mUnityPlayer.resume()
    mUnityPlayer.windowFocusChanged(true)
  }

  val unityPlayer: UnityPlayer
    get() {
      UnityPlayer.currentActivity = currentActivity
      return mUnityPlayer
    }

  /**
   * Pauses the `UnityPlayer`.
   */
  fun pausePlayer() {
    d("UnityPlayerWrapper: Pausing")
    mUnityPlayer.windowFocusChanged(false)
    mUnityPlayer.pause()
  }

  /**
   * Shuts down the `UnityPlayer`.
   * Note: this will kill the application process entirely.
   */
  fun quitPlayer() {
    d("UnityPlayerWrapper: Shutting down UnityPlayer")
    mUnityPlayer.quit()
  }

  /**
   * Handles the destruction of `UnityWallpaperEngine`-owned `SurfaceHolder`.
   *
   * @param surfaceHolder `UnityWallpaperEngine`-owned `SurfaceHolder` that was destroyed.
   */
  fun handleSurfaceDestroyed(surfaceHolder: SurfaceHolder?) {
    // We don't care about destruction of other surfaces except the one currently in use
    if (mSurfaceHolder !== surfaceHolder) return
    v(
      String.format(
        Locale.ENGLISH,
        "UnityPlayerWrapper: Handling SurfaceHolder (w: %1d, h: %2d) destruction",
        surfaceHolder?.surfaceFrame?.width() ?: -1,
        surfaceHolder?.surfaceFrame?.height() ?: -1,
      ),
    )
    setPlayerSurface(null)
  }

  /**
   * Sets the primary rendering surface of `UnityPlayer`.
   *
   * @param surfaceHolder `SurfaceHolder` to set as primary surface. Set to null to detach the primary surface.
   */
  fun setPlayerSurface(surfaceHolder: SurfaceHolder?) {
    v(
      String.format(
        Locale.ENGLISH,
        "UnityPlayerWrapper: Updating Unity player surface (w: %1d, h: %2d)",
        surfaceHolder?.surfaceFrame?.width() ?: -1,
        surfaceHolder?.surfaceFrame?.height() ?: -1,
      ),
    )
    mSurfaceHolder = surfaceHolder
    setPlayerSurfaceDirect(surfaceHolder?.surface)
  }

  /**
   * Forwards touch events to Unity.
   *
   * @param inputEvent
   */
  fun injectInputEvent(inputEvent: InputEvent?): Boolean {
    return mUnityPlayer.injectEvent(inputEvent)
  }

  /**
   * Forwards configuration changes to Unity.
   *
   * @param configuration
   */
  fun injectConfigurationChange(configuration: Configuration?) {
    mUnityPlayer.configurationChanged(configuration)
  }

  /**
   * Sets internal `UnityPlayer` reference to `ContextWrapper`,
   * used for soft input and some other stuff.
   *
   * @param contextWrapper
   */
  fun setContext(contextWrapper: ContextWrapper?) {
    if (mUnityPlayerContextWrapperField == null) {
      return
    }
    try {
      if (contextWrapper == null) {
        mUnityPlayerContextWrapperField!![mUnityPlayer] = applicationContext
      } else {
        mUnityPlayerContextWrapperField!![mUnityPlayer] = contextWrapper
      }
    } catch (e: Throwable) {
      e.printStackTrace()
    }
  }

  private fun setPlayerSurfaceDirect(surface: Surface?) {
    mUnityPlayer.displayChanged(0, surface)
  }

  /**
   * Processes UnityPlayer instance to make it behave like if it actually wasn't launched from an Activity.
   *
   * @param contextWrapper
   */
  private fun setupUnityPlayerInnerState(contextWrapper: ContextWrapper) {
    // Completely detach Unity from Activity
    val playerFields = UnityPlayer::class.java.declaredFields
    for (field in playerFields) {
      // Unregister SurfaceView callbacks
      when (field.type) {
        SurfaceView::class.java -> {
          try {
            field.isAccessible = true
            val surfaceView = field[mUnityPlayer]
            val callbacksField = surfaceView.javaClass.getDeclaredField("mCallbacks")
            callbacksField.isAccessible = true
            val callbacks = callbacksField[surfaceView] as MutableList<*>
            callbacks.clear()
          } catch (e: Throwable) {
            e.printStackTrace()
          }
        }
        BroadcastReceiver::class.java -> {
          try {
            field.isAccessible = true
            val shutdownReceiver = field[mUnityPlayer] as? BroadcastReceiver
            contextWrapper.unregisterReceiver(shutdownReceiver)
            field[mUnityPlayer] = null
          } catch (e: IllegalArgumentException) {
            if (e.message != null) {
              if (!e.message!!.contains("Receiver not registered")) {
                e.printStackTrace()
              }
            }
          } catch (e: Throwable) {
            e.printStackTrace()
          }
        }
        ContextWrapper::class.java -> {
          field.isAccessible = true
          mUnityPlayerContextWrapperField = field
          setContext(null)
        }
      }
    }
  }

  companion object {
    var unityPlayerCurrentActivity: Activity?
      get() = UnityPlayer.currentActivity
      set(activity) {
        UnityPlayer.currentActivity = activity
      }
  }
}
