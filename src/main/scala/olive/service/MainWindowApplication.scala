package olive.service

import javafx.application.Application
import javafx.concurrent.Worker.State
import javafx.scene.Scene
import javafx.scene.control._
import javafx.scene.layout._
import javafx.scene.web.WebView
import javafx.stage.Stage
import netscape.javascript.JSObject
import olive.util.UIUtil

class MainWindowApplication extends Application {
  override def start(primaryStage: Stage): Unit = {
    val root = new BorderPane()
    val menuBar = new MenuBar()
    menuBar.useSystemMenuBarProperty.set(true)
    menuBar.getMenus.addAll(new Menu("File"), new Menu("Edit"), new Menu("Actions"))
    val scene = new Scene(root, 900, 800)
    root.setTop(menuBar)
    val webView = new WebView()
    root.setCenter(webView)
    val webEngine = webView.getEngine
    webEngine.setJavaScriptEnabled(true)
    webEngine.setOnAlert(e => UIUtil.alertHandler(e.getData))
    webEngine.setOnError(e => UIUtil.errorHandler(e))
    webEngine.setConfirmHandler(msg => UIUtil.confirmHandler(msg))
    webEngine.getLoadWorker.stateProperty().addListener((_, _, newState) => {
      newState match {
        case State.SUCCEEDED =>
          val window = webEngine.executeScript("window").asInstanceOf[JSObject]
          window.setMember("haha", HahaService)
        case _ =>
      }
    })
    webEngine.load(this.getClass.getResource("/html/index.html").toExternalForm)
    primaryStage.setScene(scene)
    primaryStage.setTitle("olive")
    primaryStage.show()
  }
}