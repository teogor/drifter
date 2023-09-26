package dev.teogor.publish

import dev.teogor.publish.model.PublishPluginExtension
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.DependencyConstraintHandler


fun Project.configureBomModule(
  publishOptions: PublishPluginExtension,
) {
  collectBomConstraints(publishOptions)
}

private fun Project.collectBomConstraints(
  publishOptions: PublishPluginExtension,
) {
  val bomConstraints: DependencyConstraintHandler = dependencies.constraints
  val bomOptions = publishOptions.bomOptions

  rootProject.subprojects {
    val subproject = this

    if (bomOptions.acceptedModules.contains(subproject.name)) {
      bomConstraints.api(subproject)
    }
  }
}

private fun DependencyConstraintHandler.api(
  constraintNotation: Any,
) = add("api", constraintNotation)
