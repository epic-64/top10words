package composer

class ComposerRepository:
  private val file1 = """
        |{
        |    "name": "test1",
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
        |    "name": "test2",
        |    "require": {
        |        "php": "^8.2",
        |        "guzzlehttp/guzzle": "^7.2",
        |        "laravel/framework": "^9.19",
        |        "laravel/sanctum": "^3.0",
        |        "laravel/tinker": "^2.7"
        |    }
        |}""".stripMargin.replaceAll("\n", "")

  private val file3 = """
        |{
        |    "name": "openai-php/client",
        |    "description": "OpenAI PHP is a supercharged PHP API client that allows you to interact with the Open AI
        |     API",
        |    "keywords": ["php", "openai", "sdk", "codex", "GPT-3", "DALL-E", "api", "client", "natural", "language",
        |     "processing"],
        |    "license": "MIT",
        |    "authors": [
        |        {
        |            "name": "Nuno Maduro",
        |            "email": "enunomaduro@gmail.com"
        |        },
        |        {
        |            "name": "Sandro Gehri"
        |        }
        |    ],
        |    "require": {
        |        "php": "^8.1.0",
        |        "php-http/discovery": "^1.19.4",
        |        "php-http/multipart-stream-builder": "^1.4.2",
        |        "psr/http-client": "^1.0.3",
        |        "psr/http-client-implementation": "^1.0.1",
        |        "psr/http-factory-implementation": "*",
        |        "psr/http-message": "^1.1.0|^2.0.0"
        |    },
        |    "require-dev": {
        |        "guzzlehttp/guzzle": "^7.9.2",
        |        "guzzlehttp/psr7": "^2.7.0",
        |        "laravel/pint": "^1.17.3",
        |        "mockery/mockery": "^1.6.12",
        |        "nunomaduro/collision": "^7.10.0",
        |        "pestphp/pest": "^2.35.1",
        |        "pestphp/pest-plugin-arch": "^2.7",
        |        "pestphp/pest-plugin-type-coverage": "^2.8.6",
        |        "phpstan/phpstan": "^1.12.4",
        |        "rector/rector": "^1.2.5",
        |        "symfony/var-dumper": "^6.4.11"
        |    },
        |    "autoload": {
        |        "psr-4": {
        |            "OpenAI\\": "src/"
        |        },
        |        "files": [
        |            "src/OpenAI.php"
        |        ]
        |    },
        |    "autoload-dev": {
        |        "psr-4": {
        |            "Tests\\": "tests/"
        |        }
        |    },
        |    "minimum-stability": "dev",
        |    "prefer-stable": true,
        |    "config": {
        |        "sort-packages": true,
        |        "preferred-install": "dist",
        |        "allow-plugins": {
        |            "pestphp/pest-plugin": true,
        |            "php-http/discovery": false
        |        }
        |    },
        |    "scripts": {
        |        "lint": "pint -v",
        |        "refactor": "rector --debug",
        |        "test:lint": "pint --test -v",
        |        "test:refactor": "rector --dry-run",
        |        "test:types": "phpstan analyse --ansi",
        |        "test:type-coverage": "pest --type-coverage --min=100",
        |        "test:unit": "pest --colors=always",
        |        "test": [
        |            "@test:lint",
        |            "@test:refactor",
        |            "@test:types",
        |            "@test:type-coverage",
        |            "@test:unit"
        |        ]
        |    }
        |}""".stripMargin.replaceAll("\n", "")

  def all(): List[ComposerFile] =
    List(
      ComposerFile.fromJson(file1),
      ComposerFile.fromJson(file2),
      ComposerFile.fromJson(file3)
    )
