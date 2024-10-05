package composer

import upickle.default.*
import upickle.implicits.key

case class Package(packageName: String, packageVersion: String)derives ReadWriter {
    override def toString: String = s"$packageName:$packageVersion"
}

case class PackageSummary(packageName: String, usageCount: Int, versions: Map[String, List[String]])
case class CompiledPackageList(items: List[PackageSummary])

object CompiledPackageList:
    def sort(items: List[PackageSummary]): List[PackageSummary] =
        items
            .sortBy(_.packageName).reverse
            .sortBy(_.usageCount).reverse

    def fromFiles(files: List[ComposerFile]): CompiledPackageList = {
        val compiledList = files
            .flatMap(file => file.combinedList.map(pkg => (file.name, pkg)))
            .groupBy((_, pkg) => pkg.packageName)
            .map { (packageName, packageList) =>
                val versions = packageList
                    .groupBy { (_, pkg) => pkg.packageVersion }
                    .map { (version, packages) =>
                        // Collect all filenames where this version appears
                        (version, packages.map { case (fileName, _) => fileName })
                    }

                // Create the PackageSummary using the package name, the count of packages, and versions with filenames
                PackageSummary(packageName, packageList.size, versions)
            }

        CompiledPackageList(compiledList.toList)
    }

    def printInfo(numberOfFiles: Int, packageCount: Int, versionCount: Int): Unit = {
        println(s"total number of files: $numberOfFiles")
        println(s"total number of packages (by name): $packageCount")
        println(s"total number of unique package versions: $versionCount")
    }

    def print(items: List[PackageSummary]): Unit = {
        items.foreach((item: PackageSummary) => {
            println(s"${item.packageName} - ${item.usageCount}")
            item.versions.foreach((version, projectList) => {
                println(s"  $version")
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
