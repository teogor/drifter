package dev.teogor.publish

import dev.teogor.publish.model.PublishPluginExtension
import org.gradle.api.publish.maven.MavenPom

private fun MavenPom.info(
  publishOptions: PublishPluginExtension,
) {
  val artifactName = publishOptions.name.lowercase() + "-" + publishOptions.artifactId.lowercase()
  name.set(artifactName)
  description.set(publishOptions.description)
  inceptionYear.set(publishOptions.inceptionYear)
  url.set(publishOptions.url)
}

private fun MavenPom.license(
  publishOptions: PublishPluginExtension,
) {
  licenses {
    license {
      publishOptions.licenseType.let { license ->
        name.set(license.title)
        url.set(license.url)
        distribution.set(license.distribution)
      }
    }
  }
}

private fun MavenPom.developers(
  publishOptions: PublishPluginExtension,
) {
  developers {
    publishOptions.developers.forEach { developer ->
      developer {
        id.set(developer.id)
        name.set(developer.name)
        url.set(developer.url)
        roles.set(developer.roles)
        timezone.set(developer.timezone)
        organization.set(developer.organization)
        organizationUrl.set(developer.organizationUrl)
      }
    }
  }
}

private fun MavenPom.scm(
  publishOptions: PublishPluginExtension,
) {
  scm {
    url.set(publishOptions.scmUrl)
    connection.set(publishOptions.scmConnection)
    developerConnection.set(publishOptions.scmDeveloperConnection)
  }
}

fun MavenPom.applyPublishOptions(
  publishOptions: PublishPluginExtension,
) = apply {
  info(publishOptions)
  license(publishOptions)
  developers(publishOptions)
  scm(publishOptions)
}
