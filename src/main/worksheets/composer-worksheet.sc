import composer.*
import upickle.default.*
import upickle.implicits.key

import scala.collection.immutable.HashMap

val repo = new ComposerRepository()

val files = repo.all()

val compiledList = CompiledPackageList.fromFiles(files)

val sortedList = CompiledPackageList.sort(compiledList.items)

CompiledPackageList.printInfo(files.size, compiledList.items.size, compiledList.items.flatMap(_.versions.keys).size)

sortedList.foreach((item: PackageSummary) => {
    println(s"${item.packageName} - ${item.usageCount}")
    item.versions.foreach((version, count) => println(s"  $version - $count"))
})