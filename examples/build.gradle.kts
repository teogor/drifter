import org.jetbrains.dokka.gradle.DokkaPlugin

plugins {
  alias(libs.plugins.android.application) apply false
  alias(libs.plugins.android.library) apply false
  alias(libs.plugins.jetbrains.kotlin.android) apply false

  alias(libs.plugins.ceres.android.application) apply false
  alias(libs.plugins.ceres.android.application.compose) apply false
  alias(libs.plugins.ceres.android.library) apply false
  alias(libs.plugins.ceres.android.library.compose) apply false

  alias(libs.plugins.spotless) apply true
}

val ktlintVersion = "0.50.0"

subprojects {
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
