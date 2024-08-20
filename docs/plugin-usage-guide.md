# Usage Guide for Plugin (Unity Integration Plugin)

## Overview

The Unity Integration Plugin simplifies the process of integrating Unity projects into your Gradle-based project. It provides two essential tasks: `BuildIl2CppTask` and `UnityAssetSyncTask`. This guide explains how to use these tasks effectively.

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

## Using `BuildIl2CppTask`

### Purpose

The `BuildIl2CppTask` automates the compilation and building of Il2Cpp for Unity integration. It enhances the efficiency of your development workflow by handling complex build processes.

### Configuration

To use `BuildIl2CppTask`, follow these steps:

1. Configure Unity options in your `build.gradle` file:

=== "Groovy"

    ```groovy title="build.gradle"
    unityOptions {
        exportFolder = 'path/to/unity/export/folder'
        libraryName = 'YourUnityLibrary'
        // Add more configuration options as needed
    }
    ```

=== "Kotlin"

    ```kotlin title="build.gradle.kts"
    val unityOptions = mapOf(
        "exportFolder" to "path/to/unity/export/folder",
        "libraryName" to "YourUnityLibrary"
        // Add more configuration options as needed
    )
    ```

2. Create the task and set Unity options:

=== "Groovy"

    ```groovy title="build.gradle"
    createBuildIl2CppTask(unityOptions)
    ```

=== "Kotlin"

    ```kotlin title="build.gradle.kts"
    createBuildIl2CppTask(unityOptions)
    ```

3. Execute the task:

```shell
./gradlew buildIl2Cpp
```

Here's a Kotlin code snippet for your `build.gradle` file that demonstrates the configuration and usage:

=== "Kotlin"

    ```kotlin title="build.gradle.kts"
    // Configure Unity options
    val unityOptions = mapOf(
        "exportFolder" to "path/to/unity/export/folder",
        "libraryName" to "YourUnityLibrary"
        // Add more configuration options as needed
    )

    // Create and set up the BuildIl2CppTask
    createBuildIl2CppTask(unityOptions)

    // Execute the BuildIl2CppTask
    tasks.named("buildIl2Cpp").configure {
        // Add any additional configuration or dependencies here if needed
        // For example:
        // dependsOn("someOtherTask")
    }
    ```

### Using `UnityAssetSyncTask`

### Purpose

The `UnityAssetSyncTask` streamlines the synchronization of Unity exported assets with your project. It prepares the project for Unity integration by copying essential folders.

### Configuration

To use `UnityAssetSyncTask`, follow these steps:

1. Configure Unity options in your `build.gradle` file (if not already done).

2. Create the task and set Unity options:

=== "Groovy"

    ```groovy title="build.gradle"
    createUnityAssetSyncTask(unityOptions)
    ```

=== "Kotlin"

    ```kotlin title="build.gradle.kts"
    createUnityAssetSyncTask(unityOptions)
    ```

3. Execute the task:

```shell
./gradlew syncUnityAssets
```

Here's a Kotlin code snippet for your `build.gradle` file that demonstrates the configuration and usage:

=== "Kotlin"

    ```kotlin title="build.gradle.kts"
    // Create and set up the UnityAssetSyncTask
    createUnityAssetSyncTask(unityOptions)

    // Execute the UnityAssetSyncTask
    tasks.named("syncUnityAssets").configure {
        // Add any additional configuration or dependencies here if needed
        // For example:
        // dependsOn("anotherTask")
    }
    ```

## Conclusion

With the Unity Integration Plugin, you can seamlessly integrate Unity projects into your Gradle-based project, enhancing the development process and ensuring consistency. If you encounter any issues or have feature requests, please don't hesitate to contribute or reach out for support.

## Quick References

1. [Installation](#installation)
2. [Using `BuildIl2CppTask`](#using-buildil2cpptask)
3. [Using `UnityAssetSyncTask`](#using-unityassetsynctask)
4. [Conclusion](#conclusion)
