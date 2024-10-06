import composer.{ComposerFile, ComposerReportRepository}
import org.scalatest.funsuite.AnyFunSuite

class ComposerTest extends AnyFunSuite {
    test("list is sorted correctly") {
        val files = List(
            ComposerFile.fromJson(
                """
                  |{
                  |  "name": "test1",
                  |  "require": {
                  |    "awesome/tool": "1.0.0"
                  |  }
                  |}""".stripMargin
            ),
            ComposerFile.fromJson(
                """
                  |{
                  |  "name": "test2",
                  |  "require": {
                  |    "awesome/tool": "1.0.0"
                  |  }
                  |}""".stripMargin
            ),
            ComposerFile.fromJson(
                """
                  |{
                  |  "name": "test3",
                  |  "require": {
                  |    "awesome/tool": "1.0.1"
                  |  }
                  |}""".stripMargin
            )
        )

        println(files)

        val report = ComposerReportRepository.getReport(files)
        println(report)

        println(ComposerReportRepository.createReportString(report))
    }
}
