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

plugins {
  alias(libs.plugins.ceres.android.library)
  alias(libs.plugins.ceres.android.library.compose)
  alias(libs.plugins.jetbrains.compose.compiler)
  alias(libs.plugins.teogor.winds)
}

android {
  namespace = "dev.teogor.drifter.compose"

  defaultConfig {

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    consumerProguardFiles("consumer-rules.pro")
  }

  buildTypes {
    release {
      isMinifyEnabled = false
      proguardFiles(
        getDefaultProguardFile("proguard-android-optimize.txt"),
        "proguard-rules.pro",
      )
    }
  }
}

dependencies {
  implementation(platform(libs.ceres.bom))
  api(libs.ceres.core.common)

  implementation(projects.unity.common)
  implementation(projects.integration)
  implementation(projects.wallpaper)

  implementation(platform(libs.compose.bom))
  implementation(libs.ui)
  implementation(libs.ui.graphics)
  implementation(libs.ui.tooling.preview)
  implementation(libs.androidx.foundation.layout.android)

  implementation(libs.lifecycle.runtime.ktx)
}

winds {
  moduleMetadata {
    artifactDescriptor {
      name = "Compose"
    }
  }
}
