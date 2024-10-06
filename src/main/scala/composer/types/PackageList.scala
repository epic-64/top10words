package composer.types

import upickle.default.ReadWriter

case class Package(packageName: String, packageVersion: String) derives ReadWriter:
    override def toString: String = s"$packageName:$packageVersion"

case class PackageSummary(packageName: String, usageCount: Int, versions: Map[String, List[String]])

sealed trait PackageListState
class Compiled extends PackageListState
class Sorted extends PackageListState

case class PackageList[S <: PackageListState](items: List[PackageSummary])

extension (list: PackageList[?])
    def getPrintableString: String = list.items.map((item: PackageSummary) => {
        val versions = item.versions.map((version, projectList) => {
            val projects = projectList.map((project) => s"    $project").mkString("\n")
            s"  $version - ${projectList.size}\n$projects"
        }).mkString("\n")
        s"${item.packageName} - ${item.usageCount}\n$versions"
    }).mkString("\n")

object CompiledPackageList:
    def sort(list: PackageList[Compiled]): PackageList[Sorted] =
        PackageList[Sorted](list.items.sortBy(_.packageName).reverse.sortBy(_.usageCount).reverse)

    def fromFiles(files: List[ComposerFile]): PackageList[Compiled] = {
        val groups = files
            .flatMap(file => file.combinedList.map(pkg => (file.name, pkg)))
            .groupBy((_, pkg) => pkg.packageName)

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
