package pl.setblack.bulafa.jvm.watcher

import java.util.UUID

import better.files.File
import pl.setblack.bulafa.domain.run.InSynchronizer.{Directory, FileWithContent, SynchronizerEvent}
import pl.setblack.lsa.events.DomainRef

trait FileProcessor {

  def accepts(subpath : Seq[String], file : File) : Boolean

  def process(subpath : Seq[String], file : File) : Seq[SynchronizerEvent]
}


class HTMLFileProcessor extends FileProcessor {

  override def accepts(subpath: Seq[String], file: File): Boolean = file.extension == Some(".html")

  override def process(subpath: Seq[String], file: File): Seq[SynchronizerEvent] = {
    val fileContent = file.contentAsString
    Seq(FileWithContent(fileContent, subpath, UUID.randomUUID()))
  }
}

class DirectoryProcessor extends FileProcessor {
  override def accepts(subpath: Seq[String], file: File): Boolean = file.isDirectory

  override def process(subpath: Seq[String], file: File): Seq[SynchronizerEvent] = {
    Seq(Directory(subpath, UUID.randomUUID()))
  }
}