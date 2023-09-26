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
import com.vanniktech.maven.publish.SonatypeHost
import dev.teogor.publish.applyPublishOptions

plugins {
  alias(libs.plugins.androidLibrary)
  alias(libs.plugins.kotlinAndroid)

  id("dev.teogor.publish")
}

android {
  namespace = "dev.teogor.drifter.compose"
  compileSdk = 34

  defaultConfig {
    minSdk = 24

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

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
  }

  kotlinOptions {
    jvmTarget = JavaVersion.VERSION_11.toString()
  }

  buildFeatures {
    compose = true
  }

  composeOptions {
    kotlinCompilerExtensionVersion = "1.5.1"
  }
}

dependencies {
  implementation(project(":drifter-core"))
  implementation(project(":drifter-integration"))
  implementation(project(":drifter-wallpaper"))

  implementation(platform(libs.compose.bom))
  implementation(libs.ui)
  implementation(libs.ui.graphics)
  implementation(libs.ui.tooling.preview)

  implementation(libs.lifecycle.runtime.ktx)
}

publishOptions {

  defaultLibraryInfo(
    artifactId = "compose",
    version = "1.0.0-alpha01",
  )

  mavenPublishing {
    publishToMavenCentral(SonatypeHost.S01)
    signAllPublications()

    @Suppress("UnstableApiUsage")
    pom {
      coordinates(
        groupId = this@publishOptions.groupId,
        artifactId = this@publishOptions.artifactId,
        version = this@publishOptions.version,
      )

      applyPublishOptions(this@publishOptions)
    }
  }

}
