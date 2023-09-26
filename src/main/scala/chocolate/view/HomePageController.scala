package chocolate.view

import scalafxml.core.macros.sfxml
import chocolate.MainApp

@sfxml
class HomePageController(){
  def startGame(): Unit = {
    MainApp.showGame()
  }
  def instructions(): Unit = {
    MainApp.showInstructions()
  }
  def quit(): Unit = {
    MainApp.stage.close()
  }
  def ranking(): Unit = {
    MainApp.showScoreScreen()
  }
}