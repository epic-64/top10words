import composer.{ComposerFile, ComposerRepository, Package}
import upickle.default.*
import upickle.implicits.key

import scala.collection.immutable.HashMap

val repo = new ComposerRepository()

val files = repo.all()

// create a list that sorts takes all packages
// from all files (combinedList), counts their usage,
// sorts by usage, and lists the packages with their usage numbers.
// also, make sure to group each package by version as well, and count version usage
val compiledList = files
  .flatMap(_.combinedList)
  .groupBy(_.packageName)
  .map((packageName, packageList) => {
    val versions = packageList.groupBy(_.packageVersion)
    val versionCounts = versions.map((version, packages) => (version, packages.size))

    // create a hashmap so the return reads like a json object
    HashMap(
      "packageName" -> packageName,
      "usage" -> packageList.size,
      "versions" -> versionCounts
    )
  }).toList

println(s"total number of files: ${files.size}")
println(s"total number of packages (by name): ${compiledList.size}")
println(s"total number of unique package versions: ${compiledList.flatMap(_("versions").asInstanceOf[HashMap[String, Int]].keys).size}")


// sort the list by usage
compiledList
    .sortBy(_("packageName").asInstanceOf[String])
    .reverse
    .sortBy(_("usage").asInstanceOf[Int])
    .reverse
    .foreach(item => {
        println(s"${item("packageName")} - ${item("usage")}")
        item("versions").asInstanceOf[HashMap[String, Int]]
            .foreach((version, count) => println(s"  $version - $count"))
    })