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

package dev.teogor.drifter.plugin.models

/**
 * Represents the options for building a Unity project.
 *
 * @param splashMode The splash mode to use. This can be one of
 * the following values:
 *   * `0`: No splash.
 *   * `1`: Use the Unity logo as the splash.
 *   * `2`: Use a custom splash image.
 *
 * @param splashEnable Whether to enable the splash screen.
 *
 * @param buildId The build ID.
 *
 * @param version The version of the Unity project.
 *
 * @param notchConfig The notch configuration. This can be one of
 * the following values:
 *   * `"none"`: No notch.
 *   * `"left"`: Notch on the left.
 *   * `"right"`: Notch on the right.
 *   * `"both"`: Notch on both sides.
 *
 * @param ndkVersion The version of the NDK to use.
 *
 * @param ndkPath The path to the NDK.
 *
 * @param platforms The list of platforms to build for.
 *
 * @param configuration The build configuration. This can be one of
 * the following values:
 *   * `"Debug"`: Build a debug configuration.
 *   * `"Release"`: Build a release configuration.
 *
 * @param streamingAssets The list of streaming assets to include in
 * the build.
 *
 * @param exportFolder The folder to export the build to.
 *
 * @param libraryName The name of the library to create.
 */
data class UnityOptions(
  var splashMode: Int = 0,
  var splashEnable: Boolean = false,
  var buildId: String = "",
  var version: String = "",
  var notchConfig: String = "",
  var ndkVersion: String = "",
  var ndkPath: String = "",
  var platforms: List<PlatformArch> = emptyList(),
  var configuration: Configuration = Configuration.Debug,
  var streamingAssets: List<String> = emptyList(),
  var exportFolder: String = "",
  var libraryName: String = "unityLibrary",
)
