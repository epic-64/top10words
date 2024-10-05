package composer

import upickle.default.*
import upickle.implicits.key

case class Package(packageName: String, packageVersion: String)derives ReadWriter {
    override def toString: String = s"$packageName:$packageVersion"
}

case class PackageSummary(packageName: String, usageCount: Int, versions: Map[String, Int])
case class CompiledPackageList(items: List[PackageSummary])

object CompiledPackageList:
    def sort(items: List[PackageSummary]): List[PackageSummary] =
        items
            .sortBy(_.packageName).reverse
            .sortBy(_.usageCount).reverse

    def fromFiles(files: List[ComposerFile]): CompiledPackageList = {
        val compiledList = files
            .flatMap(_.combinedList)
            .groupBy(_.packageName)
            .map((packageName, packageList) => {
                val versionCounts = packageList
                    .groupBy(_.packageVersion)
                    .map((version, packages) => (version, packages.size))

                PackageSummary(packageName, packageList.size, versionCounts)
            })

        CompiledPackageList(compiledList.toList)
    }

    def printInfo(numberOfFiles: Int, packageCount: Int, versionCount: Int): Unit = {
        println(s"total number of files: $numberOfFiles")
        println(s"total number of packages (by name): $packageCount")
        println(s"total number of unique package versions: $versionCount")
    }

case class ComposerFile(
    name: String,
    @key("require") packages: Map[String, String],
    @key("require-dev") dev: Option[Map[String, String]] = None
)derives ReadWriter {
    def combinedList: List[Package] =
        packagesAsList ++ devAsList

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
