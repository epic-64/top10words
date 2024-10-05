import composer.*
import upickle.default.*
import upickle.implicits.key

import scala.collection.immutable.HashMap

val repo = new ComposerRepository()

val files: List[ComposerFile] = repo.all()

val compiledList = CompiledPackageList.fromFiles(files)
val sortedList = CompiledPackageList.sort(compiledList)

CompiledPackageList.printInfo(files.size, compiledList.items.size, compiledList.items.flatMap(_.versions.keys).size)
sortedList.print()