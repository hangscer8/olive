package olive.util

import java.net.NetworkInterface

/**
  * Created by Nathan.jiang on 2018/9/30
  */
object implicitOps {

  implicit class EnumerationOpt[E](enumeration: java.util.Enumeration[E]) {
    def toList: List[E] = {
      var list = List.empty[E]
      while (enumeration.hasMoreElements) {
        list = enumeration.nextElement() :: list
      }
      list.reverse //保持顺序
    }
  }

  implicit class NetworkInterfaceOpt(networkInterface: NetworkInterface) {
    def getMacAddress: Option[String] = {
      networkInterface.getHardwareAddress match {
        case null => None
        case array =>
          Some(array.map { byte => (byte & 0xff).toHexString }
            .map(hex => hex.length match {
              case 1 => "0" + hex
              case _ => hex
            })
            .mkString(":"))
      }
    }
  }

}