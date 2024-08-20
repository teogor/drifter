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
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.file.CopySpec
import org.gradle.api.file.DeleteSpec
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.TaskExecutionException
import org.gradle.kotlin.dsl.register
import org.gradle.process.ExecSpec

open class BuildIl2CppTask : DefaultTask() {

  private var unityOptions: UnityOptions? = null

  private val workingDir: String by lazy {
    project.projectDir.toString().replace("\\\\", "/")
  }

  init {
    group = "dev.teogor.drifter"
    description = "Compile and build Il2Cpp for Unity integration"
  }

  @TaskAction
  fun buildIl2Cpp() {
    try {
      if (unityOptions == null) {
        // TODO: Consider making 'unityOptions' a global variable to handle null scenarios.
        throw RuntimeException(
          "An error occurred when trying to access 'unityOptions.' Please help us by reporting this issue at https://github.com/teogor/drifter/issues/new.",
        )
      }
      val options = unityOptions!!

      val configuration = options.configuration.value
      val staticLibraries = emptyArray<String>()

      val android = project.extensions.getByType(AndroidComponents::class.java)
      val sdkComponents = android.sdkComponents

      options.platforms.forEach { architecture ->
        project.buildIl2Cpp(
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

  fun setUnityOptions(unityOptions: UnityOptions) {
    this.unityOptions = unityOptions
  }

  private fun Project.buildIl2Cpp(
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

    // Create a copy task to move the file to the desired location
    tasks.create("copyLibil2cpp$abi") {
      val execSpec = exec {
        // Explicitly cast to ExecSpec to address lambda capture limitations and
        // guarantee type safety for the extension method call.
        @Suppress("USELESS_CAST", "KotlinRedundantDiagnosticSuppress")
        (this as ExecSpec).configureIl2CppExec(
          executablePath = getIl2CppExecutablePath(
            workingDir,
            executableExtension,
          ),
          commandLineArgs = commandLineArgs,
          sdkDirectory = sdkDirectory,
        )
      }
      execSpec.assertNormalExitValue()

      copy {
        // Explicitly cast to CopySpec to address lambda capture limitations and
        // guarantee type safety for the extension method call.
        @Suppress("USELESS_CAST", "KotlinRedundantDiagnosticSuppress")
        (this as CopySpec).copyIl2CppSymbols(
          workingDir = workingDir,
          abi = abi,
        )
      }

      delete {
        // Explicitly cast to DeleteSpec to address lambda capture limitations and
        // guarantee type safety for the extension method call.
        @Suppress("USELESS_CAST", "KotlinRedundantDiagnosticSuppress")
        (this as DeleteSpec).deleteUnnecessaryIl2CppFiles(
          workingDir = workingDir,
          abi = abi,
        )
      }
    }
  }

  private fun getIl2CppExecutablePath(
    workingDir: String,
    executableExtension: String,
  ): String {
    return "$workingDir/src/main/Il2CppOutputProject/IL2CPP/build/deploy/il2cpp$executableExtension"
  }
}

/**
 * Configures an [ExecSpec] for executing the Il2Cpp compiler.
 *
 * @receiver The [ExecSpec] to be configured.
 * @param executablePath The path to the Il2Cpp executable.
 * @param commandLineArgs The command-line arguments to pass to the Il2Cpp executable.
 * @param sdkDirectory The path to the Android SDK root directory.
 */
private fun ExecSpec.configureIl2CppExec(
  executablePath: String,
  commandLineArgs: MutableList<String>,
  sdkDirectory: String,
) {
  executable = executablePath
  args(*commandLineArgs.toTypedArray())
  environment("ANDROID_SDK_ROOT", sdkDirectory)
}

/**
 * Copies Il2Cpp symbol files to the appropriate destination.
 *
 * @receiver The [CopySpec] to be configured.
 * @param workingDir The root directory of the Il2Cpp project.
 * @param abi The target ABI for the symbol files.
 */
private fun CopySpec.copyIl2CppSymbols(
  workingDir: String,
  abi: String,
) {
  from("$workingDir/src/main/jniLibs/$abi/libil2cpp.dbg.so")
  into("$workingDir/symbols/$abi/")
  rename("libil2cpp.dbg.so", "tlibil2cpp.so")
}

/**
 * Deletes unnecessary Il2Cpp files from the jniLibs directory.
 *
 * @receiver The [DeleteSpec] to be configured.
 * @param workingDir The root directory of the Il2Cpp project.
 * @param abi The target ABI for the files to be deleted.
 */
private fun DeleteSpec.deleteUnnecessaryIl2CppFiles(
  workingDir: String,
  abi: String,
) {
  delete("$workingDir/src/main/jniLibs/$abi/libil2cpp.dbg.so")
  delete("$workingDir/src/main/jniLibs/$abi/libil2cpp.sym.so")
}

/**
 * Creates a Gradle task for building Il2Cpp.
 *
 * @receiver The Gradle Project instance.
 * @param unityOptions The configuration options for Unity integration.
 */
fun Project.createBuildIl2CppTask(
  unityOptions: UnityOptions,
) {
  tasks.register<BuildIl2CppTask>("buildIl2Cpp") {
    dependsOn("syncUnityAssets")
    setUnityOptions(unityOptions)
  }
}
