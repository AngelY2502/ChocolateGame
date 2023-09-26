package chocolate.view
import scalafxml.core.macros.sfxml
import chocolate.MainApp

@sfxml
class RootLayoutController(){
  def handleHome(): Unit = {
    MainApp.showHome()
  }
  def handleHelp(): Unit = {
    MainApp.showInstructions()
  }
}