pluginManagement {
  includeBuild("${rootProject.projectDir}\\drifter-plugin") {
    name = "drifter-gradle-plugin-test"
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
include(":app")
include(":module-unity")

// Drifter
include(":drifter-bom")
include(":drifter-common")
include(":drifter-compose")
include(":drifter-integration")
include(":drifter-plugin")
include(":drifter-wallpaper")
include(":codegen")
include(":ksp")
include(":runtime")
include(":unity:common")
include(":unity:v2022-3-7f1")
