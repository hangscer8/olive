package olive.service

import java.awt.Toolkit
import java.awt.datatransfer.DataFlavor

import akka.actor.{Actor, Timers}
import olive.util.JsonUtil

import scala.concurrent.duration._
import scala.util.{Failure, Success, Try}
import scala.collection.JavaConverters._

class ClipBoardActor extends Actor with Timers {
  lazy val clipboard = Toolkit.getDefaultToolkit.getSystemClipboard
  var carchedContent: List[String] = Nil

  var carchedLength = 0

  val flag = 10

  override def preStart(): Unit = {
    timers.startPeriodicTimer("slow", "getContentsSlow", 1 seconds)
  }

  def getContents: Try[String] = Try(clipboard.getContents(null))
    .map { transferable =>
      transferable.isDataFlavorSupported(DataFlavor.javaFileListFlavor) match {
        case true =>
          JsonUtil.toJson(transferable.getTransferData(DataFlavor.javaFileListFlavor).asInstanceOf[java.util.List[java.io.File]].asScala.sortBy(_.lastModified()))
        case false =>
          transferable.getTransferData(DataFlavor.stringFlavor).asInstanceOf[String]
      }
    }

  def receiveFast(remainTimes: Int): Receive = {
    case "getContentsFast" =>
      getContents match {
        case Success(mayBeNewStringSelection) =>
          carchedContent match {
            case `mayBeNewStringSelection` :: _ => //the same selection ,follow remainTimes
              remainTimes match {
                case 1 =>
                  timers.cancelAll()
                  timers.startPeriodicTimer("slow", "getContentsSlow", 1 seconds)
                  context.unbecome()
                case _ => context.become(receiveFast(remainTimes - 1))
              }
            case _ =>
              //new selection ; reset remainTimes
              context.system.eventStream.publish(NewSelectionEvent(mayBeNewStringSelection))
              carchedLength += 1
              carchedContent ::= mayBeNewStringSelection
              context.become(receiveFast(30))
          }
        case Failure(exception) =>
          println(exception)
      }
    case _ =>
  }
  org.h2.bnf.Bnf
  override def receive: Receive = {
    case "getContentsSlow" =>
      getContents match {
        case Success(mayBeNewStringSelection) =>
          carchedLength match {
            case 0 =>
              //first new
              context.system.eventStream.publish(NewSelectionEvent(mayBeNewStringSelection))
              carchedLength += 1
              carchedContent ::= mayBeNewStringSelection
            case _ =>
              carchedContent match {
                case `mayBeNewStringSelection` :: _ => //the same selection
                case _ =>
                  //new selection
                  context.system.eventStream.publish(NewSelectionEvent(mayBeNewStringSelection))
                  carchedLength += 1
                  carchedContent ::= mayBeNewStringSelection
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