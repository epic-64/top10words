import composer.{ComposerReportRepository, ComposerRepository}
import upickle.default.*
import upickle.implicits.key

import scala.collection.immutable.HashMap

val repo = new ComposerRepository()

val files = repo.all()

val report = ComposerReportRepository.getReport(files)
val sorted = report
  .sortBy(_.packageName).reverse
  .sortBy(_.occurrences.size).reverse
val reportString = ComposerReportRepository.createReportString(sorted)

println(reportString)