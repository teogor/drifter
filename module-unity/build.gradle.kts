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

import dev.teogor.drifter.plugin.getSafeDrifterUnityPathExport
import dev.teogor.drifter.plugin.getSafeDrifterUnityPathNdk
import dev.teogor.drifter.plugin.models.Configuration
import dev.teogor.drifter.plugin.models.PlatformArch
import dev.teogor.drifter.plugin.unityOptions

plugins {
  alias(libs.plugins.ceres.android.library)
  alias(libs.plugins.teogor.drifter)
}

val unityStreamingAssets: String? by project
val unityStreamingAssetsList: List<String> = unityStreamingAssets?.split(",") ?: emptyList()

android {
  namespace = "dev.teogor.drifter.demo.module.unity"

  unityOptions(
    androidConfig = this,
  ) {
    splashMode = 0
    splashEnable = true
    buildId = "ea574d1a-3365-44b3-9676-33bceabcf351"
    notchConfig = "portrait|landscape"
    version = "2022.3.7f1"

    ndkVersion = "23.1.7779620"
    ndkPath = getSafeDrifterUnityPathNdk()

    platforms = listOf(
      PlatformArch.Arm64,
      PlatformArch.Armv7,
    )
    configuration = Configuration.Release
    streamingAssets += unityStreamingAssetsList

    exportFolder = getSafeDrifterUnityPathExport()
  }
}

dependencies {
  implementation(projects.drifterWallpaper)

  implementation(libs.appcompat)
  implementation(libs.gson)
}
