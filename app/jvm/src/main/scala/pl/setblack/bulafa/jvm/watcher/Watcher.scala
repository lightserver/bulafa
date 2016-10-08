package pl.setblack.bulafa.jvm.watcher
import scala.collection.JavaConversions._
import akka.actor.{ActorRef, ActorSystem}
import better.files._
import FileWatcher._
import java.nio.file.{Path, WatchEvent, StandardWatchEventKinds => EventType}

import pl.setblack.bulafa.domain.data.ArticleDomain.{ArticleEvent, Update}
import pl.setblack.lsa.events.DomainRef

class Watcher(val path: String)(implicit system: ActorSystem) {
  def start(rootArticle:DomainRef[ArticleEvent]) = {
    val dir = File(path)

    dir.walk(10).foreach(f => {

      val subpath = dir.path.relativize(f.path).iterator().toSeq.map( _.toString)
      //println(s"have ${dir.path.relativize(f.path).toString}")
      println(s"have ${subpath}")
      //rootArticle.send(Update("empty"),  )

    })
    watchIt(dir)
  }

  private def watchIt(dir : File): Unit = {

    val watcher: ActorRef =  dir.newWatcher(recursive = true)

    // register partial function for an event
    watcher ! on(EventType.ENTRY_DELETE) {
      case file if file.isDirectory => println(s"$file dir got deleted")
      case file => println(s"$file got deleted")
    }

    // watch for multiple events
    watcher ! when(events = EventType.ENTRY_CREATE, EventType.ENTRY_MODIFY) {
      case (EventType.ENTRY_CREATE, file) => println(s"$file got created")
      case (EventType.ENTRY_MODIFY, file) => println(s"$file got modified")
    }
  }
}

object Watcher {

}
