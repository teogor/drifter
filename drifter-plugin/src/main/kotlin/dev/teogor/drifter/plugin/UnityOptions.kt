package dev.teogor.drifter.plugin

import com.android.build.api.dsl.CommonExtension
import dev.teogor.drifter.plugin.models.UnityOptions
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

/**
 * Configure BuildConfig-specific options for the Android project.
 *
 * @param commonExtension The common extension of the Android project.
 */
fun Project.unityOptions(
    androidConfig: CommonExtension<*, *, *, *, *>,
    configure: UnityOptions.() -> Unit,
) {
  val unityOptions = UnityOptions().apply(configure)

  androidConfig.applyUnityManifestPlaceholders(
    unityOptions = unityOptions,
  )
  unityBuildTask(
    commonExtension = androidConfig,
    unityOptions = unityOptions,
  )

  createUnityAssetSyncTask(
    unityOptions = unityOptions,
  )

  androidConfig.apply {
    dependencies {
      // add("api", project(":unity-wallpaper"))
    }
  }
}
