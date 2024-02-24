/*
 * Copyright 2024 teogor (Teodor Grigor)
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

package dev.teogor.drifter.unity.common

import android.app.Activity
import dev.teogor.drifter.unity.common.UnityPlayerManager.currentActivity
import dev.teogor.drifter.unity.common.UnityPlayerManager.unityPlayer
import java.lang.ref.WeakReference

/**
 * Manages the lifecycle and interactions of the UnityPlayer instance.
 *
 * @property currentActivity: A weak reference to the current Activity associated with the UnityPlayer.
 * @property unityPlayer: The current instance of the IUnityPlayer interface, or null if not initialized.
 */
object UnityPlayerManager {

  /**
   * A weak reference to the current Activity associated with the UnityPlayer.
   */
  private lateinit var currentActivity: WeakReference<Activity>

  /**
   * The current instance of the IUnityPlayer interface, or null if not initialized.
   */
  private var unityPlayer: IUnityPlayer? = null

  /**
   * Retrieves the current UnityPlayer instance, or null if not initialized.
   *
   * @return The current IUnityPlayer instance, or null if not available.
   */
  fun getUnityPlayer(): IUnityPlayer? {
    return unityPlayer
  }

  /**
   * Sets the current UnityPlayer instance.
   *
   * @param newInstance The new IUnityPlayer instance to set.
   */
  fun setUnityPlayer(newInstance: IUnityPlayer) {
    unityPlayer = newInstance
  }

  /**
   * Gets a weak reference to the current Activity.
   *
   * @return The current Activity, or null if not set.
   */
  var activity: Activity?
    get() = currentActivity.get()
    set(value) {
      currentActivity = WeakReference(value)
      value?.let { unityPlayer?.onNewCurrentActivity(it) }
    }
}
