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
import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration
import android.view.InputEvent
import android.view.Surface
import android.view.SurfaceHolder
import dev.teogor.drifter.integration.initializer.ActivityContextProvider.currentActivity
import dev.teogor.drifter.integration.utilities.DebugLog.d
import dev.teogor.drifter.integration.utilities.DebugLog.v
import dev.teogor.drifter.integration.utilities.UnityVersionInfo.Companion.updateUnityVersionIfNeeded
import dev.teogor.drifter.unity.common.IUnityPlayer
import dev.teogor.drifter.unity.common.LocalUnityEngine
import dev.teogor.drifter.unity.common.UnityOptions
import dev.teogor.drifter.unity.common.UnityPlayerManager
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

  private val iUnityPlayer: IUnityPlayer
  private var mUnityPlayerContextWrapperField: Field? = null

  /**
   * Current `SurfaceHolder` used by `UnityPlayer`.
   */
  private var mSurfaceHolder: SurfaceHolder? = null

  init {
    updateUnityVersionIfNeeded(contextWrapper)
    applicationContext = contextWrapper.applicationContext
    iUnityPlayer = LocalUnityEngine.current.createUnityPlayer(
      contextWrapper = contextWrapper,
      options = UnityOptions().apply {
        setContextFieldConfigurator {
          mUnityPlayerContextWrapperField = it
          setContext(null)
        }
      },
    )
    UnityPlayerManager.setUnityPlayer(iUnityPlayer)
  }

  /**
   * Resumes the `UnityPlayer`
   */
  fun resumePlayer() {
    d("UnityPlayerWrapper: Resuming")
    iUnityPlayer.resume()
    iUnityPlayer.windowFocusChanged(true)
  }

  val unityPlayer: IUnityPlayer
    get() {
      UnityPlayerManager.activity = currentActivity
      return iUnityPlayer
    }

  /**
   * Pauses the `UnityPlayer`.
   */
  fun pausePlayer() {
    d("UnityPlayerWrapper: Pausing")
    iUnityPlayer.windowFocusChanged(false)
    iUnityPlayer.pause()
  }

  /**
   * Shuts down the `UnityPlayer`.
   * Note: this will kill the application process entirely.
   */
  fun quitPlayer() {
    d("UnityPlayerWrapper: Shutting down UnityPlayer")
    iUnityPlayer.quit()
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
    return iUnityPlayer.injectEvent(inputEvent)
  }

  /**
   * Forwards configuration changes to Unity.
   *
   * @param configuration
   */
  fun injectConfigurationChange(configuration: Configuration?) {
    iUnityPlayer.configurationChanged(configuration)
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
        mUnityPlayerContextWrapperField!![iUnityPlayer] = applicationContext
      } else {
        mUnityPlayerContextWrapperField!![iUnityPlayer] = contextWrapper
      }
    } catch (e: Throwable) {
      e.printStackTrace()
    }
  }

  private fun setPlayerSurfaceDirect(surface: Surface?) {
    iUnityPlayer.displayChanged(0, surface)
  }

  companion object {
    var unityPlayerCurrentActivity: Activity?
      get() = UnityPlayerManager.activity
      set(activity) {
        UnityPlayerManager.activity = activity
      }
  }
}
