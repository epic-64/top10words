import composer.*
import upickle.default.*
import upickle.implicits.key

import scala.collection.immutable.HashMap

val repo = new ComposerRepository()

val files = repo.all()

val compiledList = CompiledPackageList.fromFiles(files)

def printInfo(numberOfFiles: Int, packageCount: Int, versionCount: Int): Unit = {
    println(s"total number of files: $numberOfFiles")
    println(s"total number of packages (by name): $packageCount")
    println(s"total number of unique package versions: $versionCount")
}

printInfo(files.size, compiledList.items.size, compiledList.items.flatMap(_.versions.keys).size)

val sortedList = CompiledPackageList.sort(compiledList.items)

sortedList.foreach((item: PackageSummary) => {
    println(s"${item.packageName} - ${item.usageCount}")
    item.versions.foreach((version, count) => println(s"  $version - $count"))
})