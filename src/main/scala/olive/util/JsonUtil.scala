package olive.util

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule

object JsonUtil {
  private val mapper = new ObjectMapper()
  mapper.registerModule(DefaultScalaModule)

  def fromJson[T](json: String)(implicit manifest: Manifest[T]) = mapper.readValue(json, manifest.runtimeClass.asInstanceOf[Class[T]])

  def toJson(obj: Any): String = mapper.writeValueAsString(obj)
}