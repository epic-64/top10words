package composer

import upickle.default.*
import upickle.implicits.key

case class Package(packageName: String, packageVersion: String)derives ReadWriter {
    override def toString: String = s"$packageName:$packageVersion"
}

case class PackageSummary(packageName: String, usageCount: Int, versions: Map[String, List[String]])

sealed trait PackageListState
class Unsorted extends PackageListState
class Compiled extends PackageListState
class Sorted extends PackageListState

case class PackageList[s <: PackageListState](items: List[PackageSummary])

object CompiledPackageList:
    def sort(list: PackageList[Compiled]): PackageList[Sorted] =
        PackageList[Sorted](list.items
            .sortBy(_.packageName).reverse
            .sortBy(_.usageCount).reverse
        )

    private def getSummaries(packageName: String, packageList: List[Package]): PackageSummary = {
        val versions = packageList
            .groupBy(_.packageVersion)
            .map { (version, packages) =>
                (version, packages.map(_.toString))
            }

        PackageSummary(packageName, packageList.size, versions)
    }

    def fromFiles(files: List[ComposerFile]): PackageList[Compiled] = {
        val summaries = files
            .flatMap(file => file.combinedList.map(pkg => (file.name, pkg)))
            .groupBy((_, pkg) => pkg.packageName)
            .map((packageName, packageList) => getSummaries(packageName, packageList.map(_._2)))
            .toList

        PackageList[Compiled](summaries)
    }

    def printInfo(numberOfFiles: Int, packageCount: Int, versionCount: Int): Unit = {
        println(s"total number of files: $numberOfFiles")
        println(s"total number of packages (by name): $packageCount")
        println(s"total number of unique package versions: $versionCount")
    }

    def print(list: PackageList[?]): Unit = {
        list.items.foreach((item: PackageSummary) => {
            println(s"${item.packageName} - ${item.usageCount}")
            item.versions.foreach((version, projectList) => {
                println(s"  $version - ${projectList.size}")
                projectList.foreach((project) => {
                    println(s"    $project")
                })
            })
        })
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
