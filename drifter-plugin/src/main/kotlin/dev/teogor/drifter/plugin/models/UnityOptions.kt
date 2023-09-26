package dev.teogor.drifter.plugin.models

data class UnityOptions(
  var splashMode: Int = 0,
  var splashEnable: Boolean = false,
  var buildId: String = "",
  var version: String = "",
  var notchConfig: String = "",
  var ndkVersion: String = "",
  var ndkPath: String = "",
  var platforms: List<PlatformArch> = emptyList(),
  var configuration: Configuration = Configuration.Debug,
  var streamingAssets: List<String> = emptyList(),
  var exportFolder: String = "",
  var libraryName: String = "unityLibrary",
)
