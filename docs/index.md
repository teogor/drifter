# Drifter Repository

Drifter simplifies the integration between Unity and Android, enhancing performance seamlessly and
effortlessly.

## Download

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Maven Central](https://img.shields.io/maven-central/v/dev.teogor.drifter/bom.svg?label=Maven%20Central)](https://central.sonatype.com/search?q=g%3Adev.teogor.drifter+a%3Abom&smo=true)
[![API](https://img.shields.io/badge/API-24%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=24)
[![Profile](https://source.teogor.dev/badges/teogor-github.svg)](https://github.com/teogor)
[![Portfolio](https://source.teogor.dev/badges/teogor-dev.svg)](https://teogor.dev)

## Table of Contents

- [Modules](#modules)
  - [drifter-compose](#drifter-compose)
  - [drifter-integration](#drifter-integration)
  - [drifter-core](#drifter-core)
  - [drifter-wallpaper](#drifter-wallpaper)
  - [drifter-plugin](#drifter-plugin)
- [Getting Started](#getting-started)
- [Contributing](#contributing)
- [License](#license)

### Gradle

Add the dependency below to your **module**'s `build.*` file.

- for `build.gradle`

```gradle
dependencies {
  implementation platform('dev.teogor.drifter:bom:1.0.0-alpha01')
}
```

- for `build.gradle.kts`

```kotlin
dependencies {
  implementation(platform("dev.teogor.drifter:bom:1.0.0-alpha01"))
}
```

> **Note**: This library has more modules therefore include only the ones that you want to use.

## Simplifying Unity Engine Integration with Jetpack Compose

**Empowering seamless Unity experiences in your Android apps**

Drifter now offers powerful tools to integrate the Unity Engine directly into your Jetpack Compose
UI. Unlock a world of possibilities with the `drifter-compose` module, designed to simplify and
enhance your Compose development journey.

**Effortlessly Integrate Unity Engine:**

* Embed Unity content directly within your Jetpack Compose layouts using intuitive APIs.
* Enjoy smooth and responsive performance thanks to optimized rendering and resource management.
* Leverage pre-built components like `UnityEngineView` and `UnityEngineScaffold` to accelerate your
  development.
* Customize and adapt the integration to your specific needs and preferences.

**Explore the Documentation:**

Discover a comprehensive guide for `drifter-compose`
at [link to unity-engine-jetpack-compose](unity-engine-jetpack-compose.md).
Learn step-by-step instructions, explore code examples, and gain best practices to maximize the
potential of Unity Engine in your Compose projects.
n in a markdown code snippet format.

## Modules

### drifter-compose

The `drifter-compose` module provides functionality for working with Jetpack Compose in Android
applications. It includes utilities and components to simplify Compose-based UI development.

[Explore the source code](/drifter-compose)

### drifter-integration

The `drifter-integration` module offers integration points and connectors for third-party libraries
and services commonly used in Android development. It allows you to seamlessly integrate your
Android app with various services.

[Explore the source code](/drifter-integration)

### drifter-core

The `drifter-core` module contains core functionality and utilities that are commonly used across
Android projects. It provides a foundation for building robust Android applications.

[Explore the source code](/drifter-core)

### drifter-wallpaper

The `drifter-wallpaper` module focuses on wallpaper-related features and tools for Android. It
simplifies the development of live wallpapers and wallpaper-related functionalities.

[Explore the source code](/drifter-wallpaper)

### drifter-plugin

The `drifter-plugin` module provides a Gradle plugin that can be used to enhance your Android
project build process. It offers features such as code generation, resource management, and more.

This Gradle plugin simplifies Unity integration for your project by providing two essential tasks:

1. **BuildIl2CppTask**: Compiles and builds Il2Cpp for Unity integration.
2. **UnityAssetSyncTask**: Synchronizes Unity exported assets for project preparation.

[Explore the source code](/drifter-plugin)

## Find this repository useful? :heart:

Show your appreciation by starring this project :star: and joining our community of _
_[stargazers](https://github.com/teogor/drifter/stargazers)__.

Want to stay updated on my latest projects and contributions? Be sure to _
_[follow me](https://github.com/teogor)__ on GitHub! 🤩

# License

```xml
Designed and developed by 2023 teogor (Teodor Grigor)

  Licensed under the Apache License, Version 2.0 (the "License");you may not use this file except in compliance with the License.You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, softwaredistributed under the License is distributed on an "AS IS" BASIS,WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.See the License for the specific language governing permissions andlimitations under the License.
```
