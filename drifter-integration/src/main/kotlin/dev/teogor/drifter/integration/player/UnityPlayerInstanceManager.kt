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
import android.content.Intent
import dev.teogor.drifter.integration.activities.UnityPlayerInstantiatorActivity
import dev.teogor.drifter.integration.core.IUnityPlayerInstanceCreatedListener
import dev.teogor.drifter.integration.utilities.ActivityThemeUtility.getThemeResId
import dev.teogor.drifter.integration.utilities.DebugLog.d
import dev.teogor.drifter.integration.utilities.DebugLog.e
import dev.teogor.drifter.integration.utilities.DebugLog.v
import dev.teogor.drifter.integration.utilities.UnityVersionInfo

/**
 * Manages creation of `UnityPlayerWrapper` and notifies the listener about this event.
 */
class UnityPlayerInstanceManager {
  private val mUnityPlayerInstanceCreatedListeners =
    ArrayList<IUnityPlayerInstanceCreatedListener>()
  private var mUnityPlayerPauseResumeManager: UnityPlayerPauseResumeManager =
    UnityPlayerPauseResumeManager()
  /**
   * @return Currently active `UnityPlayerHolder`, or null if none is active.
   */
  /**
   * Sets the currently  active `UnityWallpaperService.UnityPlayerHolder`.
   *
   * @param unityPlayerHolder Currently active `UnityWallpaperService.UnityPlayerHolder`.
   */
  /**
   * Currently active `UnityWallpaperService.UnityPlayerHolder`, or null if none is active.
   */
  var activeUnityPlayerHolder: UnityPlayerHolder? = null
  /**
   * Gets the `UnityPlayerWrapper` instance.
   *
   * @return `UnityPlayerWrapper` instance, or null if it is not instantiated yet.
   */

  /**
   * `UnityPlayerWrapper` instance. There could be only one.
   */
  @get:Synchronized
  var unityPlayerWrapperInstance: UnityPlayerWrapper? = null
    private set

  val unityPlayerPauseResumeManager: UnityPlayerPauseResumeManager
    get() {
      return mUnityPlayerPauseResumeManager
    }

  val isUnityPlayerPaused: Boolean
    get() = unityPlayerWrapperInstance == null || mUnityPlayerPauseResumeManager.isUnityPlayerPaused

  /**
   * @return New `UnityPlayerHolder` instance.
   */
  fun createUnityPlayerHolder(): UnityPlayerHolder {
    return UnityPlayerHolder(mUnityPlayerPauseResumeManager)
  }

  /**
   * Requests that Unity player should be created.
   *
   * @param contextWrapper
   */
  @Synchronized
  fun requestCreateUnityPlayer(contextWrapper: ContextWrapper) {
    // If provided context is actually an Activity, use that Activity when instantiating the Unity player
    if (contextWrapper is Activity) {
      createUnityPlayerFromActivity(contextWrapper, false)
      return
    }
    createUnityPlayerViaInstantiatorActivity(contextWrapper)
  }

  /**
   * Creates a `UnityPlayerWrapper` immediately using the `contextWrapper`.
   *
   * @param contextWrapper `ContextWrapper` passed to `UnityPlayer` constructor.
   */
  @Synchronized
  fun createUnityPlayerDirectly(contextWrapper: ContextWrapper?) {
    if (unityPlayerWrapperInstance != null) return
    v("Instantiating Unity Player")
    unityPlayerWrapperInstance = UnityPlayerWrapper(contextWrapper!!)
    mUnityPlayerPauseResumeManager.setUnityPlayerWrapper(unityPlayerWrapperInstance)
    notifyUnityPlayerInstanceCreatedListeners()
  }

  @Synchronized
  fun createUnityPlayerFromActivity(activity: Activity) {
    this.createUnityPlayerFromActivity(activity, false)
  }

  @Synchronized
  fun createUnityPlayerFromActivity(activity: Activity, finishActivityAfterCreation: Boolean) {
    if (unityPlayerWrapperInstance != null) {
      e("createUnityPlayerFromActivity called when mUnityPlayerWrapper != null")
      return
    }
    val instantiatePlayerRunnable = Runnable {
      // For some reason, Android creates a new permission request dialog each time Activity
      // is minimized and maximized again. This is weird, but safe to ignore.
      if (unityPlayerWrapperInstance != null) {
        d(
          "Permission request dialog returned, but mUnityPlayerWrapper != null. " +
            "This is weird. Perhaps the Activity was minimized when the permission request dialog " +
            "was visible?",
        )
        return@Runnable
      }

      // Save current theme
      val currentThemeResId = getThemeResId(activity)
      var unityPlayerContext: ContextWrapper? = activity.application
      if (UnityVersionInfo.instance != null) {
        unityPlayerContext =
          if (UnityVersionInfo.instance!!.isUnityNonActivityConstructorBugFixed) activity.application else activity
      }
      createUnityPlayerDirectly(unityPlayerContext)

      // For some reason, {@code UnityPlayer) resets the {@code Activity) theme, so we set it again.
      activity.setTheme(currentThemeResId)
      if (finishActivityAfterCreation) {
        activity.finish()
      }
    }
    runRunnable(instantiatePlayerRunnable)
  }

  /**
   * Starts `UnityPlayerInstantiatorActivity`, which will then instantiate the `UnityPlayer`.
   *
   * @param context `Context` used to start the `UnityPlayerInstantiatorActivity`
   * @see UnityPlayerInstantiatorActivity
   */
  private fun createUnityPlayerViaInstantiatorActivity(context: Context) {
    if (unityPlayerWrapperInstance != null) {
      e("createUnityPlayerViaInstantiatorActivity called when mUnityPlayerWrapper != null")
      return
    }
    val intent = Intent(context, UnityPlayerInstantiatorActivity::class.java)
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    context.startActivity(intent)
  }

  /**
   * Registers a `IUnityPlayerInstanceCreatedListener` to be called when `UnityPlayer` instance is created.
   * Note: there is no unregister method. Listener list is cleared automatically when
   * `UnityPlayer` instance is created.
   *
   * @param listener
   */
  @Synchronized
  fun registerUnityPlayerInstanceCreatedListener(listener: IUnityPlayerInstanceCreatedListener?) {
    requireNotNull(listener) { "listener == null" }
    mUnityPlayerInstanceCreatedListeners.add(listener)
    if (unityPlayerWrapperInstance != null) {
      e("Adding IUnityPlayerInstanceCreatedListener when mUnityPlayerWrapper != null")
      notifyUnityPlayerInstanceCreatedListeners()
    }
  }

  /**
   * Notifies listeners that an `UnityPlayer` instance was created.
   */
  private fun notifyUnityPlayerInstanceCreatedListeners() {
    v("Notifying UnityPlayerInstanceCreatedListeners")
    for (listener in mUnityPlayerInstanceCreatedListeners) {
      listener.onUnityPlayerInstanceCreated()
    }
    mUnityPlayerInstanceCreatedListeners.clear()
  }

  private fun runRunnable(runnable: Runnable) {
    try {
      runnable.run()
    } catch (tr: Throwable) {
      tr.printStackTrace()
    }
  }

  companion object {
    private var unityPlayerManagerInstance: UnityPlayerInstanceManager? = null

    val instance: UnityPlayerInstanceManager by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
      unityPlayerManagerInstance ?: UnityPlayerInstanceManager().also {
        unityPlayerManagerInstance = it
      }
    }
  }
}
