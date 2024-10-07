package scelvor

import java.util.concurrent.{Executors, ScheduledExecutorService, TimeUnit}

// Immutable data structure for a Skill
case class Skill(name: String, level: Int = 1, experience: Double = 0.0) {
  // Method to gain experience and potentially level up
  def gainExperience(xp: Double): Skill = {
    val updatedExperience = experience + xp
    if (updatedExperience >= xpToNextLevel) {
      copy(level = level + 1, experience = 0.0) // Reset experience after leveling up
    } else {
      copy(experience = updatedExperience)
    }
  }

  // Threshold for leveling up
  def xpToNextLevel: Double = 100 * level
}

// Immutable Resource case class
case class Resource(name: String)

// Immutable Player with methods to gain experience in skills
case class Player(name: String, skills: Map[String, Skill]) {
  // Gain XP in a skill and return a new Player instance with updated skill
  def gainExperience(skillName: String, xp: Double): Player = {
    skills.get(skillName) match {
      case Some(skill) =>
        val updatedSkill = skill.gainExperience(xp)
        copy(skills = skills.updated(skillName, updatedSkill)) // Return updated player
      case None =>
        this // If skill not found, return unchanged player
    }
  }
}

// Game logic
object Game {
  val gatherXpPerAction = 10.0

  // Start gathering resources and return an updated player
  def gatherResource(player: Player, resource: Resource, skillName: String): Player = {
    println(s"Gathering ${resource.name}...")
    val updatedPlayer = player.gainExperience(skillName, gatherXpPerAction)
    updatedPlayer
  }

  // Game loop that runs every second using a scheduled executor
  def gameLoop(player: Player, resource: Resource, skillName: String): Unit = {
    val scheduler: ScheduledExecutorService = Executors.newScheduledThreadPool(1)

    var currentPlayer = player // Keep track of the current immutable player state

    // Task to run every second
    val gatherTask = new Runnable {
      override def run(): Unit = {
        currentPlayer = gatherResource(currentPlayer, resource, skillName)
      }
    }

    // Schedule the task to run at a fixed rate
    scheduler.scheduleAtFixedRate(gatherTask, 0, 1, TimeUnit.SECONDS)
  }
}

// Main entry point
@main def startGame(): Unit = {
  // Create initial immutable player with a gathering skill
  val player   = Player("Hero", Map("Gathering" -> Skill("Gathering")))
  val resource = Resource("Wood")

  // Start the game loop with scheduled gathering
  Game.gameLoop(player, resource, "Gathering")
}
