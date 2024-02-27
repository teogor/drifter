[//]: # (This file was automatically generated - do not edit)

## Implementation

### Latest Version

The latest release is [`1.0.0-alpha01`](../releases.md)

### BoM Releases

The BoM (Bill of Materials) is the central repository for managing library versions within the
Drifter project. It streamlines the process of tracking the latest versions of key components and
dependencies, ensuring that your project remains up-to-date and compatible with the latest
advancements.

Here's a summary of the latest BoM versions:

|    Version    |               Release Notes                | Release Date |
|:-------------:|:------------------------------------------:|:------------:|
| 1.0.0-alpha01 | [changelog 🔗](changelog/1.0.0-alpha01.md) | 26 Sept 2023 |

### Using Version Catalog

#### Declare Components

This catalog provides the implementation details of Drifter libraries, including Build of
Materials (BoM) and individual libraries, in TOML format.

=== "Default"

    ```toml title="gradle/libs.versions.toml"
    [versions]
    drifter = "1.0.0-alpha01"
     = "1.0.0-alpha01"
    drifter-unity = "1.0.0-alpha01"

    [libraries]
    drifter-codegen = { group = "dev.teogor.drifter", name = "drifter-codegen", version.ref = "drifter" }
    drifter-common = { group = "dev.teogor.drifter", name = "drifter-common", version.ref = "drifter" }
    drifter-compose = { group = "dev.teogor.drifter", name = "drifter-compose", version.ref = "drifter" }
    drifter-integration = { group = "dev.teogor.drifter", name = "drifter-integration", version.ref = "drifter" }
    drifter = { group = "dev.teogor.drifter", name = "drifter", version.ref = "" }
    drifter-wallpaper = { group = "dev.teogor.drifter", name = "drifter-wallpaper", version.ref = "drifter" }
    drifter-ksp = { group = "dev.teogor.drifter", name = "drifter-ksp", version.ref = "drifter" }
    drifter-runtime = { group = "dev.teogor.drifter", name = "drifter-runtime", version.ref = "drifter" }
    drifter-unity-common = { group = "dev.teogor.drifter", name = "drifter-unity-common", version.ref = "drifter-unity" }
    drifter-unity-2022.3.7f1 = { group = "dev.teogor.drifter", name = "drifter-unity-2022-3-7f1", version.ref = "drifter-unity" }
    ```

=== "Using BoM"

    ```toml title="gradle/libs.versions.toml"
    [versions]
    drifter-bom = "1.0.0-alpha01"

    [libraries]
    drifter-bom = { group = "dev.teogor.drifter", name = "drifter-bom", version.ref = "drifter-bom" }
    drifter-codegen = { group = "dev.teogor.drifter", name = "drifter-codegen" }
    drifter-common = { group = "dev.teogor.drifter", name = "drifter-common" }
    drifter-compose = { group = "dev.teogor.drifter", name = "drifter-compose" }
    drifter-integration = { group = "dev.teogor.drifter", name = "drifter-integration" }
    drifter = { group = "dev.teogor.drifter", name = "drifter" }
    drifter-wallpaper = { group = "dev.teogor.drifter", name = "drifter-wallpaper" }
    drifter-ksp = { group = "dev.teogor.drifter", name = "drifter-ksp" }
    drifter-runtime = { group = "dev.teogor.drifter", name = "drifter-runtime" }
    drifter-unity-common = { group = "dev.teogor.drifter", name = "drifter-unity-Common" }
    drifter-unity-2022.3.7f1 = { group = "dev.teogor.drifter", name = "drifter-unity-2022-3-7f1" }
    ```

#### Dependencies Implementation

=== "Kotlin"

    ```kotlin title="build.gradle.kts"
    dependencies {
      // When Using Drifter BoM
      implementation(platform(libs.drifter.bom))

      // Drifter Libraries
      implementation(libs.drifter.codegen)
      implementation(libs.drifter.common)
      implementation(libs.drifter.compose)
      implementation(libs.drifter.integration)
      implementation(libs.drifter)
      implementation(libs.drifter.wallpaper)
      implementation(libs.drifter.ksp)
      implementation(libs.drifter.runtime)
      implementation(libs.drifter.unity.common)
      implementation(libs.drifter.unity.2022.3.7f1)
    }
    ```

=== "Groovy"

    ```groovy title="build.gradle"
    dependencies {
      // When Using Drifter BoM
      implementation platform(libs.drifter.bom)

      // Drifter Libraries
      implementation libs.drifter.codegen
      implementation libs.drifter.common
      implementation libs.drifter.compose
      implementation libs.drifter.integration
      implementation libs.drifter
      implementation libs.drifter.wallpaper
      implementation libs.drifter.ksp
      implementation libs.drifter.runtime
      implementation libs.drifter.unity.common
      implementation libs.drifter.unity.2022.3.7f1
    }
    ```
