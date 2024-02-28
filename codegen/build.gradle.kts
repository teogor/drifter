/*
 * Copyright 2024 teogor (Teodor Grigor)
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

plugins {
  id("java-library")
  alias(libs.plugins.kotlin.jvm)
  alias(libs.plugins.teogor.winds)
}

java {
  sourceCompatibility = JavaVersion.VERSION_11
  targetCompatibility = JavaVersion.VERSION_11
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
  kotlinOptions {
    jvmTarget = JavaVersion.VERSION_11.toString()
  }
}

dependencies {
  api(projects.runtime)

  api(libs.kotlin.poet)
  api(libs.kotlin.poet.ksp)
}

winds.publishingOptions.sonatypeHost = SonatypeHost.S01
winds {
  moduleMetadata {
    artifactDescriptor {
      name = "Codegen"
    }
  }
}
