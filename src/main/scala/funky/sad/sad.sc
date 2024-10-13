import scala.language.postfixOps

val mood = "sad"
val sad = "sad"
val Quran = "Quran"

object take:
  infix def exercise = println("Taking exercise")

object listen:
  infix def to(other: String) = println(s"Listening to $other")

object offer:
  infix def prayer = println("Offering prayer")

object chainable;

object a:
  infix def friend = println("a friend")
  infix def walk = println("a walk")

object talk:
  infix def to(other: a.type) =
    print("Talking to ")
    other

object go:
  infix def fer(other: a.type) =
    print("Going for ")
    other

extension (mood: String)
  infix def is(other: String): Boolean = mood == other

if mood is sad then
  take exercise;
  listen to Quran;
  offer prayer;
  talk to a friend;
  go fer a walk;
