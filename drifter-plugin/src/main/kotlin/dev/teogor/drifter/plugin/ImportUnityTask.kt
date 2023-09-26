package dev.teogor.drifter.plugin

import dev.teogor.drifter.plugin.models.UnityOptions
import org.gradle.api.Project

fun Project.importUnityTask(
    unityOptions: UnityOptions,
) {

  tasks.register("importUnityExport") {
    description = "Import essential folders from Unity export and prepare project"

    doLast {
      val sourceDir = file(unityOptions.exportFolder)
      val targetDir = file(projectDir.path)

      val targetedUnityFolders = listOf(
        "assets",
        "Il2CppOutputProject",
        "jniLibs",
        "jniStaticLibs",
      )
      val targetedUnitySymbols = listOf(
        "symbols",
      )

      val rootDirectory = file(targetDir.absolutePath)
      val srcDirectory = file("${rootDirectory.absolutePath}\\src\\main")

      // Delete the specified folders
      targetedUnitySymbols.forEach { folderName ->
        val folder = rootDirectory.resolve(folderName)
        if (folder.exists()) {
          folder.deleteRecursively()
        }
      }
      targetedUnityFolders.forEach { folderName ->
        val folder = srcDirectory.resolve(folderName)
        if (folder.exists()) {
          folder.deleteRecursively()
        }
      }

      val unityExportLibName = unityOptions.libraryName
      val exportRootDirectory = file("${sourceDir.absolutePath}\\$unityExportLibName")
      val exportSrcDirectory = file("${exportRootDirectory.absolutePath}\\src\\main")

      targetedUnitySymbols.forEach { folderName ->
        val folder = exportRootDirectory.resolve(folderName)
        if (folder.exists()) {
          copy {
            from(folder)
            into(file("$rootDirectory\\$folderName"))
          }
        }
      }

      targetedUnityFolders.forEach { folderName ->
        val folder = exportSrcDirectory.resolve(folderName)
        if (folder.exists()) {
          copy {
            from(folder)
            into(file("$srcDirectory\\$folderName"))
          }
        }
      }
    }
  }

}
