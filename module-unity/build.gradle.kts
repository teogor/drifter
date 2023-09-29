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
import dev.teogor.drifter.plugin.models.Configuration
import dev.teogor.drifter.plugin.models.PlatformArch
import dev.teogor.drifter.plugin.unityOptions

plugins {
  alias(libs.plugins.android.library)
  alias(libs.plugins.jetbrains.kotlin.android)

  id("dev.teogor.drifter.plugin")
}

val unityStreamingAssets: String? by project
val unityStreamingAssetsList: List<String> = unityStreamingAssets?.split(",") ?: emptyList()

android {
  namespace = "dev.teogor.drifter.demo.unity.library"

  compileSdk = 34

  defaultConfig {
    minSdk = 21
  }

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
  }

  kotlinOptions {
    jvmTarget = JavaVersion.VERSION_11.toString()
  }

  unityOptions(
    androidConfig = this,
  ) {
    splashMode = 0
    splashEnable = true
    buildId = "ea574d1a-3365-44b3-9676-33bceabcf351"
    notchConfig = "portrait|landscape"
    version = "2022.3.7f1"

    ndkVersion = "23.1.7779620"
    ndkPath = "D:\\Unity\\Editor\\2022.3.7f1\\Editor\\Data\\PlaybackEngines\\AndroidPlayer\\NDK"

    platforms = listOf(
      PlatformArch("armeabi-v7a", "armv7"),
      PlatformArch("arm64-v8a", "arm64"),
    )
    configuration = Configuration.Release
    streamingAssets += unityStreamingAssetsList

    exportFolder = "E:\\ZeoOwl\\.github\\aquarium-unity\\Export"
  }
}

dependencies {
  api(project(":drifter-wallpaper"))

  implementation(libs.appcompat)
  implementation(libs.gson)
}
