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
import dev.teogor.drifter.plugin.utils.error.UnityOptionsNotInitializedException
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.TaskExecutionException
import org.gradle.kotlin.dsl.register
import java.io.File

/**
 * A Gradle task for refreshing and updating Unity assets and resources in the project's directory.
 *
 * This task performs the following actions:
 * - Deletes specific folders from the target directory.
 * - Copies updated content from the Unity export directory, including symbols, assets,
 *   JNI libraries, and other resources.
 *
 * @see [createRefreshUnityAssetsTask]
 */
open class RefreshUnityAssetsTask : DefaultTask() {
  private lateinit var unityOptions: UnityOptions

  private val sourceDir: File by lazy {
    checkInitialized()
    File(unityOptions.exportedProjectLocation)
  }

  private val targetDir: File by lazy {
    project.projectDir
  }

  init {
    group = "dev.teogor.drifter"
    description = "Refreshes and updates Unity assets and resources in the project's directory."
  }

  /**
   * Executes the asset refresh task.
   *
   * This method orchestrates the refresh process by calling individual methods
   * for folder deletion and content copying.
   *
   * @throws UnityOptionsNotInitializedException if the UnityOptions is not initialized.
   * @throws TaskExecutionException if an error occurs during the task execution.
   */
  @TaskAction
  fun executeTask() {
    try {
      checkInitialized()
      deleteTargetFolders()
      copyUpdatedContent()
    } catch (e: Exception) {
      throw TaskExecutionException(this, e)
    }
  }

  /**
   * Deletes specified folders from the target directory.
   *
   * @throws FolderDeletionException if an error occurs during folder deletion.
   */
  private fun deleteTargetFolders() {
    val targetedUnityFolders = listOf(
      "symbols",
      "src/main/assets",
      "src/main/Il2CppOutputProject",
      "src/main/jniLibs",
      "src/main/jniStaticLibs",
      "src/main/resources/META-INF",
    )

    val unityModulePath = targetDir

    try {
      targetedUnityFolders.forEach { folderName ->
        val folder = File(unityModulePath, folderName)
        if (folder.exists()) {
          folder.deleteRecursively()
        }
      }
    } catch (e: Exception) {
      // Log and throw a custom exception for folder deletion failure
      logger.error("Failed to delete target folders", e)
      throw FolderDeletionException(
        "Failed to delete one or more target folders. This may be due to permission issues or file system errors.",
        e,
      )
    }
  }

  /**
   * Copies updated content from the Unity export directory to the target directory.
   *
   * @throws ContentCopyException if an error occurs during content copying.
   */
  private fun copyUpdatedContent() {
    val options = unityOptions
    val targetedUnityFolders = listOf(
      "symbols",
      "src/main/assets",
      "src/main/Il2CppOutputProject",
      "src/main/jniLibs",
      "src/main/jniStaticLibs",
      "src/main/resources/META-INF",
    )

    val unityExportLibName = options.libraryName
    val exportRootDirectory = File(sourceDir, unityExportLibName)
    val unityModulePath = targetDir

    try {
      targetedUnityFolders.forEach { folderName ->
        val folder = File(exportRootDirectory, folderName)
        if (folder.exists()) {
          folder.copyRecursively(
            target = File(unityModulePath, folderName),
            overwrite = true,
            onError = { _, _ -> OnErrorAction.SKIP },
          )
        }
      }
    } catch (e: Exception) {
      // Log and throw a custom exception for content copy failure
      logger.error("Failed to copy updated content", e)
      throw ContentCopyException(
        "Failed to copy content from the Unity export directory. This may be due to file access issues or I/O errors.",
        e,
      )
    }
  }

  /**
   * Sets the UnityOptions for this task.
   *
   * @param unityOptions The UnityOptions to be used by the task.
   */
  fun setUnityOptions(unityOptions: UnityOptions) {
    this.unityOptions = unityOptions
  }

  /**
   * Checks if UnityOptions has been initialized. Throws an exception if it has not.
   *
   * @throws UnityOptionsNotInitializedException if UnityOptions is not initialized.
   */
  private fun checkInitialized() {
    if (!::unityOptions.isInitialized) {
      throw UnityOptionsNotInitializedException()
    }
  }

  companion object {
    /**
     * The name of the Gradle task for refreshing Unity assets.
     *
     * This constant is used to identify the task in Gradle's task registry and can be used
     * when registering or configuring the task in build scripts or other Gradle-related code.
     *
     * @see [createRefreshUnityAssetsTask]
     */
    const val TASK_NAME = "refreshUnityAssets"
  }
}

/**
 * Exception thrown when folder deletion fails during the task execution.
 *
 * @param message A description of the error that occurred.
 * @param cause The cause of the error, or `null` if the cause is nonexistent or unknown.
 *
 * @see [RefreshUnityAssetsTask.deleteTargetFolders]
 */
class FolderDeletionException(message: String, cause: Throwable? = null) : RuntimeException(
  """
  |Failed to delete one or more target folders. This may be due to permission issues or file system errors.
  |
  |$message
  |
  |To report this problem, file an issue on GitHub: https://github.com/teogor/drifter/issues
  """.trimMargin(),
  cause,
)

/**
 * Exception thrown when copying content fails during the task execution.
 *
 * @param message A description of the error that occurred.
 * @param cause The cause of the error, or `null` if the cause is nonexistent or unknown.
 *
 * @see [RefreshUnityAssetsTask.copyUpdatedContent]
 */
class ContentCopyException(message: String, cause: Throwable? = null) : RuntimeException(
  """
  |Failed to copy content from the Unity export directory. This may be due to file access issues or I/O errors.
  |
  |$message
  |
  |To report this problem, file an issue on GitHub: https://github.com/teogor/drifter/issues
  """.trimMargin(),
  cause,
)

/**
 * Creates and registers a [RefreshUnityAssetsTask] with the given [unityOptions].
 *
 * @param unityOptions The UnityOptions to be used by the task.
 *
 * @see [RefreshUnityAssetsTask]
 */
fun Project.createRefreshUnityAssetsTask(
  unityOptions: UnityOptions,
) {
  tasks.register<RefreshUnityAssetsTask>(RefreshUnityAssetsTask.TASK_NAME) {
    setUnityOptions(unityOptions)
  }
}
