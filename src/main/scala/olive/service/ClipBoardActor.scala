package olive.service

import java.awt.Toolkit
import java.awt.datatransfer.DataFlavor
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream

import akka.actor.{Actor, Timers}
import javax.imageio.ImageIO

import scala.collection.JavaConverters._
import scala.concurrent.duration._
import scala.util.{Failure, Success, Try}

class ClipBoardActor extends Actor with Timers {
  lazy val clipboard = Toolkit.getDefaultToolkit.getSystemClipboard
  var carchedContent: Content = null

  override def preStart(): Unit = {
    timers.startPeriodicTimer("slow", "getContentsSlow", 1 seconds)
  }

  def getContents: Try[Content] = Try(clipboard.getContents(null))
    .map { transferable =>
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
    }

  def receiveFast(remainTimes: Int): Receive = {
    case "getContentsFast" =>
      getContents match {
        case Success(mayBeNewContent) =>
          carchedContent match {
            case `mayBeNewContent` => //the same selection ,follow remainTimes
              remainTimes match {
                case 1 =>
                  timers.cancelAll()
                  timers.startPeriodicTimer("slow", "getContentsSlow", 1 seconds)
                  context.unbecome()
                case _ => context.become(receiveFast(remainTimes - 1))
              }
            case _ =>
              //new selection ; reset remainTimes
              context.system.eventStream.publish(NewSelectionEvent(mayBeNewContent))
              carchedContent = mayBeNewContent
              context.become(receiveFast(30))
          }
        case Failure(exception) =>
          println(exception)
          timers.cancelAll()
          timers.startPeriodicTimer("slow", "getContentsSlow", 1 seconds)
          context.unbecome()
      }
    case _ =>
  }

  override def receive: Receive = {
    case "getContentsSlow" =>
      getContents match {
        case Success(mayBeNewContent) =>
          carchedContent match {
            case null =>
              //first new
              context.system.eventStream.publish(NewSelectionEvent(mayBeNewContent))
              carchedContent = mayBeNewContent
            case _ =>
              carchedContent match {
                case `mayBeNewContent` => //the same selection
                case _ =>
                  //new selection
                  context.system.eventStream.publish(NewSelectionEvent(mayBeNewContent))
                  carchedContent = mayBeNewContent
                  timers.cancelAll()
                  timers.startPeriodicTimer("fast", "getContentsFast", 0.5 seconds)
                  context.become(receiveFast(30))
              }
          }
        case Failure(exception) =>
          println(exception)
      }
    case _ =>
  }
}