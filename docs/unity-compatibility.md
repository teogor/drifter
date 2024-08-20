# Unity Module Compatibility

Drifter is a comprehensive library designed to enhance and streamline your Unity development
workflow. This document outlines the specific Unity versions that the Drifter library supports.

## Supported Unity Versions

| Version         | Release Date | Release Notes                                                       | Hub Installation                   | Downloads                                                                 |
|-----------------|--------------|---------------------------------------------------------------------|------------------------------------|---------------------------------------------------------------------------|
| **2022.3.42f1** | 13 Aug 2024  | [Read](https://unity.com/releases/editor/whats-new/2022.3.42#notes) | [Install](unityhub://2022.3.42f1/) | [See all](https://unity.com/releases/editor/whats-new/2022.3.42#installs) |
| **2022.3.7f1**  | 8 Aug 2023   | [Read](https://unity.com/releases/editor/whats-new/2022.3.7#notes)  | [Install](unityhub://2022.3.7f1/)  | [See all](https://unity.com/releases/editor/whats-new/2022.3.7#installs)  |

## How to Use

To ensure compatibility and take full advantage of the features provided by Drifter, make sure you
are using one of the supported Unity versions listed above.

### Installation Steps

1. Import the Drifter library into your project.
2. Import the relevant Unity module from Drifter that suits your project requirements.
   Here’s the refined section with the installation steps incorporating the code snippets:

### Installation Steps

1. Import the Drifter library into your project.

   For more details, view the [implementation guide](releases/implementation.md).

2. Import the relevant Unity module from Drifter that suits your project requirements.

   For example, to implement the Drifter Unity module for `v2022.3.7f1`, add the following
   dependency:

   ```kotlin
   dependencies {
      implementation("dev.teogor.drifter:drifter-unity-2022-3-7f1:$version")
   }
   ```

3. Set up your `Application` class to initialize Drifter with the appropriate Unity version.

   Update your `Application` class as follows:

   ```kotlin
   class DemoApplication : Application() {

     override fun onCreate() {
       super.onCreate()

       // Initialize Drifter with the Unity factory for version 2022.3.7f1
       RegistryStartup.provides(withUnity202237f1Factory())
     }
   }
   ```

Alternatively, you can use `LocalUnityEngine` to provide the Unity factory:

   ```kotlin
   LocalUnityEngine provide Unity202237f1Factory()
   ```

## Need Help?

If you encounter any issues or have questions regarding compatibility, feel free to open an issue on
the [GitHub Issues page](https://github.com/teogor/drifter/issues).

## Quick References

1. [Supported Unity Versions](#supported-unity-versions)
2. [How to Use](#how-to-use)
3. [Installation Steps](#installation-steps)
4. [Need Help?](#need-help)
