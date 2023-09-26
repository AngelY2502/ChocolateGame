package chocolate

import chocolate.model.Score
import chocolate.util.Database
import chocolate.view.{ChocolateGameController, ScoreScreenController}
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.Scene
import javafx.{scene => jfxs}
import javafx.scene.layout.AnchorPane
import scalafxml.core.{FXMLLoader, NoDependencyResolver}
import scalafx.Includes._
import scalafx.collections.ObservableBuffer


object MainApp extends JFXApp {
  Database.setupDB()
  val scoreData = new ObservableBuffer[Score]()
  scoreData ++= Score.getAllScore()

  val rootResource = getClass.getResource("view/RootLayout.fxml")
  val loader = new FXMLLoader(rootResource, NoDependencyResolver)
  loader.load()
  val roots = loader.getRoot[jfxs.layout.BorderPane]

  stage = new PrimaryStage {
    title = "Chocolate Factory"
    resizable = true
    scene = new Scene {
      root = roots: jfxs.Parent
    }
  }
  var GameController: Option[ChocolateGameController#Controller] = None
  def showGame(): Unit = {
    val resource = getClass.getResource("view/ChocolateGame.fxml")
    val loader = new FXMLLoader(resource, NoDependencyResolver)
    loader.load
    val roots: AnchorPane = loader.getRoot[jfxs.layout.AnchorPane]
    val control = loader.getController[ChocolateGameController#Controller]()
    GameController = Option(control)
    this.roots.setCenter(roots)
  }

  var scoreController: Option[ScoreScreenController#Controller] = None
  def showScoreScreen(): Unit = {
    val resource = getClass.getResource("view/ScoreScreen.fxml")
    val loader = new FXMLLoader(resource, NoDependencyResolver)
    loader.load()
    val roots: AnchorPane = loader.getRoot[jfxs.layout.AnchorPane]
    val control = loader.getController[ScoreScreenController#Controller]()
    scoreController = Option(control)
    this.roots.setCenter(roots)
  }

  def showHome(): Unit = {
    val resource = getClass.getResource("view/HomePage.fxml")
    val loader = new FXMLLoader(resource, NoDependencyResolver)
    val roots: AnchorPane = loader.load()
    this.roots.setCenter(roots)
  }

  def showInstructions(): Unit = {
    val resource = getClass.getResource("view/Instructions.fxml")
    val loader = new FXMLLoader(resource, NoDependencyResolver)
    val roots: AnchorPane = loader.load()
    this.roots.setCenter(roots)
  }
  showHome()

}