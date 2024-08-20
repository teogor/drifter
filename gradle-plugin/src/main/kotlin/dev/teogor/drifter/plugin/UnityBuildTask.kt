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

package dev.teogor.drifter.plugin

import com.android.build.api.dsl.CommonExtension
import dev.teogor.drifter.plugin.models.UnityOptions
import org.gradle.api.Project
import java.util.Locale

@OptIn(InternalDrifterApi::class)
fun Project.unityBuildTask(
  commonExtension: CommonExtension<*, *, *, *, *, *>,
  unityOptions: UnityOptions,
) {
  val unityNativeBuildTask = project.tasks.getByName(UnityNativeBuildTask.TASK_NAME)

  if (unityOptions.ndkPath.isEmpty()) {
    unityOptions.ndkPath = drifterUnityPathNdk ?: ""
  }

  if (unityOptions.exportedProjectLocation.isEmpty()) {
    unityOptions.exportedProjectLocation = drifterUnityPathExport ?: ""
  }

  commonExtension.apply {
    ndkVersion = unityOptions.ndkVersion
    ndkPath = unityOptions.ndkPath

    defaultConfig {
      packaging {
        jniLibs {
          unityOptions.platforms.forEach { architecture ->
            keepDebugSymbols.add("*/${architecture.abi}/*.so")
          }
        }
      }

      @Suppress("UnstableApiUsage")
      externalNativeBuild {
        cmake {
          abiFilters.addAll(unityOptions.platforms.map { it.abi })
        }
      }
    }

    afterEvaluate {
      val buildTypeTaskNames = commonExtension.buildTypes.map {
        val formattedBuildType = it.name.splitCamelCase().toTitleCase()
        formattedBuildType
      }

      buildTypeTaskNames
        .mapNotNull { formattedName ->
          project(path).tasks.named("merge${formattedName}JniLibFolders").getOrNull()
        }
        .forEach { task ->
          task.dependsOn(unityNativeBuildTask)
        }
    }

    androidResources {
      ignoreAssetsPattern = "!.svn:!.git:!.ds_store:!*.scc:!CVS:!thumbs.db:!picasa.ini:!*~"
      noCompress.addAll(
        listOf(
          ".unity3d",
          ".ress",
          ".resource",
          ".obb",
          ".bundle",
          ".unityexp",
        ) + unityOptions.streamingAssets,
      )
    }

    lint {
      abortOnError = false
    }
  }
}

/**
 * Splits a camel case string into separate words.
 */
private fun String.splitCamelCase(): String {
  return this.replace(Regex("([a-z])([A-Z])"), "$1 $2")
}

/**
 * Converts a string to title case.
 *
 * Title case means that the first character of each word is capitalized.
 *
 * @return The string in title case.
 */
private fun String.toTitleCase(): String {
  return this.split(" ").joinToString(" ") { word ->
    word.replaceFirstChar { char ->
      if (char.isLowerCase()) {
        char.titlecase(Locale.getDefault())
      } else {
        char.toString()
      }
    }
  }
}
