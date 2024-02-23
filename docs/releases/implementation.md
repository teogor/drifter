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

    [libraries]
    drifter-compose = { group = "dev.teogor.drifter", name = "compose", version.ref = "drifter" }
    drifter-core = { group = "dev.teogor.drifter", name = "core", version.ref = "drifter" }
    drifter-integration = { group = "dev.teogor.drifter", name = "integration", version.ref = "drifter" }
    drifter = { group = "dev.teogor.drifter", name = "drifter", version.ref = "" }
    drifter-wallpaper = { group = "dev.teogor.drifter", name = "wallpaper", version.ref = "drifter" }
    ```

=== "Using BoM"

    ```toml title="gradle/libs.versions.toml"
    [versions]
    drifter-bom = "1.0.0-alpha01"

    [libraries]
    drifter-bom = { group = "dev.teogor.drifter", name = "bom", version.ref = "drifter-bom" }
    drifter-compose = { group = "dev.teogor.drifter", name = "compose" }
    drifter-core = { group = "dev.teogor.drifter", name = "core" }
    drifter-integration = { group = "dev.teogor.drifter", name = "integration" }
    drifter = { group = "dev.teogor.drifter", name = "drifter" }
    drifter-wallpaper = { group = "dev.teogor.drifter", name = "wallpaper" }
    ```

#### Dependencies Implementation

=== "Kotlin"

    ```kotlin title="build.gradle.kts"
    dependencies {
      // When Using Drifter BoM
      implementation(platform(libs.drifter.bom))

      // Drifter Libraries
      implementation(libs.drifter.compose)
      implementation(libs.drifter.core)
      implementation(libs.drifter.integration)
      implementation(libs.drifter)
      implementation(libs.drifter.wallpaper)
    }
    ```

=== "Groovy"

    ```groovy title="build.gradle"
    dependencies {
      // When Using Drifter BoM
      implementation platform(libs.drifter.bom)

      // Drifter Libraries
      implementation libs.drifter.compose
      implementation libs.drifter.core
      implementation libs.drifter.integration
      implementation libs.drifter
      implementation libs.drifter.wallpaper
    }
    ```
