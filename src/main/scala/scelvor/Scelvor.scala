package scelvor

import java.util.concurrent.{Executors, ScheduledExecutorService, TimeUnit}

// Simple data structure for a Skill
case class Skill(name: String, var level: Int = 1, var experience: Double = 0.0)

// Resource case class
case class Resource(name: String)

// Player with skills and methods for gaining experience
case class Player(name: String, skills: Map[String, Skill]) {
  // Gain XP in a skill
  def gainExperience(skillName: String, xp: Double): Unit = {
    val skill = skills(skillName)
    skill.experience += xp
    levelUp(skill)
  }

  // Level up if experience exceeds the threshold
  def levelUp(skill: Skill): Unit = {
    val xpThreshold = 100 * skill.level
    if (skill.experience >= xpThreshold) {
      skill.level += 1
      skill.experience = 0 // Reset experience after level up
      println(s"${skill.name} leveled up to ${skill.level}!")
    }
  }
}

// Improved game loop with scheduled gathering
object Game {
  val gatherXpPerAction = 10.0

  // Start gathering resources and leveling up the skill
  def gatherResource(player: Player, resource: Resource, skillName: String): Unit = {
    println(s"Gathering ${resource.name}...")
    player.gainExperience(skillName, gatherXpPerAction)
  }

  // Game loop that runs every second using a scheduled executor
  def gameLoop(player: Player, resource: Resource, skillName: String): Unit = {
    val scheduler: ScheduledExecutorService = Executors.newScheduledThreadPool(1)

    // Task to run every second
    val gatherTask = new Runnable {
      override def run(): Unit = gatherResource(player, resource, skillName)
    }

    // Schedule the task to run at a fixed rate
    scheduler.scheduleAtFixedRate(gatherTask, 0, 1, TimeUnit.SECONDS)
  }
}

// Main entry point
@main def startGame(): Unit = {
  // Create initial player with a gathering skill
  val player   = Player("Hero", Map("Gathering" -> Skill("Gathering")))
  val resource = Resource("Wood")

  // Start the game loop with scheduled gathering
  Game.gameLoop(player, resource, "Gathering")
}
