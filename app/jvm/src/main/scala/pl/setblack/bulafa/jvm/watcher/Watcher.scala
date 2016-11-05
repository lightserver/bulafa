package pl.setblack.bulafa.jvm.watcher
import scala.collection.JavaConversions._
import akka.actor.{ActorRef, ActorSystem}
import better.files._
import FileWatcher._
import java.nio.file.{Path, WatchEvent, StandardWatchEventKinds => EventType}
import java.util.UUID

import pl.setblack.bulafa.domain.data.InArticle.{ArticleDomainRef, ArticleEvent, Update}
import pl.setblack.bulafa.domain.run.InSynchronizer.{Directory, Dump, FileWithContent, SynchronizerEvent}
import pl.setblack.bulafa.domain.run.state.Synchronizer
import pl.setblack.lsa.events.{DomainListener, DomainRef}
import slogging.{LazyLogging, LoggerFactory}

class Watcher(val path: String)(implicit system: ActorSystem) extends  DomainListener[Synchronizer, SynchronizerEvent]
with LazyLogging{
  val watchLogger = LoggerFactory.getLogger("WATCHER")
  val dir = File(path)
  val processors = Seq( new HTMLFileProcessor , new DirectoryProcessor)
  def start(synchronizer:DomainRef[SynchronizerEvent]) = {
    dir.walk(10).foreach(f => {
      fileCreated(synchronizer, f)
    })

    synchronizer.send(Dump)

    logger.debug("done synchronizing.....")

    watchIt(dir, synchronizer)
  }

 def fileCreated(synchronizer: DomainRef[SynchronizerEvent], file: File): Unit = {
    val subpath = dir.path.relativize(file.path).iterator().toSeq.map( _.toString)

   val events = ( this.processors
      .filter( _.accepts(subpath, file))
      .map( _.process(subpath, file))
          .foldLeft (Seq[SynchronizerEvent]() ) (_ ++ _ ))
   events.foreach( e => {
     e match {
       case create: FileWithContent => watchLogger.debug(create.path.mkString(","))
       case dir : Directory => watchLogger.debug(dir.path.mkString(","))
       case _ =>
     }

     synchronizer.send(e)
   })

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

  private def dump(knownArticles: Map[Seq[String], ArticleDomainRef]): Unit = {
    knownArticles.keys.foreach(key => println(s" have key ${key}"))
  }

  override def onDomainChanged(domainState: Synchronizer, ev: Option[SynchronizerEvent]): Unit ={

    ev match {
      case Some(Dump) => dump(domainState.knownArticles)
      case _ => println("changed synch")
    }

  }



}


object Watcher {

}
