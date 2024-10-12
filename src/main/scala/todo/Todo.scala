package todo

object Todo {
  def main(args: Array[String]): Unit = {
    val ⏳ = "⏳"
    val ✅ = "✅"
    val ❌ = "❌"

    type 📚📅 = List[📅]
    type 🧵 = String
    type ⬛ = Unit

    def 📢(a: Any): ⬛ = println(a)
    def 📢📅(tasks: 📚📅): ⬛ =
      if tasks.isEmpty then 📢("No tasks")
      else tasks.foreach(t => 📢(t.short))
      📢("")

    // Define a Task case class with status
    case class 📅(description: 🧵, var status: 🧵 = "📅"):
      override def toString: 🧵 = s"Task(description: $description, status: $status)"
      def short: 🧵 = s"$status $description"

    // Task list to store tasks
    var tasks: 📚📅 = List()

    // Define emoji-based infix function for task state transitions
    extension (description: 🧵) {
      private infix def 💾(status: 🧵): ⬛ = {
        tasks = tasks.map {
          case task if task.description == description => task.copy(status = status)
          case task                                    => task
        }
        📢(s"💾 Updated task: $description with status: $status")
      }

      private infix def ✨(status: 🧵): ⬛ = {
        tasks = 📅(description, status) :: tasks
        📢(s"✨ Created task: $description with status: $status")
      }
    }

    📢("DEBUG 1: All tasks")
    📢📅(tasks)

    "Fix the car" ✨ ⏳
    "Plan vacation" ✨ ⏳
    "Clean the house" ✨ ⏳
    "Buy groceries" ✨ ⏳

    📢("\nDEBUG 2: All tasks")
    📢📅(tasks)

    "Fix the car" 💾 ✅
    "Plan vacation" 💾 ❌

    📢("\nDEBUG 3: All tasks")
    📢📅(tasks)

    📢("DEBUG 4: Open tasks")
    📢📅(tasks.filter(_.status == ⏳))

    📢("DEBUG 5:")
    tasks.foreach(📢)
  }
}