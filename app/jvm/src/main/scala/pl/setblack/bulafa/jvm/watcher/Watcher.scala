package pl.setblack.bulafa.jvm.watcher
import scala.collection.JavaConversions._
import akka.actor.{ActorRef, ActorSystem}
import better.files._
import FileWatcher._
import java.nio.file.{Path, WatchEvent, StandardWatchEventKinds => EventType}
import java.util.UUID

import pl.setblack.bulafa.domain.data.InArticle.{ArticleEvent, Update}
import pl.setblack.bulafa.domain.run.InSynchronizer.{Create, SynchronizerEvent}
import pl.setblack.bulafa.domain.run.state.Synchronizer
import pl.setblack.lsa.events.{DomainListener, DomainRef}

class Watcher(val path: String)(implicit system: ActorSystem) extends  DomainListener[Synchronizer, SynchronizerEvent]{
  val dir = File(path)
  def start(synchronizer:DomainRef[SynchronizerEvent]) = {
    dir.walk(10).foreach(f => {

      fileCreated(synchronizer, f)
    })
    watchIt(dir, synchronizer)
  }

  def fileCreated(synchronizer: DomainRef[SynchronizerEvent], file: File): Unit = {
    val subpath = dir.path.relativize(file.path).iterator().toSeq.map( _.toString)
    //println(s"have ${dir.path.relativize(f.path).toString}")
    println(s"created ${subpath}")
    val fileContent  = if ( file.isDirectory ) {""} else { file.contentAsString}
    synchronizer.send(Create(fileContent, subpath, UUID.randomUUID()))
  }

  private def watchIt(dir : File, synchronizer: DomainRef[SynchronizerEvent]): Unit = {

    val watcher: ActorRef =  dir.newWatcher(recursive = true)

    // register partial function for an event
    watcher ! on(EventType.ENTRY_DELETE) {
      case file if file.isDirectory => println(s"$file dir got deleted")
      case file => println(s"$file got deleted")
    }

    // watch for multiple events
    watcher ! when(events = EventType.ENTRY_CREATE, EventType.ENTRY_MODIFY) {
      case (EventType.ENTRY_CREATE, file) => {
        val subpath = dir.path.relativize(file.path).iterator().toSeq.map( _.toString)
        //println(s"have ${dir.path.relativize(f.path).toString}")
        println(s"created in ${subpath}")
        fileCreated(synchronizer, file)
      }
      case (EventType.ENTRY_MODIFY, file) => println(s"$file got modified")
    }
  }

  override def onDomainChanged(domainState: Synchronizer, ev: Option[SynchronizerEvent]): Unit ={
    println(s"changed synchronizer")
  }
}

object Watcher {

}
