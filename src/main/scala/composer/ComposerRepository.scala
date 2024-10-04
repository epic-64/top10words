package composer

class ComposerRepository:
    private val file1 = """
    |{
    |    "require": {
    |        "php": "^8.0.2",
    |        "guzzlehttp/guzzle": "^7.2",
    |        "laravel/framework": "^9.19",
    |        "laravel/sanctum": "^3.0",
    |        "laravel/tinker": "^2.7"
    |    },
    |    "require-dev": {
    |        "fakerphp/faker": "^1.9.1",
    |        "infection/infection": "^0.26.16",
    |        "laravel/pint": "^1.0",
    |        "laravel/sail": "^1.0.1",
    |        "mockery/mockery": "^1.4.4",
    |        "nunomaduro/collision": "^6.1",
    |        "phpunit/phpunit": "^9.5.10",
    |        "spatie/laravel-ignition": "^1.0"
    |    }
    |}""".stripMargin.replaceAll("\n", "")

    private val file2 = """
    |{
    |    "require": {
    |        "php": "^8.2",
    |        "guzzlehttp/guzzle": "^7.2",
    |        "laravel/framework": "^9.19",
    |        "laravel/sanctum": "^3.0",
    |        "laravel/tinker": "^2.7"
    |    }
    |}""".stripMargin.replaceAll("\n", "")

    def all(): List[ComposerFile] =
        List(
            ComposerFile.fromJson(file1),
            ComposerFile.fromJson(file2),
        )
