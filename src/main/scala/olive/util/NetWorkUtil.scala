package olive.util

import java.net.NetworkInterface

import implicitOps._

/**
  * Created by Nathan.jiang on 2018/9/30
  */
object NetWorkUtil {
  def ipList: List[String] = {
    NetworkInterface.getNetworkInterfaces.toList
      .flatMap { ni =>
        ni.getInetAddresses.toList
          .filterNot(_.isLinkLocalAddress)
          .filterNot(_.isLoopbackAddress)
          .map(_.getHostAddress)
      }.sorted
  }
}