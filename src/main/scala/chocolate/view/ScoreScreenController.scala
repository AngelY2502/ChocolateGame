package chocolate.view

import chocolate.MainApp
import chocolate.model.Score
import scalafxml.core.macros.sfxml
import scalafx.Includes._
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.control.{Alert, TableColumn, TableView}


@sfxml
class ScoreScreenController(
                           private val scoreTable: TableView[Score],
                           private val playerName: TableColumn[Score,String],
                           private val playerScore: TableColumn[Score, Int]
                           ){
  scoreTable.items = MainApp.scoreData
  playerName.cellValueFactory = data  => data.value.playerName
  playerScore.cellValueFactory = data => data.value.playerScore

  def handleDelete(): Unit = {
    val selectedIndex = scoreTable.selectionModel.value.selectedIndex.value
    if (selectedIndex >= 0) {
      val score = scoreTable.items().remove(selectedIndex)
      score.delete()
    } else {
      val alert = new Alert(AlertType.Warning) {
        initOwner(MainApp.stage)
        title = "No Selection"
        headerText = "No Score Selected"
        contentText = "Please select a Score in the table."
      }.showAndWait()
    }
  }

  def home(): Unit = {
    MainApp.showHome()
  }

}