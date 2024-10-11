package scelverna

import com.googlecode.lanterna.TerminalSize
import com.googlecode.lanterna.TextColor
import com.googlecode.lanterna.screen.{Screen, TerminalScreen}
import com.googlecode.lanterna.terminal.{DefaultTerminalFactory, Terminal}
import com.googlecode.lanterna.terminal.swing.SwingTerminalFontConfiguration
import scala.util.Random
import java.awt.Font

object Scelverna {
  def main(args: Array[String]): Unit = {
    // Create terminal and screen with fixed size and larger font
    val terminalFactory = new DefaultTerminalFactory()
    terminalFactory.setInitialTerminalSize(new TerminalSize(80, 24)) // Set terminal size to avoid width/height issues
    val fontConfig = SwingTerminalFontConfiguration.newInstance(new Font("Monospaced", Font.PLAIN, 24)) // Set larger font size
    terminalFactory.setTerminalEmulatorFontConfiguration(fontConfig)
    val terminal: Terminal = terminalFactory.createTerminal()
    val screen: Screen = new TerminalScreen(terminal)

    // Start screen
    screen.startScreen()
    screen.clear()

    val textGraphics = screen.newTextGraphics()
    val random = new Random()

    // Parameters to control landmass generation
    val landProbability = 0.05
    val connectionProbability = 0.8
    val landColor = TextColor.ANSI.GREEN // Fixed color for landmasses
    val oceanColor = TextColor.ANSI.BLUE // Fixed color for ocean

    // Generate initial world map with landmasses and oceans
    var previousCharWasLand = false
    val map = Array.ofDim[Char](24, 80)
    for (y <- 0 until 24) {
      previousCharWasLand = false
      for (x <- 0 until 80) {
        val isLand = if (previousCharWasLand) random.nextDouble() < connectionProbability else random.nextDouble() < landProbability
        if (isLand) {
          map(y)(x) = if (random.nextBoolean()) '(' else ')'
          previousCharWasLand = true
        } else {
          map(y)(x) = ' ' // Empty space for ocean
          previousCharWasLand = false
        }
      }
    }

    // Function to draw the current map state
    def drawMap(): Unit = {
      for (y <- 0 until 24) {
        for (x <- 0 until 80) {
          val char = map(y)(x)
          if (char == '(' || char == ')') {
            textGraphics.setForegroundColor(landColor)
          } else {
            textGraphics.setForegroundColor(oceanColor)
          }
          textGraphics.putString(x, y, char.toString)
        }
      }
      screen.refresh()
    }

    // Add motion to the landmasses by shifting them
    def shiftMap(): Unit = {
      for (y <- 0 until 24) {
        val lastChar = map(y)(79)
        for (x <- 79 until 1 by -1) {
          map(y)(x) = map(y)(x - 1)
        }
        map(y)(0) = lastChar
      }
    }

    // Continuously update the screen to show motion
    var running = true
    while (running) {
      drawMap()
      shiftMap()
      Thread.sleep(50) // Control the speed of the motion for smoother framerate
      if (screen.pollInput() != null) running = false // Stop if any key is pressed
    }

    // Stop screen
    screen.stopScreen()
  }
}
