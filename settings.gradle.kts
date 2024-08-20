pluginManagement {
  // TODO better way of testing and including plugin
  includeBuild("${rootProject.projectDir}\\gradle-plugin") {
    name = "drifter-plugin"
  }
  repositories {
    google()
    mavenCentral()
    gradlePluginPortal()
  }
}

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
  repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
  repositories {
    google()
    mavenCentral()
  }
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "Drifter"

// Demo Application
include(":demo:app")
include(":demo:module-unity")

// Drifter
include(":bom")
include(":codegen")
include(":compiler")
include(":compose")
include(":core")
include(":integration")
include(":runtime")
include(":wallpaper")

// Plugin
include(":gradle-plugin")

// Unity
include(":unity:common")
include(":unity:v2022-3-7f1")
