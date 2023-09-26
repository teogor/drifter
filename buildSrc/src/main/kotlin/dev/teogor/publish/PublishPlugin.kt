package dev.teogor.publish

import dev.teogor.publish.model.PublishPluginExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

class PublishPlugin : Plugin<Project> {
  override fun apply(target: Project) {
    with(target) {
      val publishOptions = target.extensions.create(
        "publishOptions",
        PublishPluginExtension::class.java,
      )

      pluginManager.apply("com.vanniktech.maven.publish")

      afterEvaluate {
        if(publishOptions.isBomModule) {
          configureBomModule(publishOptions)
        } else if(publishOptions.isPlugin) {

        } else {

        }
      }
    }
  }
}
