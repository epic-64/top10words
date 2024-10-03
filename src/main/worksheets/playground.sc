class Pair[A, B](first: A, second: B):
  def swap: Pair[B, A] = Pair(second, first);
  override def toString: String = s"Pair($first, $second)"

val pair = new Pair(5, "Hello World");
println(pair)

val swapped = pair.swap
println(swapped)

sealed trait ItemState
class Draft extends ItemState
class Approved extends ItemState
class Published extends ItemState

class Item[S <: ItemState](val content: String)

class ItemRepository:
  def modifyContent[S <: ItemState](item: Item[S], newContent: String): Item[S] =
    new Item[S](newContent)

  def approve(item: Item[Draft]): Item[Approved] =
    new Item[Approved](item.content)

  def publish(item: Item[Approved]): Item[Published] =
    new Item[Published](item.content)

val repo = new ItemRepository()

val draftItem = new Item[Draft]("I am a Draft")

val modifiedDraft = repo.modifyContent(draftItem, "I am an updated Draft")

val publishedItem = repo.publish(modifiedDraft)

val modifiedPublished = repo.modifyContent(draftItem, "I am an updated published content")

repo.publish(publishedItem)