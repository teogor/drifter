# Usage Guide for Plugin (Unity Integration Plugin)

## Overview

The Unity Integration Plugin simplifies the process of integrating Unity projects into your
Gradle-based project. It provides two essential tasks: `UnityNativeBuildTask`
and `RefreshUnityAssetsTask`. This guide explains how to use these tasks effectively.

## Prerequisites

Before using the Unity Integration Plugin, ensure the following:

- You have a Gradle-based Android project.
- Unity projects to be integrated have been properly exported.

## Installation

To get started, add the Unity Integration Plugin to your `build.gradle` file:

=== "Groovy"

    ```groovy title="build.gradle"
    apply plugin: 'dev.teogor.drifter.plugin'
    ```

=== "Kotlin"

    ```kotlin title="build.gradle.kts"
    plugins {
        id("dev.teogor.drifter.plugin")
    }
    ```

## Using `UnityNativeBuildTask`

### Purpose

The `UnityNativeBuildTask` compiles and builds native code for Unity integration, managing
architecture-specific binaries and output directories. It enhances the efficiency of your
development workflow by automating the complex build processes required for Unity native code.

### Configuration

To use `UnityNativeBuildTask`, follow these steps:

1. Configure Unity options in your `build.gradle` file:

=== "Groovy"

    ```groovy title="build.gradle"
    android {
        namespace = "dev.teogor.drifter.demo.module.unity"

        unityOptions {
            splashMode = 0
            splashEnable = true
            buildId = "ea574d1a-3365-44b3-9676-33bceabcf351"
            notchConfig = "portrait|landscape"
            version = "2022.3.7f1"

            ndkVersion = "23.1.7779620"
            ndkPath = getSafeDrifterUnityPathNdk()

            platforms = [
               PlatformArch.Arm64,
               PlatformArch.Armv7
            ]
            configuration = Configuration.Release
            streamingAssets.addAll(unityStreamingAssetsList)

            exportedProjectLocation = getSafeDrifterUnityPathExport()
            libraryName = 'YourUnityLibrary'
        }
    }
    ```

=== "Kotlin"

    ```kotlin title="build.gradle.kts"
    val unityStreamingAssets: String? by project
    val unityStreamingAssetsList: List<String> = unityStreamingAssets?.split(",") ?: emptyList()

    android {
        namespace = "dev.teogor.drifter.demo.module.unity"

        unityOptions(
            androidConfig = this,
        ) {
            splashMode = 0
            splashEnable = true
            buildId = "ea574d1a-3365-44b3-9676-33bceabcf351"
            notchConfig = "portrait|landscape"
            version = "2022.3.7f1"

            ndkVersion = "23.1.7779620"
            ndkPath = getSafeDrifterUnityPathNdk()

            platforms = listOf(
              PlatformArch.Arm64,
              PlatformArch.Armv7
            )
            configuration = Configuration.Release
            streamingAssets += unityStreamingAssetsList

            exportedProjectLocation = getSafeDrifterUnityPathExport()
            libraryName = "YourUnityLibrary"
        }
    }
    ```

2. Create the task and set Unity options:

=== "Groovy"

    ```groovy title="build.gradle"
    createUnityNativeBuildTask(unityOptions)
    ```

=== "Kotlin"

    ```kotlin title="build.gradle.kts"
    createUnityNativeBuildTask(unityOptions)
    ```

3. Execute the task:

```shell
./gradlew unityNativeBuild
```

Here's a Kotlin code snippet for your `build.gradle` file that demonstrates the configuration and
usage:

=== "Kotlin"

    ```kotlin title="build.gradle.kts"
    // Configure Unity options
    val unityOptions = mapOf(
        "exportFolder" to "path/to/unity/export/folder",
        "libraryName" to "YourUnityLibrary"
        // Add more configuration options as needed
    )

    // Create and set up the UnityNativeBuildTask
    createUnityNativeBuildTask(unityOptions)

    // Execute the UnityNativeBuildTask
    tasks.named("unityNativeBuild").configure {
        // Add any additional configuration or dependencies here if needed
        // For example:
        // dependsOn("someOtherTask")
    }
    ```

## Using `RefreshUnityAssetsTask`

### Purpose

The `RefreshUnityAssetsTask` streamlines the synchronization and updating of Unity exported assets
with your project. It prepares the project for Unity integration by updating essential folders and
removing outdated content.

### Configuration

To use `RefreshUnityAssetsTask`, follow these steps:

1. Configure Unity options in your `build.gradle` file (if not already done).

2. Create the task and set Unity options:

=== "Groovy"

    ```groovy title="build.gradle"
    createRefreshUnityAssetsTask(unityOptions)
    ```

=== "Kotlin"

    ```kotlin title="build.gradle.kts"
    createRefreshUnityAssetsTask(unityOptions)
    ```

3. Execute the task:

```shell
./gradlew refreshUnityAssets
```

Here's a Kotlin code snippet for your `build.gradle` file that demonstrates the configuration and
usage:

=== "Kotlin"

    ```kotlin title="build.gradle.kts"
    // Create and set up the RefreshUnityAssetsTask
    createRefreshUnityAssetsTask(unityOptions)

    // Execute the RefreshUnityAssetsTask
    tasks.named("refreshUnityAssets").configure {
        // Add any additional configuration or dependencies here if needed
        // For example:
        // dependsOn("anotherTask")
    }
    ```

## Conclusion

With the Unity Integration Plugin, you can seamlessly integrate Unity projects into your
Gradle-based project, enhancing the development process and ensuring consistency. If you encounter
any issues or have feature requests, please don't hesitate to contribute or reach out for support.

## Quick References

1. [Installation](#installation)
2. [Using `UnityNativeBuildTask`](#using-unitynativebuildtask)
3. [Using `RefreshUnityAssetsTask`](#using-refreshunityassetstask)
4. [Conclusion](#conclusion)
