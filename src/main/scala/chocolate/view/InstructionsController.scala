package chocolate.view

import scalafxml.core.macros.sfxml
import chocolate.MainApp

@sfxml
class InstructionsController() {
  def home(): Unit = {
    MainApp.showHome()
  }
}