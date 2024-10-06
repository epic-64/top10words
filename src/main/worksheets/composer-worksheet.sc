import composer.{ComposerReportRepository, ComposerRepository}
import upickle.default.*
import upickle.implicits.key

import scala.collection.immutable.HashMap

val repo = new ComposerRepository()

val files = repo.all()

val report = ComposerReportRepository.getReport(files)
val reportString = ComposerReportRepository.createReportString(report)

println(reportString)