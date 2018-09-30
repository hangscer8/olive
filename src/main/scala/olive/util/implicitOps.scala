package olive.util

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

}