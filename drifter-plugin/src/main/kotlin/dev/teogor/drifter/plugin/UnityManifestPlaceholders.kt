package dev.teogor.drifter.plugin

import com.android.build.api.dsl.CommonExtension
import dev.teogor.drifter.plugin.models.UnityOptions

fun CommonExtension<*, *, *, *, *>.applyUnityManifestPlaceholders(
    unityOptions: UnityOptions,
) {
  defaultConfig {
    // Add meta fields to your manifest
    manifestPlaceholders += mapOf(
      "unity-options-splash-mode" to unityOptions.splashMode,
      "unity-options-splash-enable" to unityOptions.splashEnable,
      "unity-options-build-id" to unityOptions.buildId,
      "unity-options-version" to unityOptions.version,
      "unity-options-notch-config" to unityOptions.notchConfig,
    )
  }

  buildFeatures {
    buildConfig = true
  }
}
