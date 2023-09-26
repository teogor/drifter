# Unity Module

This module contains Unity-related assets and libraries for your Android project.

## Unity Configuration

To configure Unity integration with the Android project, use the following options:

- **Splash Mode:** Set to `0` or `1`.
- **Splash Enable:** Disable (`false`) or Enabled (`true`).
- **Build ID:** `"your_build_id"`.
- **Notch Configuration:** `"portrait|landscape"`.
- **Unity Version:** `"your_unity_version"`.

## Android NDK Configuration

For Android NDK integration, use the following settings:

- **NDK Version:** `"your_ndk_version"` (Tested with `"23.1.7779620"`).
- **NDK Path:** Specify the path to your NDK installation.

## Supported Platforms

This module supports the following Android platforms:

- **armeabi-v7a** (Architecture: "armv7").
- **arm64-v8a** (Architecture: "arm64").

## Build Configuration

- **Configuration:** Release.

## Streaming Assets

Streaming assets can be configured using the `unityStreamingAssetsList`. Please ensure the required Unity assets are included.

## Export Folder

The Unity export folder is set to `"your_export_folder"`. Adjust this path to your project's export location.

Please make sure to configure the paths and settings according to your project's setup and requirements.

## Example Configuration
```kt
android {
    unityOptions(
        androidConfig = this,
    ) {
        // Unity Configuration
        splashMode = 0
        splashEnable = true
        buildId = "your_build_id"
        notchConfig = "portrait|landscape"
        version = "your_unity_version"

        // Android NDK Configuration
        ndkVersion = "your_ndk_version"
        ndkPath = "path_to_your_ndk"

        // Supported Platforms
        platforms = listOf(
            PlatformArch("armeabi-v7a", "armv7"),
            PlatformArch("arm64-v8a", "arm64"),
        )

        // Build Configuration
        configuration = Configuration.Release

        // Streaming Assets (Optional)
        streamingAssets += unityStreamingAssetsList

        // Export Folder
        exportFolder = "path_to_your_export_folder"
    }
}
```

Replace `"your_build_id"`, `"your_unity_version"`, `"your_ndk_version"`, `"path_to_your_ndk"`, and `"path_to_your_export_folder"` with the appropriate values for your project. This template demonstrates how to configure Unity options within your Android project's Gradle build file.

## Import Unity Assets

To import Unity assets and libraries into this module, you can use the `importUnityExport` Gradle command. This command allows you to bring in the following folders:

- `/symbols`
- `/src/main/assets/bin/Data/`
- `/src/main/assets/UnityServicesProjectConfiguration.json`
- `/src/main/Il2CppOutputProject/`
- `/src/main/jniLibs/`
- `/src/main/jniStaticLibs/`

Here's how you can run the `importUnityExport` command:

```bash
./gradlew :module-unity:importUnityExport
```
