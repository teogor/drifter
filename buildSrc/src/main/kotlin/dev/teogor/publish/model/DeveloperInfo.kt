package dev.teogor.publish.model

data class DeveloperInfo(
  var id: String = "teogor",
  var name: String = "Teodor Grigor",
  var url: String = "https://teogor.dev",
  var roles: List<String> = listOf("Code Owner", "Developer", "Designer", "Maintainer"),
  var timezone: String = "UTC+2",
  var organization: String = "Teogor",
  var organizationUrl: String = "https://github.com/teogor",
)
