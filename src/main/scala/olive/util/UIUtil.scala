package olive.util

import javafx.scene.control.{ButtonType, Dialog}
import javafx.scene.web.WebErrorEvent

object UIUtil {
  def confirmHandler(msg: String): Boolean = {
    val dialog = new Dialog[ButtonType]()
    dialog.getDialogPane.setContentText(msg)
    dialog.getDialogPane.getButtonTypes.addAll(ButtonType.YES, ButtonType.NO)
    dialog.showAndWait().filter(_ == ButtonType.YES).isPresent
  }

  def alertHandler(msg: String) = {
    val dialog = new Dialog[String]()
    dialog.getDialogPane.setContentText(msg)
    dialog.getDialogPane.getButtonTypes.add(ButtonType.OK)
    dialog.show()
  }

  def errorHandler(e: WebErrorEvent) = {
    println(s"exception:${e.getException} ,message:${e.getMessage} ,eventType:${e.getEventType}")
  }

}