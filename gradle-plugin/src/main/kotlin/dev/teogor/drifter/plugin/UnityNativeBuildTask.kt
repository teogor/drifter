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

import com.android.build.api.dsl.SdkComponents
import com.android.build.api.variant.AndroidComponents
import dev.teogor.drifter.plugin.models.PlatformArch
import dev.teogor.drifter.plugin.models.UnityOptions
import dev.teogor.drifter.plugin.utils.error.UnityOptionsNotInitializedException
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.file.CopySpec
import org.gradle.api.file.DeleteSpec
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.TaskExecutionException
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.register
import org.gradle.process.ExecSpec

/**
 * A Gradle task for building native code for Unity integration.
 *
 * This task performs the following actions:
 * - Executes the native compiler with the specified configuration and architecture.
 * - Manages the output of the compilation process, including copying and deleting files.
 *
 * @see [createUnityNativeBuildTask]
 */
open class UnityNativeBuildTask : DefaultTask() {
  private lateinit var unityOptions: UnityOptions

  private val workingDir: String by lazy {
    project.projectDir.toString().replace("\\\\", "/")
  }

  init {
    group = "dev.teogor.drifter"
    description = "Compiles and builds native code for integrating with Unity, targeting multiple architectures."
  }

  /**
   * Executes the native build process.
   *
   * This method orchestrates the build process by calling individual methods
   * for building and managing native code.
   *
   * @throws TaskExecutionException if an error occurs during the task execution.
   */
  @TaskAction
  fun buildUnityNative() {
    checkInitialized()

    try {
      val options = unityOptions

      val configuration = options.configuration.value
      val staticLibraries = emptyArray<String>()

      val android = project.extensions.getByType<AndroidComponents>()
      val sdkComponents = android.sdkComponents

      options.platforms.forEach { architecture ->
        project.buildUnityNative(
          workingDir = workingDir,
          configuration = configuration,
          architecture = architecture,
          staticLibraries = staticLibraries,
          sdkComponents = sdkComponents,
        )
      }
    } catch (e: Exception) {
      throw TaskExecutionException(this, e)
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
   * Builds native code for Unity integration.
   *
   * @receiver The Gradle Project instance.
   * @param workingDir The root directory of the native project.
   * @param configuration The build configuration (e.g., Debug, Release).
   * @param architecture The target architecture for the build.
   * @param staticLibraries An array of additional static libraries.
   * @param sdkComponents The SDK components required for the build.
   */
  private fun Project.buildUnityNative(
    workingDir: String,
    configuration: String,
    architecture: PlatformArch,
    staticLibraries: Array<String>,
    sdkComponents: SdkComponents,
  ) {
    val abi = architecture.abi
    val arch = architecture.architecture
    val sdkDirectory = sdkComponents.sdkDirectory.get().asFile.path
    val ndkDirectory = sdkComponents.ndkDirectory.get().asFile.path

    val commandLineArgs = mutableListOf(
      "--compile-cpp",
      "--platform=Android",
      "--architecture=$arch",
      "--outputpath=$workingDir/src/main/jniLibs/$abi/libil2cpp.so",
      "--baselib-directory=$workingDir/src/main/jniStaticLibs/$abi",
      "--incremental-g-c-time-slice=3",
      "--configuration=$configuration",
      "--dotnetprofile=unityaot-linux",
      "--profiler-report",
      "--profiler-output-file=$workingDir/build/il2cpp_${abi}_$configuration/il2cpp_conv.traceevents",
      "--print-command-line",
      "--data-folder=$workingDir/src/main/Il2CppOutputProject/Source/il2cppOutput/data",
      "--generatedcppdir=$workingDir/src/main/Il2CppOutputProject/Source/il2cppOutput",
      "--cachedirectory=$workingDir/build/il2cpp_${abi}_$configuration/il2cpp_cache",
      "--tool-chain-path=$ndkDirectory",
    )

    staticLibraries.forEachIndexed { _, fileName ->
      commandLineArgs.add(
        "--additional-libraries=$workingDir/src/main/jniStaticLibs/$abi/$fileName",
      )
    }

    var executableExtension = ""
    if (org.gradle.internal.os.OperatingSystem.current().isWindows) {
      executableExtension = ".exe"
      commandLineArgs.replaceAll { it.replace("\"", "\\\"") }
    }

    tasks.create("copyLibNative$abi") {
      val execSpec = exec {
        @Suppress("USELESS_CAST", "KotlinRedundantDiagnosticSuppress")
        (this as ExecSpec).configureNativeBuildExec(
          executablePath = getNativeExecutablePath(
            workingDir,
            executableExtension,
          ),
          commandLineArgs = commandLineArgs,
          sdkDirectory = sdkDirectory,
        )
      }
      execSpec.assertNormalExitValue()

      copy {
        @Suppress("USELESS_CAST", "KotlinRedundantDiagnosticSuppress")
        (this as CopySpec).copyNativeSymbols(
          workingDir = workingDir,
          abi = abi,
        )
      }

      delete {
        @Suppress("USELESS_CAST", "KotlinRedundantDiagnosticSuppress")
        (this as DeleteSpec).deleteUnnecessaryNativeFiles(
          workingDir = workingDir,
          abi = abi,
        )
      }
    }
  }

  /**
   * Gets the path to the native executable.
   *
   * @param workingDir The root directory of the native project.
   * @param executableExtension The extension of the executable (e.g., ".exe" for Windows).
   * @return The path to the native executable.
   */
  private fun getNativeExecutablePath(
    workingDir: String,
    executableExtension: String,
  ): String {
    return "$workingDir/src/main/Il2CppOutputProject/IL2CPP/build/deploy/il2cpp$executableExtension"
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
     * The name of the Gradle task for building native code for Unity integration.
     *
     * This constant is used to identify the task in Gradle's task registry and can be used
     * when registering or configuring the task in build scripts or other Gradle-related code.
     *
     * @see [createUnityNativeBuildTask]
     */
    const val TASK_NAME = "unityNativeBuild"
  }
}

/**
 * Configures an [ExecSpec] for executing the native compiler.
 *
 * @receiver The [ExecSpec] to be configured.
 * @param executablePath The path to the native executable.
 * @param commandLineArgs The command-line arguments to pass to the native executable.
 * @param sdkDirectory The path to the Android SDK root directory.
 */
private fun ExecSpec.configureNativeBuildExec(
  executablePath: String,
  commandLineArgs: MutableList<String>,
  sdkDirectory: String,
) {
  executable = executablePath
  args(*commandLineArgs.toTypedArray())
  environment("ANDROID_SDK_ROOT", sdkDirectory)
}

/**
 * Copies native symbol files to the appropriate destination.
 *
 * @receiver The [CopySpec] to be configured.
 * @param workingDir The root directory of the native project.
 * @param abi The target ABI for the symbol files.
 */
private fun CopySpec.copyNativeSymbols(
  workingDir: String,
  abi: String,
) {
  from("$workingDir/src/main/jniLibs/$abi/libil2cpp.dbg.so")
  into("$workingDir/symbols/$abi/")
  rename("libil2cpp.dbg.so", "tlibil2cpp.so")
}

/**
 * Deletes unnecessary native files from the jniLibs directory.
 *
 * @receiver The [DeleteSpec] to be configured.
 * @param workingDir The root directory of the native project.
 * @param abi The target ABI for the files to be deleted.
 */
private fun DeleteSpec.deleteUnnecessaryNativeFiles(
  workingDir: String,
  abi: String,
) {
  delete("$workingDir/src/main/jniLibs/$abi/libil2cpp.dbg.so")
  delete("$workingDir/src/main/jniLibs/$abi/libil2cpp.sym.so")
}

/**
 * Creates and registers a [UnityNativeBuildTask] with the given [unityOptions].
 *
 * @param unityOptions The UnityOptions to be used by the task.
 *
 * @see [UnityNativeBuildTask]
 */
fun Project.createUnityNativeBuildTask(
  unityOptions: UnityOptions,
) {
  tasks.register<UnityNativeBuildTask>(UnityNativeBuildTask.TASK_NAME) {
    dependsOn(RefreshUnityAssetsTask.TASK_NAME)
    setUnityOptions(unityOptions)
  }
}
