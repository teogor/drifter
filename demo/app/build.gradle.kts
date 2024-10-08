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
  alias(libs.plugins.ceres.android.application)
  alias(libs.plugins.ceres.android.application.compose)
  alias(libs.plugins.jetbrains.compose.compiler)
  alias(libs.plugins.ksp)
}

android {
  namespace = "dev.teogor.drifter.demo"

  defaultConfig {
    applicationId = "com.zeoowl.lwp.aquarium"
    versionCode = 1
    versionName = "1.0.0-alpha01"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    vectorDrawables {
      useSupportLibrary = true
    }
  }

  buildTypes {
    release {
      isMinifyEnabled = false
      proguardFiles(
        getDefaultProguardFile("proguard-android-optimize.txt"),
        "proguard-rules.pro",
      )
      signingConfig = signingConfigs.getByName("debug")
    }
  }

  packaging {
    resources {
      excludes += "/META-INF/{AL2.0,LGPL2.1}"
    }
  }
}

dependencies {
  implementation(projects.demo.moduleUnity)

  implementation(projects.runtime)
  ksp(projects.compiler)

  implementation(projects.compose)
  implementation(projects.core)
  implementation(projects.unity.v202237f1)
  implementation(projects.integration)
  implementation(projects.wallpaper)

  implementation(platform(libs.ceres.bom))
  implementation(libs.ceres.core.register)

  implementation(libs.gson)
  implementation(libs.core.ktx)
  implementation(libs.lifecycle.runtime.ktx)
  implementation(libs.activity.compose)
  implementation(platform(libs.compose.bom))
  implementation(libs.ui)
  implementation(libs.ui.graphics)
  implementation(libs.ui.tooling.preview)
  implementation(libs.material3)
  testImplementation(libs.junit)
  androidTestImplementation(libs.androidx.test.ext.junit)
  androidTestImplementation(libs.espresso.core)
  androidTestImplementation(platform(libs.compose.bom))
  androidTestImplementation(libs.ui.test.junit4)
  debugImplementation(libs.ui.tooling)
  debugImplementation(libs.ui.test.manifest)
}

ksp {
  logging.captureStandardError(LogLevel.INFO)
}
