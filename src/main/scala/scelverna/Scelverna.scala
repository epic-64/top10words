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

    // Draw colorful world map
    val textGraphics = screen.newTextGraphics()
    val random = new Random()

    // Parameters to control landmass generation
    val landProbability = 0.05
    val connectionProbability = 0.8

    // Generate world map with landmasses and oceans
    var previousCharWasLand = false
    for (y <- 0 until 24) {
      previousCharWasLand = false
      for (x <- 0 until 80) {
        val isLand = if (previousCharWasLand) random.nextDouble() < connectionProbability else random.nextDouble() < landProbability
        if (isLand) {
          val char = if (random.nextBoolean()) '(' else ')'
          val color = TextColor.ANSI.values()(random.nextInt(TextColor.ANSI.values().length))
          textGraphics.setForegroundColor(color)
          textGraphics.putString(x, y, char.toString)
          previousCharWasLand = true
        } else {
          textGraphics.putString(x, y, " ") // Draw empty space for ocean
          previousCharWasLand = false
        }
      }
    }

    // Refresh the screen to show the text
    screen.refresh()

    // Wait for user to press any key before closing
    screen.readInput()

    // Stop screen
    screen.stopScreen()
  }
}
