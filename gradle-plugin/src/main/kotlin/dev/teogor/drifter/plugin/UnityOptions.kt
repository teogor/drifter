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
import dev.teogor.drifter.plugin.models.Configuration
import dev.teogor.drifter.plugin.models.UnityOptions
import dev.teogor.drifter.plugin.unity.readBuildFingerprint
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

/**
 * Configure BuildConfig-specific options for the Android project.
 *
 * @param androidConfig The common extension of the Android project.
 */
fun Project.unityOptions(
  androidConfig: CommonExtension<*, *, *, *, *, *>,
  configure: UnityOptions.() -> Unit,
) {
  val unityOptions = UnityOptions()
    .apply(configure)
    .let { parseBuildFingerprint(it) }

  parseBuildFingerprint(unityOptions)

  androidConfig.applyUnityManifestPlaceholders(
    unityOptions = unityOptions,
  )

  createUnityNativeBuildTask(
    unityOptions = unityOptions,
  )

  createRefreshUnityAssetsTask(
    unityOptions = unityOptions,
  )

  unityBuildTask(
    commonExtension = androidConfig,
    unityOptions = unityOptions,
  )

  androidConfig.apply {
    dependencies {
      // add("api", project(":unity-wallpaper"))
    }
  }
}

/**
 * Updates the given [UnityOptions] based on the build fingerprint read from a file.
 *
 * This function reads the build fingerprint file located at the root project directory
 * and updates the `configuration` property of the provided [UnityOptions] instance
 * based on the build type specified in the fingerprint.
 *
 * @param unityOptions The [UnityOptions] instance to be updated.
 * @return The updated [UnityOptions] instance with configuration set according to the build fingerprint.
 */
private fun Project.parseBuildFingerprint(
  unityOptions: UnityOptions,
): UnityOptions {
  val parsedFingerprint = readBuildFingerprint(
    dirPath = projectDir.absolutePath,
  )

  if (parsedFingerprint != null) {
    unityOptions.apply {
      configuration = when (parsedFingerprint.buildType) {
        "Release" -> Configuration.Release
        "Debug" -> Configuration.Debug
        else -> configuration
      }
    }
  }

  return unityOptions
}
