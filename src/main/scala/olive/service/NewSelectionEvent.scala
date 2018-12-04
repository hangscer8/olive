package olive.service

import java.io.File

trait Content

case class StringContent(content: String) extends Content

case class FileContent(files: Set[File]) extends Content

case class ImageContent(array: Array[Byte]) extends Content

case class NewSelectionEvent(content: Content)