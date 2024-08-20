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

package dev.teogor.drifter.plugin

import com.android.build.api.dsl.CommonExtension
import dev.teogor.drifter.plugin.models.UnityOptions

/**
 * Applies the Unity manifest placeholders to the project's manifest.
 *
 * @param unityOptions The Unity options.
 */
fun CommonExtension<*, *, *, *, *, *>.applyUnityManifestPlaceholders(
  unityOptions: UnityOptions,
) {
  defaultConfig {
    // Add meta fields to your manifest
    manifestPlaceholders += mapOf(
      "unity-options-splash-mode" to unityOptions.splashMode,
      "unity-options-splash-enable" to unityOptions.splashEnable,
      "unity-options-build-id" to unityOptions.buildId,
      "unity-options-version" to unityOptions.version,
      "unity-options-notch-config" to unityOptions.notchConfig,
    )
  }

  buildFeatures {
    // todo do we need it? if yes try to use Querent
    buildConfig = true
  }
}
