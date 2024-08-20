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

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  `kotlin-dsl`
  id("com.gradle.plugin-publish") version "1.2.1"
  alias(libs.plugins.buildconfig)
  alias(libs.plugins.teogor.winds)
}

java {
  // Up to Java 11 APIs are available through desugaring
  // https://developer.android.com/studio/write/java11-minimal-support-table
  sourceCompatibility = JavaVersion.VERSION_11
  targetCompatibility = JavaVersion.VERSION_11
}

tasks.withType<KotlinCompile>().configureEach {
  kotlinOptions {
    jvmTarget = JavaVersion.VERSION_11.toString()
  }
}

dependencies {
  api(gradleApi())

  compileOnly(libs.jetbrains.kotlinx.serialization.json)
  compileOnly(libs.android.gradlePlugin)
  compileOnly(libs.kotlin.gradlePlugin)
  compileOnly(libs.ksp.gradlePlugin)
}

@Suppress("UnstableApiUsage")
gradlePlugin {
  website.set("https://source.teogor.dev/drifter")
  vcsUrl.set("https://github.com/teogor/drifter")

  plugins {
    register("unityConfiguratorPlugin") {
      id = "dev.teogor.drifter"
      implementationClass = "dev.teogor.drifter.plugin.DrifterPlugin"
      displayName = "Gradle Unity Configurator for Drifter Plugin"
      description =
        "Drifter simplifies the integration between Unity and Android, enhancing performance seamlessly and effortlessly."
      tags.set(
        listOf(
          "drifter",
          "unity",
          "android",
          "integration",
          "performance",
          "development",
          "android-library",
        ),
      )
    }
  }
}

winds {
  moduleMetadata {
    artifactDescriptor {
      name = "Gradle Plugin"
    }

    documentationBuilder {
      pluginIds += listOf(
        "dev.teogor.drifter",
      )
    }
  }
}

afterEvaluate {
  buildConfig {
    packageName(group.toString())

    buildConfigField("String", "NAME", "\"${group}\"")
    buildConfigField("String", "VERSION", "\"${version}\"")
  }
}
