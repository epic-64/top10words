import composer.types.*
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

        val compiledList = CompiledPackageList.fromFiles(files)
        val sortedList   = CompiledPackageList.sort(compiledList)

        assert(sortedList.items.size == 1)
        println(sortedList)
        println(sortedList.getPrintableString)
    }
}
