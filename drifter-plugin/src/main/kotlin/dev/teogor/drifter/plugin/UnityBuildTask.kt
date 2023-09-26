package dev.teogor.drifter.plugin

import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.SdkComponents
import com.android.build.api.variant.AndroidComponents
import dev.teogor.drifter.plugin.models.PlatformArch
import dev.teogor.drifter.plugin.models.UnityOptions
import org.gradle.api.Project


fun Project.unityBuildTask(
  commonExtension: CommonExtension<*, *, *, *, *>,
  unityOptions: UnityOptions,
) {

  val buildIl2CppTask = registerBuildIl2CppTask(unityOptions)

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
      if (project(path).tasks.named("mergeDebugJniLibFolders").getOrNull() != null) {
        project(path).tasks.named("mergeDebugJniLibFolders").get()
          .dependsOn(buildIl2CppTask)
      }
      if (project(path).tasks.named("mergeReleaseJniLibFolders").getOrNull() != null) {
        project(path).tasks.named("mergeReleaseJniLibFolders").get()
          .dependsOn(buildIl2CppTask)
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

private fun Project.registerBuildIl2CppTask(
  unityOptions: UnityOptions,
) = tasks.register("BuildIl2CppTask") {
  doLast {
    val workingDir = projectDir.toString().replace("\\\\", "/")
    val configuration = unityOptions.configuration.value
    val staticLibraries = emptyArray<String>()

    val android = project.extensions.getByType(AndroidComponents::class.java)
    val sdkComponents = android.sdkComponents

    unityOptions.platforms.forEach { architecture ->
      buildIl2Cpp(
        workingDir = workingDir,
        configuration = configuration,
        architecture = architecture,
        staticLibraries = staticLibraries,
        sdkComponents = sdkComponents,
      )
    }
  }
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
    "--baselib-directory=${workingDir}/src/main/jniStaticLibs/$abi",
    "--incremental-g-c-time-slice=3",
    "--configuration=$configuration",
    "--dotnetprofile=unityaot-linux",
    "--profiler-report",
    "--profiler-output-file=${workingDir}/build/il2cpp_${abi}_$configuration/il2cpp_conv.traceevents",
    "--print-command-line",
    "--data-folder=${workingDir}/src/main/Il2CppOutputProject/Source/il2cppOutput/data",
    "--generatedcppdir=${workingDir}/src/main/Il2CppOutputProject/Source/il2cppOutput",
    "--cachedirectory=${workingDir}/build/il2cpp_${abi}_$configuration/il2cpp_cache",
    "--tool-chain-path=${ndkDirectory}",
  )

  staticLibraries.forEachIndexed { _, fileName ->
    commandLineArgs.add("--additional-libraries=${workingDir}/src/main/jniStaticLibs/$abi/$fileName")
  }

  var executableExtension = ""
  if (org.gradle.internal.os.OperatingSystem.current().isWindows) {
    executableExtension = ".exe"
    commandLineArgs.replaceAll { it.replace("\"", "\\\"") }
  }

  val execAction = project.exec {
    executable =
      "${workingDir}/src/main/Il2CppOutputProject/IL2CPP/build/deploy/il2cpp$executableExtension"
    args(commandLineArgs)
    environment("ANDROID_SDK_ROOT", sdkDirectory)
  }

  // Create a copy task to move the file to the desired location
  tasks.create("copyLibil2cpp$abi") {
    dependsOn(execAction)
    copy {
      from("${workingDir}/src/main/jniLibs/$abi/libil2cpp.dbg.so")
      into("${workingDir}/symbols/$abi/")
      rename("libil2cpp.dbg.so", "tlibil2cpp.so")
    }
    delete("${workingDir}/src/main/jniLibs/$abi/libil2cpp.dbg.so")
    delete("${workingDir}/src/main/jniLibs/$abi/libil2cpp.sym.so")
  }
}
