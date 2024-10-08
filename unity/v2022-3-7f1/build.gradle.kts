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
  alias(libs.plugins.teogor.winds)
}

android {
  namespace = "dev.teogor.drifter.unity.v2022.x3.x7f1"

  defaultConfig {
    consumerProguardFiles("proguard-unity.txt")
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
  api(files("libs/unity-classes.jar"))
  api(projects.unity.common)

  implementation(libs.androidx.annotation)
}

winds {
  moduleMetadata {
    artifactDescriptor {
      name = "2022-3-7f1"
    }
  }
}
