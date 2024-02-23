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
 * Represents a platform and architecture combination.
 *
 * @param abi The ABI of the platform.
 *
 * @param architecture The architecture of the platform.
 */
data class PlatformArch(
  val abi: String,
  val architecture: String,
) {
  companion object {
    val Armv7 = PlatformArch(
      abi = "armeabi-v7a",
      architecture = "armv7",
    )
    val Arm64 = PlatformArch(
      abi = "arm64-v8a",
      architecture = "arm64",
    )
    val ChromeOs = PlatformArch(
      abi = "x86",
      architecture = "x86",
    )
    val WebXR = PlatformArch(
      abi = "x86_64",
      architecture = "x86_64",
    )
  }
}
