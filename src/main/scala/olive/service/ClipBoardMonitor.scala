package olive.service

import java.awt.Toolkit
import java.awt.datatransfer.{Clipboard, ClipboardOwner, DataFlavor, Transferable}
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream

import akka.actor.{Actor, Timers}
import javax.imageio.ImageIO

import scala.concurrent.duration._
import scala.collection.JavaConverters._
import scala.util.Try

class ClipBoardMonitor extends Actor with ClipboardOwner with Timers {
  val clipboard = Toolkit.getDefaultToolkit.getSystemClipboard

  override def preStart(): Unit = {
    reGainOwner(clipboard.getContents(this))
    timers.startPeriodicTimer("gainOwner", "gainOwner", 1 seconds)
  }

  override def lostOwnership(c: Clipboard, co: Transferable): Unit = {
    val t = clipboard.getContents(this)
    val content = getContents(t)
    self ! NewSelectionEvent(content)
    Try(reGainOwner(t))
  }

  def getContents(transferable: Transferable): Content =
    transferable.isDataFlavorSupported(DataFlavor.javaFileListFlavor) match {
      case true =>
        FileContent(transferable.getTransferData(DataFlavor.javaFileListFlavor).asInstanceOf[java.util.List[java.io.File]].asScala.toSet)
      case false =>
        transferable.isDataFlavorSupported(DataFlavor.imageFlavor) match {
          case true =>
            val img = transferable.getTransferData(DataFlavor.imageFlavor).asInstanceOf[java.awt.Image]
            img.flush()
            val bi = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_RGB)
            val outputStream = new ByteArrayOutputStream()
            ImageIO.write(bi, "jpg", outputStream)
            outputStream.flush()
            val r = ImageContent(outputStream.toByteArray)
            outputStream.close()
            r
          case false =>
            StringContent(transferable.getTransferData(DataFlavor.stringFlavor).asInstanceOf[String].trim)
        }
    }

  def reGainOwner(t: Transferable) = {
    clipboard.setContents(t, this)
  }

  override def receive: Receive = {
    case "gainOwner" =>
      Try {
        reGainOwner(clipboard.getContents(this))
      }
    case a =>
      println(a)
  }
}