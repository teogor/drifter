import com.vanniktech.maven.publish.SonatypeHost
import dev.teogor.winds.api.MavenPublish
import dev.teogor.winds.api.getValue
import dev.teogor.winds.api.model.DependencyType
import dev.teogor.winds.api.model.Developer
import dev.teogor.winds.api.model.LicenseType
import dev.teogor.winds.api.model.createVersion
import dev.teogor.winds.api.provider.Scm
import dev.teogor.winds.gradle.utils.afterWindsPluginConfiguration
import dev.teogor.winds.gradle.utils.attachTo
import org.jetbrains.dokka.gradle.DokkaPlugin

plugins {
  alias(libs.plugins.android.application) apply false
  alias(libs.plugins.android.library) apply false
  alias(libs.plugins.jetbrains.kotlin.android) apply false

  alias(libs.plugins.ceres.android.application) apply false
  alias(libs.plugins.ceres.android.application.compose) apply false
  alias(libs.plugins.ceres.android.library) apply false
  alias(libs.plugins.ceres.android.library.compose) apply false

  alias(libs.plugins.winds) apply true

  alias(libs.plugins.vanniktech.maven) apply true
  alias(libs.plugins.dokka) apply true
  alias(libs.plugins.spotless) apply true
  alias(libs.plugins.api.validator) apply true
}

winds {
  buildFeatures {
    mavenPublish = true

    docsGenerator = true
  }

  mavenPublish {
    displayName = "Drifter"
    name = "drifter"

    canBePublished = false

    description =
      "\uD83C\uDFAE Drifter simplifies the integration between Unity and Android, enhancing performance seamlessly and effortlessly."

    groupId = "dev.teogor.drifter"
    artifactIdElements = 1
    url = "https://source.teogor.dev/drifter"

    version = createVersion(1, 0, 0) {
      alphaRelease(1)
    }

    // TODO winds
    //  required by dokka
    project.version = version!!.toString()

    inceptionYear = 2023

    sourceControlManagement(
      Scm.Git(
        owner = "teogor",
        repo = "drifter",
      ),
    )

    addLicense(LicenseType.APACHE_2_0)

    addDeveloper(TeogorDeveloper())
  }

  docsGenerator {
    name = "Drifter"
    identifier = "drifter"
    alertOnDependentModules = true
    dependencyGatheringType = DependencyType.LOCAL
  }
}

val excludedModulesForWinds = listOf(
  ":drifter-plugin",
)
afterWindsPluginConfiguration { winds ->
  if (!excludedModulesForWinds.contains(path)) {
    val mavenPublish: MavenPublish by winds
    if (mavenPublish.canBePublished) {
      mavenPublishing {
        publishToMavenCentral(SonatypeHost.S01)
        signAllPublications()

        @Suppress("UnstableApiUsage")
        pom {
          coordinates(
            groupId = mavenPublish.groupId!!,
            artifactId = mavenPublish.artifactId!!,
            version = mavenPublish.version!!.toString(),
          )
          mavenPublish attachTo this
        }
      }
    }
  }
}

data class TeogorDeveloper(
  override val id: String = "teogor",
  override val name: String = "Teodor Grigor",
  override val email: String = "open-source@teogor.dev",
  override val url: String = "https://teogor.dev",
  override val roles: List<String> = listOf("Code Owner", "Developer", "Designer", "Maintainer"),
  override val timezone: String = "UTC+2",
  override val organization: String = "Teogor",
  override val organizationUrl: String = "https://github.com/teogor",
) : Developer

val ktlintVersion = "0.50.0"

val excludeModules = listOf(
  project.name,
)

subprojects {
  if (!excludeModules.contains(this.name)) {
    apply<com.diffplug.gradle.spotless.SpotlessPlugin>()
    configure<com.diffplug.gradle.spotless.SpotlessExtension> {
      kotlin {
        target("**/*.kt")
        targetExclude("**/build/**/*.kt")
        ktlint(ktlintVersion)
          .userData(
            mapOf(
              "android" to "true",
              "ktlint_code_style" to "android",
              "ij_kotlin_allow_trailing_comma" to "true",
              // These rules were introduced in ktlint 0.46.0 and should not be
              // enabled without further discussion. They are disabled for now.
              // See: https://github.com/pinterest/ktlint/releases/tag/0.46.0
              "disabled_rules" to
                "filename," +
                "annotation,annotation-spacing," +
                "argument-list-wrapping," +
                "double-colon-spacing," +
                "enum-entry-name-case," +
                "multiline-if-else," +
                "no-empty-first-line-in-method-block," +
                "package-name," +
                "trailing-comma," +
                "spacing-around-angle-brackets," +
                "spacing-between-declarations-with-annotations," +
                "spacing-between-declarations-with-comments," +
                "unary-op-spacing," +
                "no-trailing-spaces," +
                "no-wildcard-imports," +
                "max-line-length",
            ),
          )
        licenseHeaderFile(rootProject.file("spotless/copyright.kt"))
        trimTrailingWhitespace()
        endWithNewline()
      }
      format("kts") {
        target("**/*.kts")
        targetExclude("**/build/**/*.kts")
        // Look for the first line that doesn't have a block comment (assumed to be the license)
        licenseHeaderFile(rootProject.file("spotless/copyright.kts"), "(^(?![\\/ ]\\*).*$)")
      }
      format("xml") {
        target("**/*.xml")
        targetExclude("**/build/**/*.xml")
        // Look for the first XML tag that isn't a comment (<!--) or the xml declaration (<?xml)
        licenseHeaderFile(rootProject.file("spotless/copyright.xml"), "(<[^!?])")
      }
    }
  }
}

apiValidation {
  /**
   * Sub-projects that are excluded from API validation
   */
  ignoredProjects.addAll(excludeModules)

  /**
   * Flag to programmatically disable compatibility validator
   */
  validationDisabled = false
}

subprojects {
  if (!excludeModules.contains(this@subprojects.name)) {
    apply<DokkaPlugin>()
  }
}
