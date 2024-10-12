package funky.sad

import scala.language.postfixOps

object Sad:
  def main(args: Array[String]): Unit =
    val mood = "sad"
    val sad = "sad"
    val Quran = "Quran"

    object take:
      infix def exercise: Unit = println("Taking exercise")

    object listen:
      infix def to(other: String): Unit = println("Listening to " + other)

    object offer:
      infix def Prayer: Unit = println("Offering prayer")

    extension (mood: String)
      infix def is(other: String): Boolean = mood == other

    if mood is sad then
      take exercise;
      listen to Quran;
      offer Prayer;

    // Output
    // Taking exercise
    // Listening to Quran
    // Offering prayer