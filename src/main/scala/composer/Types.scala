package composer

import upickle.default.*
import upickle.implicits.key

case class Package(packageName: String, packageVersion: String) derives ReadWriter:
    override def toString: String = s"$packageName:$packageVersion"

case class PackageSummary(packageName: String, usageCount: Int, versions: Map[String, List[String]])

sealed trait PackageListState
class Compiled extends PackageListState
class Sorted extends PackageListState

case class PackageList[S <: PackageListState](items: List[PackageSummary]) {
    def print(): Unit = {
        items.foreach((item: PackageSummary) => {
            println(s"${item.packageName} - ${item.usageCount}")
            item.versions.foreach((version, projectList) => {
                println(s"  $version - ${projectList.size}")
                projectList.foreach((project) => {
                    println(s"    $project")
                })
            })
        })
    }
}

object CompiledPackageList:
    def sort(list: PackageList[Compiled]): PackageList[Sorted] =
        PackageList[Sorted](list.items.sortBy(_.packageName).reverse.sortBy(_.usageCount).reverse)

    def fromFiles(files: List[ComposerFile]): PackageList[Compiled] = {
        val groups = files
            .flatMap(file => file.combinedList.map(pkg => (file.name, pkg)))
            .groupBy((_, pkg) => pkg.packageName)

        groups.foreach(println)

        val summaries = groups.map((packageName, group) => {
            val versions = group.groupBy((_, pkg) => pkg.packageVersion)
            val versionList = versions.map((version, versionGroup) => {
                val projects = versionGroup.map((_, project) => project._1)
                (version, projects)
            })
            PackageSummary(packageName, group.size, versionList)
        }).toList

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
                        name:     String,
    @key("require")     packages: Map[String, String],
    @key("require-dev") dev:      Option[Map[String, String]] = None
) derives ReadWriter

object ComposerFile:
    def fromJson(jsonContent: String): ComposerFile = read[ComposerFile](jsonContent)

extension (item: ComposerFile)
    def combinedList: List[Package] = item.getPackageList ++ item.getDevList
    private def getPackageList: List[Package] = item.packages.map((pkg, version) => Package(pkg, version)).toList
    private def getDevList: List[Package] = item.dev.getOrElse(Map.empty).map((pkg, version) => Package(pkg, version)).toList
