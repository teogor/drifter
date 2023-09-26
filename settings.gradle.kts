pluginManagement {
  includeBuild("drifter-plugin/")
  repositories {
    google()
    mavenCentral()
    gradlePluginPortal()
  }
}

dependencyResolutionManagement {
  repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
  repositories {
    google()
    mavenCentral()
  }
}

rootProject.name = "Drifter"

// Demo Modules
include(":app")
include(":module-unity")

// Drifter BoM
include(":drifter-bom")

// Drifter Modules
include(":drifter-integration")
include(":drifter-compose")
include(":drifter-core")
include(":drifter-wallpaper")
