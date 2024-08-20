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

import dev.teogor.drifter.plugin.models.UnityOptions
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.TaskExecutionException
import org.gradle.kotlin.dsl.register
import java.io.File

open class UnityAssetSyncTask : DefaultTask() {

  private var unityOptions: UnityOptions? = null

  private val sourceDir: File by lazy {
    File(unityOptions!!.exportedProjectLocation)
  }

  private val targetDir: File by lazy {
    project.projectDir
  }

  init {
    group = "dev.teogor.drifter"
    description = "Sync Unity Exported Content"
  }

  @TaskAction
  fun importContent() {
    try {
      if (unityOptions == null) {
        // TODO: Consider making 'unityOptions' a global variable to handle null scenarios.
        throw RuntimeException(
          "An error occurred when trying to access 'unityOptions.' Please help us by reporting this issue at https://github.com/teogor/drifter/issues/new.",
        )
      }
      val options = unityOptions!!

      val targetedUnityFolders = listOf(
        "symbols",
        "src/main/assets",
        "src/main/Il2CppOutputProject",
        "src/main/jniLibs",
        "src/main/jniStaticLibs",
        "src/main/resources/META-INF",
      )

      val unityModulePath = targetDir

      // Delete the specified folders
      targetedUnityFolders.forEach { folderName ->
        val folder = File(unityModulePath, folderName)
        if (folder.exists()) {
          folder.deleteRecursively()
        }
      }

      val unityExportLibName = options.libraryName
      val exportRootDirectory = File(sourceDir, unityExportLibName)

      targetedUnityFolders.forEach { folderName ->
        val folder = File(exportRootDirectory, folderName)
        if (folder.exists()) {
          folder.copyRecursively(
            target = File(unityModulePath, folderName),
            overwrite = true,
            onError = { _, _ ->
              OnErrorAction.SKIP
            },
          )
        }
      }
    } catch (e: Exception) {
      throw TaskExecutionException(this, e)
    }
  }

  fun setUnityOptions(unityOptions: UnityOptions) {
    this.unityOptions = unityOptions
  }
}

fun Project.createUnityAssetSyncTask(
  unityOptions: UnityOptions,
) {
  tasks.register<UnityAssetSyncTask>("syncUnityAssets") {
    setUnityOptions(unityOptions)
  }
}
