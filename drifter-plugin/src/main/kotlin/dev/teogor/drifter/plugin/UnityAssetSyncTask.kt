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
    File(unityOptions!!.exportFolder)
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
        "assets",
        "Il2CppOutputProject",
        "jniLibs",
        "jniStaticLibs",
      )
      val targetedUnitySymbols = listOf(
        "symbols",
      )

      val rootDirectory = targetDir
      val srcDirectory = File(rootDirectory, "src/main")

      // Delete the specified folders
      targetedUnitySymbols.forEach { folderName ->
        val folder = File(rootDirectory, folderName)
        if (folder.exists()) {
          folder.deleteRecursively()
        }
      }
      targetedUnityFolders.forEach { folderName ->
        val folder = File(srcDirectory, folderName)
        if (folder.exists()) {
          folder.deleteRecursively()
        }
      }

      val unityExportLibName = options.libraryName
      val exportRootDirectory = File(sourceDir, unityExportLibName)
      val exportSrcDirectory = File(exportRootDirectory, "src/main")

      targetedUnitySymbols.forEach { folderName ->
        val folder = File(exportRootDirectory, folderName)
        if (folder.exists()) {
          folder.copyRecursively(File(rootDirectory, folderName))
        }
      }

      targetedUnityFolders.forEach { folderName ->
        val folder = File(exportSrcDirectory, folderName)
        if (folder.exists()) {
          folder.copyRecursively(File(srcDirectory, folderName))
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
