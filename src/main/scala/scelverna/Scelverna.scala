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

class Scelverna {
  private var woodcuttingXP: Int = 0
  private var progress: Double = 0.0 // Progress bar value (0.0 to 1.0)

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

    // Start the progress bar for woodcutting actions (takes 5 seconds)
    Future {
      while (true) {
        if (progress >= 1.0) {
          woodcuttingXP += 10  // Award XP after each 5-second action
          progress = 0.0       // Reset progress
        } else {
          progress += 1.0 / (5 * 60) // Progress increases over 5 seconds
        }
        Thread.sleep(1000 / 60) // Update 60 times per second
      }
    }
  }

  def update(): Unit = {
    // Any game logic update can go here
  }

  def render(graphics: TextGraphics): Unit = {
    graphics.putString(2, 1, s"Woodcutting XP: $woodcuttingXP")

    // Render progress bar for current action
    val progressBarLength = 40
    val filledLength = (progress * progressBarLength).toInt
    val progressBar = "=" * filledLength + " " * (progressBarLength - filledLength)
    graphics.putString(2, 3, s"Progress: [$progressBar]")
  }
}
