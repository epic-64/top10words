package scelverna

import com.googlecode.lanterna.TerminalSize
import com.googlecode.lanterna.TextColor
import com.googlecode.lanterna.screen.{Screen, TerminalScreen}
import com.googlecode.lanterna.terminal.{DefaultTerminalFactory, Terminal}
import com.googlecode.lanterna.terminal.swing.SwingTerminalFontConfiguration

import java.awt.Font

object Game {
    def main(args: Array[String]): Unit = {
        val game = new Scelverna()
        game.run()
    }
}

class Scelverna {
  def run() = {
    val terminalFactory = new DefaultTerminalFactory()
    val fontConfig      = SwingTerminalFontConfiguration.newInstance(new Font("Monospaced", Font.PLAIN, 24))
    val terminal: Terminal = terminalFactory.createTerminal()

    terminalFactory.setInitialTerminalSize(new TerminalSize(80, 24))
    terminalFactory.setTerminalEmulatorFontConfiguration(fontConfig)

    val screen  : Screen   = new TerminalScreen(terminal)
    screen.startScreen()
    screen.clear()
  }
}
