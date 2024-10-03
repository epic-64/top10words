import composer.{ComposerFile, ComposerRepository, Package}
import upickle.default.*
import upickle.implicits.key

val repo = new ComposerRepository()

val files = repo.all()