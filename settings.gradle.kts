pluginManagement {
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

rootProject.name = "Drifter"

// Drifter BoM
include(":drifter-bom")

// Drifter Modules
include(":drifter-compose")
include(":drifter-core")
include(":drifter-integration")
include(":drifter-plugin")
include(":drifter-wallpaper")

// includeBuild("examples")
