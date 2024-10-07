package scelvor

import scalafx.Includes._
import scalafx.application.JFXApp3
import scalafx.application.Platform
import scalafx.scene.Scene
import scalafx.scene.control.{Button, Label, ProgressBar}
import scalafx.geometry.Insets
import scalafx.scene.layout.VBox

import java.util.concurrent.{Executors, ScheduledExecutorService, TimeUnit}

// Immutable data structure for a Skill
case class Skill(name: String, level: Int = 1, experience: Double = 0.0) {
  def gainExperience(xp: Double): Skill = {
    val updatedExperience = experience + xp
    if (updatedExperience >= xpToNextLevel) {
      copy(level = level + 1, experience = 0.0) // Reset experience after leveling up
    } else {
      copy(experience = updatedExperience)
    }
  }

  def xpToNextLevel: Double = 100 * level

  def experienceProgress: Double = experience / xpToNextLevel
}

// Immutable Player with methods to gain experience in skills
case class Player(name: String, skills: Map[String, Skill]) {
  def gainExperience(skillName: String, xp: Double): Player = {
    skills.get(skillName) match {
      case Some(skill) =>
        val updatedSkill = skill.gainExperience(xp)
        copy(skills = skills.updated(skillName, updatedSkill)) // Return updated player
      case None => this // If skill not found, return unchanged player
    }
  }

  def getSkill(skillName: String): Option[Skill] = skills.get(skillName)
}

object GameApp extends JFXApp3 {
  private var player = Player("Hero", Map("Gathering" -> Skill("Gathering")))

  val gatherXpPerAction = 10.0

  // Labels and ProgressBar for the UI, initialized inside start() to avoid toolkit error
  var xpLabel: Label = _
  var progressBar: ProgressBar = _

  override def start(): Unit = {
    // Now we initialize UI components inside the start method
    xpLabel = new Label("Gathering (Level 1): 0/100 XP")
    progressBar = new ProgressBar {
      progress = 0.0
      prefWidth = 300
    }

    stage = new JFXApp3.PrimaryStage {
      title = "ScalaFX Melvor Idle Clone"
      scene = new Scene {
        root = new VBox {
          padding = Insets(20)
          spacing = 10
          children = Seq(
            xpLabel,
            progressBar,
            new Button("Gather Resource") {
              onAction = _ => {
                player = gatherResource(player, "Wood", "Gathering")
                updateUI()
              }
            }
          )
        }
      }
    }

    // Start background task to simulate continuous gathering
    val scheduler: ScheduledExecutorService = Executors.newScheduledThreadPool(1)
    scheduler.scheduleAtFixedRate(new Runnable {
      override def run(): Unit = {
        Platform.runLater {
          player = gatherResource(player, "Wood", "Gathering")
          updateUI()
        }
      }
    }, 0, 1, TimeUnit.SECONDS)
  }

  // Function to gather resources and update player state
  def gatherResource(player: Player, resource: String, skillName: String): Player = {
    player.gainExperience(skillName, gatherXpPerAction)
  }

  // Update the UI components
  def updateUI(): Unit = {
    player.getSkill("Gathering").foreach { skill =>
      xpLabel.text = s"Gathering (Level ${skill.level}): ${skill.experience.toInt}/${skill.xpToNextLevel.toInt} XP"
      progressBar.progress = skill.experienceProgress
    }
  }
}