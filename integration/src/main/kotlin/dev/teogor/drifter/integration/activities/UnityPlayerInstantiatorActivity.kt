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

package dev.teogor.drifter.integration.activities

import android.app.Activity
import android.os.Bundle
import dev.teogor.drifter.integration.player.UnityPlayerInstanceManager
import dev.teogor.drifter.integration.utilities.DebugLog.e
import dev.teogor.drifter.integration.utilities.UnityVersionInfo

/**
 * An invisible `Activity` that provides a `Context` for `UnityPlayer` construction.
 *
 *
 * For some reason, current Unity versions crash if `new UnityPlayer(context)` argument
 * is anything other than an `Activity` (`WallpaperService`, for example). So we create an
 * `Activity`, use it to instantiate `UnityPlayer`, and immediately kill the `Activity`.
 */
class UnityPlayerInstantiatorActivity : Activity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    // Update Unity version info
    UnityVersionInfo.updateUnityVersionIfNeeded(this)

    // Just close the Activity if there is nothing to do
    if (UnityPlayerInstanceManager.instance.unityPlayerWrapperInstance != null) {
      e(
        "UnityPlayerInstantiatorActivity created when " +
          "UnityPlayer is already created. This should not happen.",
      )
      finish()
      return
    }

    // Create the {@code UnityPlayer)
    UnityPlayerInstanceManager.instance.createUnityPlayerFromActivity(this, true)
  }
}
