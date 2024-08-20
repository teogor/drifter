# BoM

[//]: # (REGION-DEPENDENCIES)

## Getting Started with BoM

**Adding Dependencies:**

* **Manual Setup:**  This section guides you through adding BoM dependencies directly to your project's `build.gradle` files. ([Link to Manual Dependency Setup Section](#adding-bom-dependencies-manually))
* **Version Catalog (Recommended):** For a more streamlined approach, consider integrating a version catalog. This allows for centralized version management and easier updates. ([Link to Version Catalog Section](#managing-bom-versions-with-version-catalog-recommended))

**Note:** If you prefer manual dependency setup, follow the instructions in the "Manual Setup" section. Otherwise, jump to the "Version Catalog" section for centralized management.

For information on using the KAPT plugin, see the [KAPT documentation](https://kotlinlang.org/docs/kapt.html).  
For information on using the KSP plugin, see the [KSP quick-start documentation](https://kotlinlang.org/docs/ksp-quickstart.html).  
For more information about dependencies, see [Add Build Dependencies](https://developer.android.com/studio/build/dependencies).  

### Adding BoM Dependencies Manually

To use BoM in your app, add the following dependencies to your app's `build.gradle` file:

=== "Groovy"

    ```groovy title="build.gradle"
    dependencies {
        def teogorDrifterBom = "1.0.0-alpha02"
        
        implementation "dev.teogor.drifter:drifter-bom:$teogorDrifterBom"
        implementation "dev.teogor.drifter:drifter-codegen:$teogorDrifterBom"
        
        // To use Kotlin annotation processing tool (kapt)
        kapt "dev.teogor.drifter:drifter-ksp:$teogorDrifterBom"
        // To use Kotlin Symbol Processing (KSP)
        ksp "dev.teogor.drifter:drifter-ksp:$teogorDrifterBom"
        implementation "dev.teogor.drifter:drifter-compose:$teogorDrifterBom"
        implementation "dev.teogor.drifter:drifter-core:$teogorDrifterBom"
        implementation "dev.teogor.drifter:drifter-integration:$teogorDrifterBom"
        implementation "dev.teogor.drifter:drifter-runtime:$teogorDrifterBom"
        implementation "dev.teogor.drifter:drifter-wallpaper:$teogorDrifterBom"
        implementation "dev.teogor.drifter:drifter-unity-common:$teogorDrifterBom"
        implementation "dev.teogor.drifter:drifter-unity-2022-3-7f1:$teogorDrifterBom"
    }
    ```

=== "Kotlin"

    ```kotlin title="build.gradle.kts"
    dependencies {
        val teogorDrifterBom = "1.0.0-alpha02"
        
        implementation("dev.teogor.drifter:drifter-bom:$teogorDrifterBom")
        implementation("dev.teogor.drifter:drifter-codegen:$teogorDrifterBom")
        
        // To use Kotlin annotation processing tool (kapt)
        kapt("dev.teogor.drifter:drifter-ksp:$teogorDrifterBom")
        // To use Kotlin Symbol Processing (KSP)
        ksp("dev.teogor.drifter:drifter-ksp:$teogorDrifterBom")
        implementation("dev.teogor.drifter:drifter-compose:$teogorDrifterBom")
        implementation("dev.teogor.drifter:drifter-core:$teogorDrifterBom")
        implementation("dev.teogor.drifter:drifter-integration:$teogorDrifterBom")
        implementation("dev.teogor.drifter:drifter-runtime:$teogorDrifterBom")
        implementation("dev.teogor.drifter:drifter-wallpaper:$teogorDrifterBom")
        implementation("dev.teogor.drifter:drifter-unity-common:$teogorDrifterBom")
        implementation("dev.teogor.drifter:drifter-unity-2022-3-7f1:$teogorDrifterBom")
    }
    ```

### Managing BoM Versions with Version Catalog (Recommended)

This section guides you through utilizing a version catalog for centralized management of BoM dependencies in your project. This approach simplifies updates and ensures consistency.

First, define the dependencies in the `libs.versions.toml` file:

- **Group-Name Based:** This approach is used for declaring libraries referenced by group and artifact name.
- **Module Based:** This approach is used for declaring libraries referenced by their module.

=== "Group-Name Based"

    ```toml title="gradle/libs.versions.toml"
    [versions]
    teogor-drifter-bom = "1.0.0-alpha02"
    
    [libraries]
    teogor-drifter-bom = { group = "dev.teogor.drifter", name = "drifter-bom", version.ref = "teogor-drifter-bom" }
    teogor-drifter-codegen = { group = "dev.teogor.drifter", name = "drifter-codegen" }
    teogor-drifter-ksp = { group = "dev.teogor.drifter", name = "drifter-ksp" }
    teogor-drifter-compose = { group = "dev.teogor.drifter", name = "drifter-compose" }
    teogor-drifter-core = { group = "dev.teogor.drifter", name = "drifter-core" }
    teogor-drifter-integration = { group = "dev.teogor.drifter", name = "drifter-integration" }
    teogor-drifter-runtime = { group = "dev.teogor.drifter", name = "drifter-runtime" }
    teogor-drifter-wallpaper = { group = "dev.teogor.drifter", name = "drifter-wallpaper" }
    teogor-drifter-unity-common = { group = "dev.teogor.drifter", name = "drifter-unity-common" }
    teogor-drifter-unity-2022-3-7f1 = { group = "dev.teogor.drifter", name = "drifter-unity-2022-3-7f1" }
    ```

=== "Module Based"

    ```toml title="gradle/libs.versions.toml"
    [versions]
    teogor-drifter-bom = "1.0.0-alpha02"
    
    [libraries]
    teogor-drifter-bom = { module = "dev.teogor.drifter:drifter-bom", version.ref = "teogor-drifter-bom" }
    teogor-drifter-codegen = { module = "dev.teogor.drifter:drifter-codegen" }
    teogor-drifter-ksp = { module = "dev.teogor.drifter:drifter-ksp" }
    teogor-drifter-compose = { module = "dev.teogor.drifter:drifter-compose" }
    teogor-drifter-core = { module = "dev.teogor.drifter:drifter-core" }
    teogor-drifter-integration = { module = "dev.teogor.drifter:drifter-integration" }
    teogor-drifter-runtime = { module = "dev.teogor.drifter:drifter-runtime" }
    teogor-drifter-wallpaper = { module = "dev.teogor.drifter:drifter-wallpaper" }
    teogor-drifter-unity-common = { module = "dev.teogor.drifter:drifter-unity-common" }
    teogor-drifter-unity-2022-3-7f1 = { module = "dev.teogor.drifter:drifter-unity-2022-3-7f1" }
    ```

Then, add these dependencies in your app's `build.gradle` file:

=== "Groovy"

    ```groovy title="build.gradle"
    dependencies {
        implementation platform(libs.teogor.drifter.bom)
        implementation libs.teogor.drifter.codegen
        
        // To use Kotlin annotation processing tool (kapt)
        kapt libs.teogor.drifter.ksp
        // To use Kotlin Symbol Processing (KSP)
        ksp libs.teogor.drifter.ksp
        implementation libs.teogor.drifter.compose
        implementation libs.teogor.drifter.core
        implementation libs.teogor.drifter.integration
        implementation libs.teogor.drifter.runtime
        implementation libs.teogor.drifter.wallpaper
        implementation libs.teogor.drifter.unity.common
        implementation libs.teogor.drifter.unity.2022.3.7f1
    }
    ```

=== "Kotlin"

    ```kotlin title="build.gradle.kts"
    dependencies {
        implementation(platform(libs.teogor.drifter.bom))
        implementation(libs.teogor.drifter.codegen)
        
        // To use Kotlin annotation processing tool (kapt)
        kapt(libs.teogor.drifter.ksp)
        // To use Kotlin Symbol Processing (KSP)
        ksp(libs.teogor.drifter.ksp)
        implementation(libs.teogor.drifter.compose)
        implementation(libs.teogor.drifter.core)
        implementation(libs.teogor.drifter.integration)
        implementation(libs.teogor.drifter.runtime)
        implementation(libs.teogor.drifter.wallpaper)
        implementation(libs.teogor.drifter.unity.common)
        implementation(libs.teogor.drifter.unity.2022.3.7f1)
    }
    ```

[//]: # (REGION-DEPENDENCIES)

