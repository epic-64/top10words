package composer

import upickle.default.*
import upickle.implicits.key

case class Package(packageName: String, packageVersion: String)derives ReadWriter {
    override def toString: String = s"$packageName:$packageVersion"
}

case class ComposerFile(
    @key("require") packages: Map[String, String],
    @key("require-dev") dev: Option[Map[String, String]] = None
)derives ReadWriter {
    def packagesAsList: List[Package] =
        packages
            .map { case (pkg, version) => Package(pkg, version) }
            .toList

    def devAsList: List[Package] =
        dev
            .getOrElse(Map.empty)
            .map { case (pkg, version) => Package(pkg, version) }
            .toList
}

object ComposerFile:
    def fromJson(jsonContent: String): ComposerFile =
        read[ComposerFile](jsonContent)
