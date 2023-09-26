package dev.teogor.publish.model

open class PublishPluginExtension {
  lateinit var groupId: String
    private set
  lateinit var artifactId: String
    private set
  lateinit var version: String
    private set

  var name: String = ""
  var description: String = ""
  var inceptionYear: String = ""
  var url: String = ""

  var scmUrl: String = ""
  var scmConnection: String = ""
  var scmDeveloperConnection: String = ""

  lateinit var licenseType: LicenseType

  var isBomModule: Boolean = false
    private set

  var isPlugin: Boolean = false
    private set

  var bomOptions: BomOptions = BomOptions()
    private set

  var developers: MutableList<DeveloperInfo> = mutableListOf()
    private set

  fun configureBomModule(
    bomOptions: BomOptions.() -> Unit,
  ) = apply {
    isBomModule = true
    this.bomOptions.apply(bomOptions)
  }

  fun configurePlugin(
  ) = apply {
    isPlugin = true
  }

  fun developers(
    developerInfo: MutableList<DeveloperInfo>.() -> Unit,
  ) = apply {
    this.developers.apply(developerInfo)
  }

  fun setModuleCoordinates(
    groupId: String,
    artifactId: String,
    version: String,
  ) = apply {
    this.groupId = groupId
    this.artifactId = artifactId
    this.version = version
  }
}

fun MutableList<DeveloperInfo>.create(
  developerInfo: DeveloperInfo.() -> Unit,
) = apply {
  add(DeveloperInfo().apply(developerInfo))
}
