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
  var player = Player("Hero", Map("Gathering" -> Skill("Gathering")))

  val gatherXpPerAction = 10.0

  // Labels and ProgressBars for the UI
  var xpLabel: Label = _
  var progressBar: ProgressBar = _
  var actionProgressBar: ProgressBar = _

  // Time it takes to complete one action in milliseconds (e.g., 2 seconds)
  val actionDuration: Long = 2000

  override def start(): Unit = {
    // Initialize UI components inside the start method
    xpLabel = new Label("Gathering (Level 1): 0/100 XP")
    progressBar = new ProgressBar {
      progress = 0.0
      prefWidth = 300 // Increase the width of the progress bar
    }
    actionProgressBar = new ProgressBar {
      progress = 0.0
      prefWidth = 300 // Increase the width of the action progress bar
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
            actionProgressBar, // Add the action progress bar
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
          updateActionProgress() // Update action progress bar
          if (actionProgressBar.progress() >= 1.0) {
            player = gatherResource(player, "Wood", "Gathering")
            updateUI()
            resetActionProgress()
          }
        }
      }
    }, 0, 16, TimeUnit.MILLISECONDS) // Run the task every ~16ms for 60 FPS smooth action progress
  }

  // Function to gather resources and update player state
  def gatherResource(player: Player, resource: String, skillName: String): Player = {
    player.gainExperience(skillName, gatherXpPerAction)
  }

  // Update the XP UI components
  def updateUI(): Unit = {
    player.getSkill("Gathering").foreach { skill =>
      xpLabel.text = s"Gathering (Level ${skill.level}): ${skill.experience.toInt}/${skill.xpToNextLevel.toInt} XP"
      progressBar.progress = skill.experienceProgress
    }
  }

  // Reset the action progress bar
  def resetActionProgress(): Unit = {
    actionProgressBar.progress = 0.0
  }

  // Update the action progress bar (fills over the duration of the action)
  def updateActionProgress(): Unit = {
    val currentProgress = actionProgressBar.progress()
    val progressStep = 1.0 / (actionDuration / 16.0) // Correct progress step calculation for smoother 60 FPS updates
    actionProgressBar.progress = Math.min(currentProgress + progressStep, 1.0)
  }
}