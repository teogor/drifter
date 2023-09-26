
import dev.teogor.publish.model.LicenseType
import dev.teogor.publish.model.PublishPluginExtension
import dev.teogor.publish.model.create

fun PublishPluginExtension.defaultLibraryInfo(
  artifactId: String,
  version: String,
) {

  name = "Drifter"
  description = "Drifter simplifies the integration between Unity and Android, enhancing performance seamlessly and effortlessly."
  inceptionYear = "2023"
  url = "https://github.com/teogor/drifter/"

  setModuleCoordinates(
    groupId = "dev.teogor.drifter",
    artifactId = artifactId,
    version = version,
  )

  licenseType = LicenseType.APACHE_2_0

  developers {
    create {
      id = "teogor"
      name = "Teodor Grigor"
      url = "https://teogor.dev"
      roles = listOf("Code Owner", "Developer", "Designer", "Maintainer")
      timezone = "UTC+2"
      organization = "Teogor"
      organizationUrl = "https://github.com/teogor"
    }
  }

  scmUrl = "https://github.com/teogor/drifter/"
  scmConnection = "scm:git:git://github.com/teogor/drifter.git"
  scmDeveloperConnection = "scm:git:ssh://git@github.com/teogor/drifter.git"

}
