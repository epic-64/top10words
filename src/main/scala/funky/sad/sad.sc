import scala.language.postfixOps

val mood = "sad"
val sad = "sad"
val Quran = "Quran"

object take:
  infix def exercise = println("Taking exercise")

object listen:
  infix def to(other: String) = println(s"Listening to $other")

object Offer:
  infix def Prayer = println("Offering prayer")

extension (mood: String)
  infix def is(other: String): Boolean = mood == other

if mood is sad then
  take exercise;
  listen to Quran;
  Offer Prayer;




