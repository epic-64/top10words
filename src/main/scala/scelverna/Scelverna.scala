package scelverna

import com.googlecode.lanterna.TerminalSize
import com.googlecode.lanterna.TextColor
import com.googlecode.lanterna.screen.{Screen, TerminalScreen}
import com.googlecode.lanterna.terminal.{DefaultTerminalFactory, Terminal}
import com.googlecode.lanterna.terminal.swing.SwingTerminalFontConfiguration
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

class Scelverna {
  private var skills: List[Skill] = List(Woodcutting()) // Add more skills to the list as needed
  private var actionProgress: Double = 0.0 // Progress bar for the action (0.0 to 1.0)
  private val actionDurationSeconds = 5.0

  def run(): Unit = {
    val terminalFactory = new DefaultTerminalFactory()
    val fontConfig      = SwingTerminalFontConfiguration.newInstance(new Font("Monospaced", Font.PLAIN, 24))
    terminalFactory.setInitialTerminalSize(new TerminalSize(80, 24))
    terminalFactory.setTerminalEmulatorFontConfiguration(fontConfig)

    val terminal: Terminal = terminalFactory.createTerminal()
    val screen  : Screen   = new TerminalScreen(terminal)
    screen.startScreen()
    screen.clear()

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
  }

  def update(): Unit = {
    val woodcutting = skills.head.asInstanceOf[Woodcutting] // In this case, Woodcutting is the first skill

    // Update action progress
    if (actionProgress >= 1.0) {
      woodcutting.xp += 10 // Award XP after each 5-second action
      actionProgress = 0.0 // Reset action progress

      // Check if the skill levels up
      if (woodcutting.xp >= woodcutting.xpForNextLevel) {
        woodcutting.level += 1
        woodcutting.xp = 0 // Reset XP for the next level
      }
    } else {
      actionProgress += 1.0 / (actionDurationSeconds * 60) // Progress increases over 5 seconds
    }
  }

  def render(graphics: TextGraphics): Unit = {
    val woodcutting = skills.head.asInstanceOf[Woodcutting] // Woodcutting is the first skill

    // Display Woodcutting XP and level
    graphics.putString(2, 1, s"${woodcutting.name} Level: ${woodcutting.level}")
    graphics.putString(2, 2, s"XP: ${woodcutting.xp} / ${woodcutting.xpForNextLevel}")

    // Render skill XP progress bar (Blue)
    val xpProgressBarLength = 40
    val xpFilledLength = (woodcutting.progressToNextLevel * xpProgressBarLength).toInt
    graphics.setBackgroundColor(TextColor.ANSI.BLUE)
    graphics.putString(2, 3, s"XP Progress: [" + "=" * xpFilledLength + " " * (xpProgressBarLength - xpFilledLength) + "]")
    graphics.setBackgroundColor(TextColor.ANSI.DEFAULT) // Reset background color

    // Render action progress bar (Green)
    val actionProgressBarLength = 40
    val actionFilledLength = (actionProgress * actionProgressBarLength).toInt
    graphics.setBackgroundColor(TextColor.ANSI.GREEN)
    graphics.putString(2, 5, s"Action Progress: [" + "=" * actionFilledLength + " " * (actionProgressBarLength - actionFilledLength) + "]")
    graphics.setBackgroundColor(TextColor.ANSI.DEFAULT) // Reset background color
  }
}
