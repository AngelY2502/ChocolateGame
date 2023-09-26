package chocolate.view

import chocolate.MainApp
import chocolate.model._
import javafx.scene.control.{CheckBox, Label}
import scalafx.application.Platform
import scalafx.beans.property.{BooleanProperty, StringProperty}
import scalafx.collections.ObservableBuffer
import scalafx.scene.control.{Alert, Button, ButtonType, DialogPane, TextField}
import scalafx.scene.control.Alert.AlertType
import scalafxml.core.macros.sfxml
import scalafx.scene.image.ImageView
import scalafx.Includes._
import scalafx.scene.layout.VBox

import java.util.concurrent.{Executors, TimeUnit}
import scala.util.Random

@sfxml
class ChocolateGameController(private val orderLabel: Label,
                              private val scoreLabel: Label,
                              private val timeLabel: Label,
                              private val submitButton: Button,
                              private val startButton: Button,
                              private val almondNuts: ImageView,
                              private val macadamiaNuts: ImageView,
                              private val hazelnutsNuts: ImageView,
                              private val oven: ImageView,
                              private val milkChocolate: ImageView,
                              private val darkChocolate: ImageView,
                              private val matchaChocolate: ImageView,
                              private val roundShape: ImageView,
                              private val heartShape: ImageView,
                              private val shellShape: ImageView,
                              private val strawberryChocolate: ImageView,
                              private val redPacking: CheckBox,
                              private val bluePacking: CheckBox,
                              private val yellowPacking: CheckBox,
                              private var _score: Score) {

  // Items Dictionary
  private var executor = Executors.newSingleThreadScheduledExecutor()
  private val selectedItems: ObservableBuffer[String] = new ObservableBuffer[String]()
  private var currentOrder: Seq[String] = _
  private var scorePoint: Int = 0
  def score: Score = _score
  def score_=(v: Score): Unit = {
    _score = v
  }


  // Timer Properties
  private val remainingTime: StringProperty = StringProperty("20")
  private val timerRunning: BooleanProperty = BooleanProperty(false)
  private var startX: Double = 0.0
  private var startY: Double = 0.0
  submitButton.disable = true

  private def enableDrag(item: ImageView): Unit = {
    item.setOnMousePressed(event => {
      startX = event.getSceneX() - item.getTranslateX()
      startY = event.getSceneY() - item.getTranslateY()
    })
    item.setOnMouseDragged(event => {
      item.setTranslateX(event.getSceneX() - startX)
      item.setTranslateY(event.getSceneY() - startY)
    })
  }

  enableDrag(almondNuts)
  enableDrag(macadamiaNuts)
  enableDrag(hazelnutsNuts)
  enableDrag(milkChocolate)
  enableDrag(darkChocolate)
  enableDrag(matchaChocolate)
  enableDrag(strawberryChocolate)

  private def checkNutOverlap(nut: ImageView, bin: ImageView): Option[String] = {
    val nutBounds = nut.getBoundsInParent
    val ovenBounds = bin.getBoundsInParent

    // Check if the circle bounds intersect with the bin bounds
    if (nutBounds.intersects(ovenBounds)) {
      Some(nut.getId)
    } else {
      None
    }
  }

  private def checkItemOverlap(item: ImageView, bin: ImageView): Option[String] = {
    val itemBounds = item.getBoundsInParent
    val binBounds = bin.getBoundsInParent

    if (itemBounds.intersects(binBounds)) {
      Some(item.getId)
    } else {
      None
    }
  }
  // Initialize method to start the game
  def initialize(): Unit = {
    startGame()
    startButton.disable = true
    submitButton.disable = false
  }

  private def startGame(): Unit = {
    executor = Executors.newSingleThreadScheduledExecutor()
    remainingTime.value = "20"
    scorePoint = 0
    updateScoreLabel()
    generateOrder()
    _score = new Score()

    // Start the timer text
    executor.scheduleAtFixedRate(() => updateTimerText(), 0, 1, TimeUnit.SECONDS)
    executor.schedule(new Runnable() {
      override def run(): Unit = {
        endGame()
      }
    }, 20, TimeUnit.SECONDS)
  }

  private def generateOrder(): Unit = {
    val nuts = List("Almonds", "Hazelnuts", "Macadamia")
    val chocolates = List("Milk", "Dark", "Matcha", "Strawberry")
    val shapes = List("Round", "Heart", "Shell")
    val packings = List("Red", "Blue", "Yellow")

    // Randomly select one item from each category
    val selectedNuts = getRandomItem(nuts)
    val selectedChocolates = getRandomItem(chocolates)
    val selectedShapes = getRandomItem(shapes)
    val selectedPackings = getRandomItem(packings)

    currentOrder = Seq(selectedNuts, selectedChocolates, selectedShapes, selectedPackings)
    showOrder()
  }

  private def getRandomItem(items: List[String]): String = {
    if (items.nonEmpty) {
      val randomIndex = Random.nextInt(items.length)
      items(randomIndex)
    } else {
      "None" // Return a default value (e.g., "None") if the items list is empty
    }
  }

  private def showOrder(): Unit = {
    val orderText = currentOrder.mkString(" - ")
    Platform.runLater(() => {
      orderLabel.setText(s"Order: $orderText")
    })
  }

  private def updateTimerText(): Unit = {
    val remaining = remainingTime.value.toInt
    if (remaining > 0) {
      remainingTime.value = (remaining - 1).toString
      Platform.runLater(() => {
        timeLabel.setText(s"Time left: ${remainingTime.value} seconds")
      })
    } else {
      // Time's up, end the game
      Platform.runLater(() => {
        endGame()
      })
    }
  }

  private def isSelectedPackings(packing: String): Boolean = {
    packing match {
      case "Red" => redPacking.isSelected
      case "Blue" => bluePacking.isSelected
      case "Yellow" => yellowPacking.isSelected
      case _ => false
    }
  }
  def submitGame(): Unit = {
    // Check the selected items against the current order
    val selectedItemsList = getSelectedItemsList()
    val correctItemsCount = currentOrder.count(selectedItemsList.contains)

    if (correctItemsCount > 0) {
      // Award points based on the number of correct items
      val points = correctItemsCount match {
        case 1 => 10
        case 2 => 20
        case 3 => 30
        case 4 => 50
        case _ => 0
      }
      reset()
      scorePoint += points
      updateScoreLabel()
      val Alert = new Alert(AlertType.Information) {
        title = "Correct Items!"
        headerText = "Congratulations!"
        contentText = s"You have selected $correctItemsCount correct item(s) and earned $points points!"
      }
      Alert.showAndWait()


    } else {
      reset()
      val Alert = new Alert(AlertType.Error) {
        title = "Wrong Selection!"
        headerText = "Oops!"
        contentText = "You have not selected any correct items!"
      }
      Alert.showAndWait()
    }
    generateOrder()
  }

  private def reset(): Unit = {
    // Reset nut positions
    almondNuts.setTranslateX(0.0)
    almondNuts.setTranslateY(0.0)
    macadamiaNuts.setTranslateX(0.0)
    macadamiaNuts.setTranslateY(0.0)
    hazelnutsNuts.setTranslateX(0.0)
    hazelnutsNuts.setTranslateY(0.0)
    milkChocolate.setTranslateX(0.0)
    milkChocolate.setTranslateY(0.0)
    darkChocolate.setTranslateX(0.0)
    darkChocolate.setTranslateY(0.0)
    matchaChocolate.setTranslateX(0.0)
    matchaChocolate.setTranslateY(0.0)
    strawberryChocolate.setTranslateX(0.0)
    strawberryChocolate.setTranslateY(0.0)

    // Clear checkbox selections
    redPacking.setSelected(false)
    bluePacking.setSelected(false)
    yellowPacking.setSelected(false)

  }


  private def getSelectedItemsList(): Seq[String] = {
    // Check for circle overlaps with the bin and obtain the circle ID if there is an overlap
    val almondId = checkNutOverlap(almondNuts, oven)
    val macadamiaId = checkNutOverlap(macadamiaNuts, oven)
    val hazelnutsId = checkNutOverlap(hazelnutsNuts, oven)
    val milkChocolateRoundId = checkItemOverlap(milkChocolate, roundShape)
    val darkChocolateRoundId = checkItemOverlap(darkChocolate, roundShape)
    val matchaChocolateRoundId = checkItemOverlap(matchaChocolate, roundShape)
    val strawberryChocolateRoundId = checkItemOverlap(strawberryChocolate, roundShape)
    val milkChocolateHeartId = checkItemOverlap(milkChocolate, heartShape)
    val darkChocolateHeartId = checkItemOverlap(darkChocolate, heartShape)
    val matchaChocolateHeartId = checkItemOverlap(matchaChocolate, heartShape)
    val strawberryChocolateHeartId = checkItemOverlap(strawberryChocolate, heartShape)
    val milkChocolateShellId = checkItemOverlap(milkChocolate, shellShape)
    val darkChocolateShellId = checkItemOverlap(darkChocolate, shellShape)
    val matchaChocolateShellId = checkItemOverlap(matchaChocolate, shellShape)
    val strawberryChocolateShellId = checkItemOverlap(strawberryChocolate, shellShape)


    // Handle the selected items
    val selectedCircleIds = selectedItems
    val selectedPackings = if (redPacking.isSelected) "Red"
    else if (bluePacking.isSelected) "Blue"
    else if (yellowPacking.isSelected) "Yellow"
    else "None"

    // If a circle overlaps with the bin, add its ID to the selectedCircleIds
    if (almondId.isDefined) {
      selectedCircleIds += "Almonds"
    }
    if (macadamiaId.isDefined) {
      selectedCircleIds += "Macadamia"
    }
    if (hazelnutsId.isDefined) {
      selectedCircleIds += "Hazelnuts"
    }
    if (milkChocolateRoundId.isDefined) {
      selectedCircleIds += "Milk"
      selectedCircleIds += "Round"
    }
    if (darkChocolateRoundId.isDefined) {
      selectedCircleIds += "Dark"
      selectedCircleIds += "Round"
    }
    if (matchaChocolateRoundId.isDefined) {
      selectedCircleIds += "Matcha"
      selectedCircleIds += "Round"
    }
    if (strawberryChocolateRoundId.isDefined) {
      selectedCircleIds += "Strawberry"
      selectedCircleIds += "Round"
    }
    if (milkChocolateHeartId.isDefined) {
      selectedCircleIds += "Milk"
      selectedCircleIds += "Heart"
    }
    if (darkChocolateHeartId.isDefined) {
      selectedCircleIds += "Dark"
      selectedCircleIds += "Heart"
    }
    if (matchaChocolateHeartId.isDefined) {
      selectedCircleIds += "Matcha"
      selectedCircleIds += "Heart"
    }
    if (strawberryChocolateHeartId.isDefined) {
      selectedCircleIds += "Strawberry"
      selectedCircleIds += "Heart"
    }
    if (milkChocolateShellId.isDefined) {
      selectedCircleIds += "Milk"
      selectedCircleIds += "Shell"
    }
    if (darkChocolateShellId.isDefined) {
      selectedCircleIds += "Dark"
      selectedCircleIds += "Shell"
    }
    if (matchaChocolateShellId.isDefined) {
      selectedCircleIds += "Matcha"
      selectedCircleIds += "Shell"
    }
    if (strawberryChocolateShellId.isDefined) {
      selectedCircleIds += "Strawberry"
      selectedCircleIds += "Shell"
    }
    selectedCircleIds ++ Seq(selectedPackings)
  }

  private def updateScoreLabel(): Unit = {
    Platform.runLater(() => {
      scoreLabel.setText(s"Score: $scorePoint")
    })
  }

  private def endGame(): Unit = {
    timerRunning.value = false
    submitButton.disable = true
    startButton.disable = false
    Platform.runLater(() => {
      scoreLabel.setText(s"Final Score: $scorePoint")
    })
    executor.shutdown()

    val Alert = new Alert(AlertType.Information) {
      title = "Game Over!"
      headerText = "Time's up!"
      contentText = s"Your final score is $scorePoint"
    }

    // Create a custom DialogPane with a TextField for the player name
    val dialog = new DialogPane()
    val userName = new TextField()
    dialog.setContent(new VBox(8.0, new Label("Enter your name:"), userName))
    dialog.getButtonTypes.addAll(ButtonType.OK, ButtonType.Cancel)

    // Set the custom DialogPane to the Alert
    Alert.dialogPane = dialog

    val answer: Option[ButtonType] = Alert.showAndWait()
    answer match {
      case Some(ButtonType.OK) =>
        val name = try {
          if (userName.text.value.isEmpty) {
            "UnknownPlayer"
          }
          else {userName.text.value}
        } catch {
          case _: Exception => "UnknownPlayer"
        }
        if (_score != null) {
          _score.playerName.value = name
          _score.playerScore.value = scorePoint
          MainApp.scoreData += score
          score.save()
        }
      case _ =>
    }


  }
}




