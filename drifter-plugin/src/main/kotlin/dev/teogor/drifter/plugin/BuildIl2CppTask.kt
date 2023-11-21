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
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.TaskExecutionException
import org.gradle.kotlin.dsl.register
import org.gradle.process.ExecSpec
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption

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
      val builder = ProcessBuilder()
      builder.command(
        "$workingDir/src/main/Il2CppOutputProject/IL2CPP/build/deploy/il2cpp$executableExtension",
        *commandLineArgs.toTypedArray(),
      )
      builder.environment()["ANDROID_SDK_ROOT"] = sdkDirectory
      val process = builder.start()
      process.waitFor()

      val sourceFile = File("$workingDir/src/main/jniLibs/$abi/libil2cpp.dbg.so")
      val destinationFile = File("$workingDir/symbols/$abi/")

      Files.copy(
        sourceFile.toPath(),
        destinationFile.toPath().resolve("tlibil2cpp.so"),
        StandardCopyOption.REPLACE_EXISTING,
      )

      delete("$workingDir/src/main/jniLibs/$abi/libil2cpp.dbg.so")
      delete("$workingDir/src/main/jniLibs/$abi/libil2cpp.sym.so")
    }

    // val execAction = project.exec {
    //   executable =
    //     "$workingDir/src/main/Il2CppOutputProject/IL2CPP/build/deploy/il2cpp$executableExtension"
    //   args(commandLineArgs)
    //   environment("ANDROID_SDK_ROOT", sdkDirectory)
    // }

    // val execAction = project.exec {
    //   providerExecSpec(executableExtension, commandLineArgs, sdkDirectory)
    // }

    // Create a copy task to move the file to the desired location
    // tasks.create("copyLibil2cpp$abi") {
    //   val builder = ProcessBuilder()
    //   builder.command("$workingDir/src/main/Il2CppOutputProject/IL2CPP/build/deploy/il2cpp$executableExtension", *commandLineArgs.toTypedArray())
    //   builder.environment()["ANDROID_SDK_ROOT"] = sdkDirectory
    //   val process = builder.start()
    //   process.waitFor()
    //
    //   val sourceFile = File("$workingDir/src/main/jniLibs/$abi/libil2cpp.dbg.so")
    //   val destinationFile = File("$workingDir/symbols/$abi/")
    //
    //   Files.copy(
    //     sourceFile.toPath(),
    //     destinationFile.toPath(),
    //     StandardCopyOption.REPLACE_EXISTING
    //   )
    //
    //   val newFileName = "libil2cpp.so"
    //   Files.move(
    //     destinationFile.toPath().resolve("libil2cpp.dbg.so"),
    //     destinationFile.toPath().resolve(newFileName),
    //     StandardCopyOption.REPLACE_EXISTING
    //   )
    //
    //   delete("$workingDir/src/main/jniLibs/$abi/libil2cpp.dbg.so")
    //   delete("$workingDir/src/main/jniLibs/$abi/libil2cpp.sym.so")
    // }
  }
}

private fun ExecSpec.providerExecSpec(
  executableExtension: String,
  commandLineArgs: MutableList<String>,
  sdkDirectory: String,
) {
  executable =
    "$workingDir/src/main/Il2CppOutputProject/IL2CPP/build/deploy/il2cpp$executableExtension"
  args(commandLineArgs)
  environment("ANDROID_SDK_ROOT", sdkDirectory)
}

private fun CopySpec.providerCopySpec(
  workingDir: String,
  abi: String,
) {
  from("$workingDir/src/main/jniLibs/$abi/libil2cpp.dbg.so")
  into("$workingDir/symbols/$abi/")
  rename("libil2cpp.dbg.so", "tlibil2cpp.so")
}

fun Project.createBuildIl2CppTask(
  unityOptions: UnityOptions,
) {
  tasks.register<BuildIl2CppTask>("buildIl2Cpp") {
    dependsOn("syncUnityAssets")
    setUnityOptions(unityOptions)
  }
}
