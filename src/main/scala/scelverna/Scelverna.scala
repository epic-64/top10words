package scelverna

import com.googlecode.lanterna.TerminalSize
import com.googlecode.lanterna.TextColor
import com.googlecode.lanterna.screen.{Screen, TerminalScreen}
import com.googlecode.lanterna.terminal.{DefaultTerminalFactory, Terminal}
import com.googlecode.lanterna.terminal.swing.SwingTerminalFontConfiguration
import com.googlecode.lanterna.input.KeyStroke
import com.googlecode.lanterna.input.KeyType
import com.googlecode.lanterna.graphics.TextGraphics

import java.awt.Font
import scala.concurrent.duration._
import java.util.concurrent.Executors
import scala.concurrent.{ExecutionContext, Future}

object Game {
  def main(args: Array[String]): Unit = {
    val game = new Scelverna()
    game.run()
  }
}

trait Skill {
  val name: String
  var xp: Int
  var level: Int
  def xpForNextLevel: Int = level * 100 // Example XP progression per level
  def progressToNextLevel: Double = xp.toDouble / xpForNextLevel
}

case class Woodcutting() extends Skill {
  val name: String = "Woodcutting"
  var xp: Int = 0
  var level: Int = 1
}

case class Mining() extends Skill {
  val name: String = "Mining"
  var xp: Int = 0
  var level: Int = 1
}

case class Woodworking() extends Skill {
  val name: String = "Woodworking"
  var xp: Int = 0
  var level: Int = 1
}

case class Stonecutting() extends Skill {
  val name: String = "Stonecutting"
  var xp: Int = 0
  var level: Int = 1
}

class Scelverna {
  // Skills lists
  private var gatheringSkills: List[Skill] = List(Woodcutting(), Mining())
  private var manufacturingSkills: List[Skill] = List(Woodworking(), Stonecutting())

  // State to track the currently selected skill
  private var selectedSkill: Option[Skill] = Some(gatheringSkills.head)

  private var actionProgress: Double = 0.0 // Progress bar for the action (0.0 to 1.0)
  private val actionDurationSeconds = 5.0

  val terminalFactory = new DefaultTerminalFactory()
  val fontConfig      = SwingTerminalFontConfiguration.newInstance(new Font("Monospaced", Font.PLAIN, 24))
  terminalFactory.setInitialTerminalSize(new TerminalSize(80, 24))
  terminalFactory.setTerminalEmulatorFontConfiguration(fontConfig)

  val terminal: Terminal = terminalFactory.createTerminal()
  val screen  : Screen   = new TerminalScreen(terminal)
  screen.startScreen()
  screen.clear()

  def run(): Unit = {
    val graphics: TextGraphics = screen.newTextGraphics()

    // Create an ExecutionContext for the game loop
    implicit val ec: ExecutionContext = ExecutionContext.fromExecutor(Executors.newFixedThreadPool(2))

    // Start the game loop at 60 FPS
    Future {
      val frameDuration = (1000 / 60).millis
      while (true) {
        update()
        render(graphics)
        screen.refresh()
        Thread.sleep(frameDuration.toMillis)
      }
    }

    // Handle input for skill selection
    Future {
      while (true) {
        val keyStroke: KeyStroke = screen.readInput()
        handleInput(keyStroke)
      }
    }
  }

  def update(): Unit = {
    selectedSkill match {
      case Some(skill: Woodcutting) =>
        // Update action progress only for Woodcutting
        if (actionProgress >= 1.0) {
          skill.xp += 10 // Award XP after each 5-second action
          actionProgress = 0.0 // Reset action progress

          // Check if the skill levels up
          if (skill.xp >= skill.xpForNextLevel) {
            skill.level += 1
            skill.xp = 0 // Reset XP for the next level
          }
        } else {
          actionProgress += 1.0 / (actionDurationSeconds * 60) // Progress increases over 5 seconds
        }
      case _ => // Do nothing for unimplemented skills
    }
  }

  def render(graphics: TextGraphics): Unit = {
    screen.clear()

    // Render left menu
    renderMenu(graphics)

    // Render selected skill in the main pane
    selectedSkill match {
      case Some(skill: Woodcutting) =>
        renderSkillUI(graphics, skill)
      case Some(skill: Mining) =>
        renderSkillUI(graphics, skill)
      case Some(skill: Woodworking) =>
        renderNotImplemented(graphics, skill)
      case Some(skill: Stonecutting) =>
        renderNotImplemented(graphics, skill)
      case _ => // Do nothing
    }
  }

  def renderMenu(graphics: TextGraphics): Unit = {
    graphics.putString(2, 1, "Gathering Skills:")
    gatheringSkills.zipWithIndex.foreach { case (skill, index) =>
      graphics.putString(2, 3 + index, s" ${if (selectedSkill.contains(skill)) ">" else " "} ${skill.name}")
    }

    graphics.putString(2, 6 + gatheringSkills.size, "Manufacturing Skills:")
    manufacturingSkills.zipWithIndex.foreach { case (skill, index) =>
      graphics.putString(2, 8 + gatheringSkills.size + index, s" ${if (selectedSkill.contains(skill)) ">" else " "} ${skill.name}")
    }
  }

  def renderSkillUI(graphics: TextGraphics, skill: Skill): Unit = {
    graphics.putString(20, 1, s"${skill.name} Level: ${skill.level}")
    graphics.putString(20, 2, s"XP: ${skill.xp} / ${skill.xpForNextLevel}")

    // Render skill XP progress bar (Blue)
    graphics.putString(20, 4, "XP Progress:")
    renderProgressBar(graphics, 20, 5, skill.progressToNextLevel, TextColor.ANSI.BLUE)

    // Render action progress bar (Green) if applicable
    if (skill.isInstanceOf[Woodcutting]) {
      graphics.putString(20, 7, "Action Progress:")
      renderProgressBar(graphics, 20, 8, actionProgress, TextColor.ANSI.GREEN)
    }
  }

  def renderNotImplemented(graphics: TextGraphics, skill: Skill): Unit = {
    graphics.putString(20, 1, s"${skill.name}: Not Implemented")
  }

  def renderProgressBar(graphics: TextGraphics, x: Int, y: Int, progress: Double, color: TextColor): Unit = {
    val progressBarLength = 40
    val filledLength = (progress * progressBarLength).toInt

    graphics.setForegroundColor(color)

    // Render the progress bar using colored [ ] pairs
    for (i <- 0 until progressBarLength) {
      if (i < filledLength) {
        graphics.putString(x + i, y, "[")
        graphics.putString(x + i + 1, y, "]")
      } else {
        graphics.putString(x + i, y, " ")
        graphics.putString(x + i + 1, y, " ")
      }
    }

    graphics.setForegroundColor(TextColor.ANSI.DEFAULT) // Reset to default color
  }

  def handleInput(keyStroke: KeyStroke): Unit = {
    keyStroke.getKeyType match {
      case KeyType.ArrowDown =>
        navigateMenu(1)
      case KeyType.ArrowUp =>
        navigateMenu(-1)
      case _ => // Other keys can be handled here if necessary
    }
  }

  def navigateMenu(direction: Int): Unit = {
    // All skills combined (for easier navigation)
    val allSkills = gatheringSkills ++ manufacturingSkills
    val currentIndex = allSkills.indexOf(selectedSkill.get)
    val newIndex = (currentIndex + direction) match {
      case i if i < 0 => allSkills.size - 1
      case i if i >= allSkills.size => 0
      case i => i
    }
    selectedSkill = Some(allSkills(newIndex))
  }
}
