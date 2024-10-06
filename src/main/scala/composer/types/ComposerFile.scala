package composer.types

import upickle.default.read
import upickle.default.ReadWriter
import upickle.implicits.key

case class ComposerFile(
    name:     Option[String],
    @key("require")     packages: Map[String, String],
    @key("require-dev") dev:      Option[Map[String, String]] = None
) derives ReadWriter

object ComposerFile:
    def fromJson(jsonContent: String): ComposerFile = read[ComposerFile](jsonContent)

extension (item: ComposerFile)
    def combinedList: List[Package] = item.getPackageList ++ item.getDevList
    private def getPackageList: List[Package] = item.packages.map((pkg, version) => Package(pkg, version)).toList
    private def getDevList: List[Package] = item.dev.getOrElse(Map.empty).map((pkg, version) => Package(pkg, version)).toList