import com.vanniktech.maven.publish.SonatypeHost
import dev.teogor.winds.api.ArtifactIdFormat
import dev.teogor.winds.api.License
import dev.teogor.winds.api.NameFormat
import dev.teogor.winds.api.Person
import dev.teogor.winds.api.Scm
import dev.teogor.winds.api.TicketSystem
import dev.teogor.winds.ktx.createVersion
import org.jetbrains.dokka.gradle.DokkaPlugin
import dev.teogor.winds.ktx.scm
import dev.teogor.winds.ktx.ticketSystem
import dev.teogor.winds.ktx.person

plugins {
  alias(libs.plugins.android.application) apply false
  alias(libs.plugins.android.library) apply false
  alias(libs.plugins.jetbrains.kotlin.android) apply false
  alias(libs.plugins.kotlin.jvm) apply true

  alias(libs.plugins.ceres.android.application) apply false
  alias(libs.plugins.ceres.android.application.compose) apply false
  alias(libs.plugins.ceres.android.library) apply false
  alias(libs.plugins.ceres.android.library.compose) apply false

  alias(libs.plugins.teogor.winds) apply true

  alias(libs.plugins.vanniktech.maven) apply true
  alias(libs.plugins.dokka) apply true
  alias(libs.plugins.spotless) apply true
  alias(libs.plugins.api.validator) apply true
}

java {
  toolchain {
    languageVersion.set(JavaLanguageVersion.of(11))
  }
}
tasks.withType<JavaCompile>().configureEach {
  sourceCompatibility = JavaVersion.VERSION_11.toString()
  targetCompatibility = JavaVersion.VERSION_11.toString()
}

winds {
  windsFeatures {
    mavenPublishing = true
  }

  moduleMetadata {
    name = "Drifter"
    description = "\uD83C\uDFAE Drifter simplifies the integration between Unity and Android, enhancing performance seamlessly and effortlessly."
    yearCreated = 2023
    websiteUrl = "https://source.teogor.dev/drifter/"
    apiDocsUrl = "https://source.teogor.dev/drifter/html/"

    artifactDescriptor {
      group = "dev.teogor.drifter"
      name = "Drifter"
      version = createVersion(1, 0, 0) {
        alphaRelease(1)
      }
      nameFormat = NameFormat.FULL
      artifactIdFormat = ArtifactIdFormat.MODULE_NAME_ONLY
    }

    // Providing SCM (Source Control Management)
    scm<Scm.GitHub> {
      owner = "teogor"
      repository = "drifter"
    }

    // Providing Ticket System
    ticketSystem<TicketSystem.GitHub> {
      owner = "teogor"
      repository = "drifter"
    }

    // Providing Licenses
    licensedUnder(License.Apache2())

    // Providing Persons
    person<Person.DeveloperContributor> {
      id = "teogor"
      name = "Teodor Grigor"
      email = "open-source@teogor.dev"
      url = "https://teogor.dev"
      roles = listOf("Code Owner", "Developer", "Designer", "Maintainer")
      timezone = "UTC+2"
      organization = "Teogor"
      organizationUrl = "https://github.com/teogor"
    }
  }

  publishingOptions {
    publish = false
    enablePublicationSigning = true
    optInForVanniktechPlugin = true
    cascadePublish = true
    sonatypeHost = SonatypeHost.S01
  }

  documentationBuilder {
    htmlPath = "html/"
  }
}

val excludedModulesForWinds = listOf(
  ":drifter-plugin",
  ":app",
  ":module-unity",
)

val ktlintVersion = "0.50.0"

val excludeModules = listOf(
  project.name,
  "app",
  "module-unity",
)

subprojects {
  if (!excludeModules.contains(this.name)) {
    apply<com.diffplug.gradle.spotless.SpotlessPlugin>()
    configure<com.diffplug.gradle.spotless.SpotlessExtension> {
      kotlin {
        target("**/*.kt")
        targetExclude("**/build/**/*.kt")
        ktlint(ktlintVersion)
          .editorConfigOverride(
            mapOf(
              "android" to "true",
              "ktlint_code_style" to "intellij_idea",
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

// tasks.dokkaHtmlMultiModule {
//   dependsOn(":unity:dokkaHtmlMultiModule")
// }
