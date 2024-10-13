package scelverna

import com.googlecode.lanterna.TerminalSize
import com.googlecode.lanterna.TextColor
import com.googlecode.lanterna.screen.{Screen, TerminalScreen}
import com.googlecode.lanterna.terminal.{DefaultTerminalFactory, Terminal}
import com.googlecode.lanterna.terminal.swing.SwingTerminalFontConfiguration
import com.googlecode.lanterna.input.KeyStroke
import com.googlecode.lanterna.input.KeyType
import com.googlecode.lanterna.graphics.TextGraphics

import java.awt.{Font, GraphicsEnvironment}
import scala.concurrent.duration.*
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
  def xpForNextLevel: Int         = level * 100 // Example XP progression per level
  def progressToNextLevel: Double = xp.toDouble / xpForNextLevel
}

case class Woodcutting() extends Skill {
  val name: String = "Woodcutting"
  var xp: Int      = 0
  var level: Int   = 1
}

case class Mining() extends Skill {
  val name: String = "Mining"
  var xp: Int      = 0
  var level: Int   = 1
}

case class Woodworking() extends Skill {
  val name: String = "Woodworking"
  var xp: Int      = 0
  var level: Int   = 1
}

case class StoneCutting() extends Skill {
  val name: String = "Stonecutting"
  var xp: Int      = 0
  var level: Int   = 1
}

class GameState:
  var activeSkill: Option[Skill]    = None
  var inventory  : Map[String, Int] = Map("Wood" -> 0)

class Scelverna:
  private val state = new GameState()

  // Skills lists
  private val gatheringSkills: List[Skill] = List(Woodcutting(), Mining())
  private val manufacturingSkills: List[Skill] = List(Woodworking(), StoneCutting())

  // State to track the currently selected screen (skills or inventory)
  private var currentScreen: String = "skills" // Default to the skills screen

  // Combine skill names and "Inventory" as selectable menu items
  private val menuItems: List[String] =
    gatheringSkills.map(_.name) ++ manufacturingSkills.map(_.name) :+ "Inventory"

  // Spinner characters for active skill
  private val spinnerChars = List("|", "/", "-", "\\")

  // State to track spinner animation and slow it down
  private var spinnerIndex: Int = 0
  private var spinnerUpdateCounter: Int = 0 // Slows down spinner updates

  // State to track the currently selected skill/item and the active skill
  private var selectedMenuIndex: Int = 0
  private var activeSkill: Option[Skill] = None

  private var actionProgress: Double = 0.0 // Progress bar for the action (0.0 to 1.0)
  private val actionDurationSeconds  = 5.0

  val terminalFactory = new DefaultTerminalFactory()

  private def getFont(family: String, style: Int, size: Int): Font = {
    val availableFonts = GraphicsEnvironment.getLocalGraphicsEnvironment.getAvailableFontFamilyNames
    if (availableFonts.contains(family)) new Font(family, style, size)
    else new Font("Monospaced", style, size) // Fallback to Monospaced
  }

  private val fontConfig = SwingTerminalFontConfiguration.newInstance(getFont("Consolas", Font.PLAIN, 20))

  terminalFactory.setInitialTerminalSize(new TerminalSize(100, 24)) // Adjust width to make left section wider
  terminalFactory.setTerminalEmulatorFontConfiguration(fontConfig)

  val terminal: Terminal = terminalFactory.createTerminal()
  val screen: Screen     = new TerminalScreen(terminal)
  screen.startScreen()
  screen.clear()

  def run(): Unit =
    val graphics: TextGraphics = screen.newTextGraphics()

    // Create an ExecutionContext for the game loop
    implicit val ec: ExecutionContext = ExecutionContext.fromExecutor(Executors.newFixedThreadPool(2))

    // Start the game loop at 60 FPS
    Future {
      val frameDuration = (1000 / 60).millis
      while (true) {
        update(state) // Pass game state to update
        render(graphics, state) // Pass game state to render
        screen.refresh()
        Thread.sleep(frameDuration.toMillis)
      }
    }

    // Handle input for skill selection and activation
    Future {
      while (true) {
        val keyStroke: KeyStroke = screen.readInput()
        handleInput(keyStroke, state) // Pass game state to handleInput
      }
    }
  end run

  def update(state: GameState): Unit =
    // Update the active skill
    state.activeSkill match {
      case Some(skill: Woodcutting) =>
        if (actionProgress >= 1.0) {
          skill.xp += 10 // Award XP after each 5-second action
          actionProgress = 0.0 // Reset action progress

          // Add wood to inventory when completing woodcutting action
          state.inventory = state.inventory.updated("Wood", state.inventory("Wood") + 1)

          // Check if the skill levels up
          if (skill.xp >= skill.xpForNextLevel) {
            skill.level += 1
            skill.xp = 0 // Reset XP for the next level
          }
        } else {
          actionProgress += 1.0 / (actionDurationSeconds * 60) // Progress increases over 5 seconds
        }
      case _                        => // Do nothing for unimplemented skills
    }

    // Update spinner
    if (spinnerUpdateCounter >= 10) {
      spinnerIndex = (spinnerIndex + 1) % spinnerChars.size
      spinnerUpdateCounter = 0
    } else {
      spinnerUpdateCounter += 1
    }
  end update

  def render(graphics: TextGraphics, state: GameState): Unit =
    screen.clear()

    // Always render the left-side menu
    renderMenu(graphics)

    // Decide which screen to display based on the current menu selection
    if (menuItems(selectedMenuIndex) == "Inventory") {
      renderInventory(graphics, state) // Pass game state into inventory renderer
    } else {
      renderSkillUI(graphics, state) // Pass game state into skill renderer
    }

    screen.refresh()
  end render

  def renderMenu(graphics: TextGraphics): Unit =
    // Render "Gathering" header with underline
    graphics.putString(2, 1, "Gathering")
    graphics.putString(2, 2, "----------") // Underline for "Gathering"

    gatheringSkills.zipWithIndex.foreach { case (skill, index) =>
      val color   = if (activeSkill.contains(skill)) TextColor.ANSI.GREEN_BRIGHT else TextColor.ANSI.DEFAULT
      val spinner = if (activeSkill.contains(skill)) s" ${spinnerChars(spinnerIndex)}" else ""
      graphics.setForegroundColor(color)
      graphics.putString(2, 3 + index, s" ${if (selectedMenuIndex == index) ">" else " "} ${skill.name}$spinner")
    }

    graphics.setForegroundColor(TextColor.ANSI.DEFAULT) // Reset color to default

    // Render "Manufacturing" header with underline
    graphics.putString(2, 4 + gatheringSkills.size, "Manufacturing")
    graphics.putString(2, 5 + gatheringSkills.size, "-------------") // Underline for "Manufacturing"

    manufacturingSkills.zipWithIndex.foreach { case (skill, index) =>
      val color   = if (activeSkill.contains(skill)) TextColor.ANSI.GREEN_BRIGHT else TextColor.ANSI.DEFAULT
      val spinner = if (activeSkill.contains(skill)) s" ${spinnerChars(spinnerIndex)}" else ""
      graphics.setForegroundColor(color)
      graphics.putString(
        2,
        6 + gatheringSkills.size + index,
        s" ${if (selectedMenuIndex == gatheringSkills.size + index) ">" else " "} ${skill.name}$spinner"
      )
    }

    // Render "Management" header with underline (Management should never be highlighted)
    graphics.setForegroundColor(TextColor.ANSI.DEFAULT)
    graphics.putString(2, 7 + gatheringSkills.size + manufacturingSkills.size, "Management")
    graphics.putString(2, 8 + gatheringSkills.size + manufacturingSkills.size, "----------")

    // Render the inventory item and highlight if it's active
    val inventoryIndex = gatheringSkills.size + manufacturingSkills.size
    val inventoryColor = if (activeSkill.isEmpty && menuItems(selectedMenuIndex) == "Inventory") TextColor.ANSI.GREEN_BRIGHT else TextColor.ANSI.DEFAULT
    graphics.setForegroundColor(inventoryColor)
    graphics.putString(2, 9 + gatheringSkills.size + manufacturingSkills.size,
      s" ${if (selectedMenuIndex == inventoryIndex) ">" else " "} Inventory")

    graphics.setForegroundColor(TextColor.ANSI.DEFAULT) // Reset color to default
  end renderMenu

  def renderSkillSpinner(graphics: TextGraphics, skill: Skill): Unit =
    val spinnerChar = spinnerChars(spinnerIndex) // Get the current spinner character
    graphics.putString(30, 9, s"Spinner: $spinnerChar ${skill.name} in progress...")
  end renderSkillSpinner

  def renderInventory(graphics: TextGraphics, state: GameState): Unit =
    graphics.putString(30, 1, "Inventory")
    graphics.putString(30, 2, "---------")

    // Display inventory items
    state.inventory.zipWithIndex.foreach { case ((item, count), index) =>
      graphics.putString(30, 3 + index, s"$item: $count")
    }

    // Continue rendering the active skill spinner if a skill is active
    state.activeSkill.foreach { skill =>
      graphics.putString(30, 10, "Active skill is still running...")
      renderSkillSpinner(graphics, skill)
    }
  end renderInventory

  def renderSkillUI(graphics: TextGraphics, state: GameState): Unit =
    state.activeSkill match {
      case Some(skill: Woodcutting) =>
        graphics.putString(30, 1, s"${skill.name} Level: ${skill.level}")
        graphics.putString(30, 2, s"XP: ${skill.xp} / ${skill.xpForNextLevel}")

        // Render skill XP progress bar (Blue)
        graphics.putString(30, 4, "XP Progress:")
        renderProgressBar(graphics, 30, 5, skill.progressToNextLevel, TextColor.ANSI.BLUE_BRIGHT)

        // Render action progress bar (Green) if applicable
        graphics.putString(30, 7, "Action Progress:")
        renderProgressBar(graphics, 30, 8, actionProgress, TextColor.ANSI.GREEN)
      case _                        => graphics.putString(30, 1, "No active skill")
    }
  end renderSkillUI

  def renderNotImplemented(graphics: TextGraphics, skill: Skill): Unit =
    graphics.putString(30, 1, s"${skill.name}: Not Implemented")

  def renderProgressBar(graphics: TextGraphics, x: Int, y: Int, progress: Double, color: TextColor): Unit =
    val progressBarLength = 40
    val filledLength      = (progress * (progressBarLength - 2)).toInt // Reserve space for boundaries

    // Render the left boundary in gray
    graphics.setForegroundColor(TextColor.ANSI.WHITE)
    graphics.putString(x, y, "[")

    // Render the progress bar fill material
    graphics.setForegroundColor(color)
    val fillChar = '■' // Use a solid block character for optimal fill
    for (i <- 1 until 1 + filledLength)
      graphics.putString(x + i, y, fillChar.toString)

    // Render the remaining empty space in default color
    graphics.setForegroundColor(TextColor.ANSI.DEFAULT)
    for (i <- 1 + filledLength until progressBarLength - 1)
      graphics.putString(x + i, y, " ")

    // Render the right boundary in gray
    graphics.setForegroundColor(TextColor.ANSI.WHITE)
    graphics.putString(x + progressBarLength - 1, y, "]")

    graphics.setForegroundColor(TextColor.ANSI.DEFAULT) // Reset to default color
  end renderProgressBar

  def handleInput(keyStroke: KeyStroke, state: GameState): Unit =
    keyStroke.getKeyType match {
      case KeyType.ArrowDown =>
        navigateMenu(1)
      case KeyType.ArrowUp   =>
        navigateMenu(-1)
      case KeyType.Enter     =>
        // If "Inventory" is selected, just render the inventory without affecting the active skill
        if (menuItems(selectedMenuIndex) == "Inventory") {
          // Inventory rendering logic, no changes to activeSkill
        } else {
          // Activate the selected skill based on selectedMenuIndex
          if (selectedMenuIndex < gatheringSkills.size) {
            state.activeSkill = Some(gatheringSkills(selectedMenuIndex)) // Gathering skill
          } else if (selectedMenuIndex < gatheringSkills.size + manufacturingSkills.size) {
            state.activeSkill = Some(manufacturingSkills(selectedMenuIndex - gatheringSkills.size)) // Manufacturing skill
          }
        }
      case _                 => // Other keys can be handled here if necessary
    }
  end handleInput

  def navigateMenu(direction: Int): Unit =
    // Cycle through the menu items, including skills and inventory
    selectedMenuIndex = (selectedMenuIndex + direction) match {
      case i if i < 0               => menuItems.size - 1
      case i if i >= menuItems.size => 0
      case i                        => i
    }
  end navigateMenu

end Scelverna
