package pl.setblack.bulafa.domain.run

import java.util.UUID

import pl.setblack.bulafa.domain.data.InArticle
import pl.setblack.bulafa.domain.data.InArticle.{ArticleDomain, ArticleDomainRef}
import pl.setblack.bulafa.domain.data.state.ArticleContent
import pl.setblack.bulafa.domain.run.state.Synchronizer
import pl.setblack.lsa.events._
import slogging.{LazyLogging, LoggerFactory}

object InSynchronizer {

  sealed trait SynchronizerEvent

  case class FileWithContent(txt: String, path: Seq[String], uuid  : UUID) extends SynchronizerEvent

  case class Directory(path : Seq[String], uuid: UUID) extends SynchronizerEvent

  case object  Dump extends SynchronizerEvent

  implicit object SynchronizerEventSerializer extends EventConverter[SynchronizerEvent] {
    import upickle.default._
    override def readEvent(str: String): SynchronizerEvent = read[SynchronizerEvent](str)
    override def writeEvent(e: SynchronizerEvent): String = write(e)
  }

  class SynchronizerDomain(rootArticle: ArticleDomainRef)
    extends Domain[Synchronizer, SynchronizerEvent](new Synchronizer(rootArticle))
       with LazyLogging {
     val syncLogger = LoggerFactory.getLogger("SYNCHRO")
     var lastSize = 0

    def dump(state: Synchronizer): Response[Synchronizer] = {
      TransientEvent(Some(state))
    }

    override protected def processDomain(state: Synchronizer, event: SynchronizerEvent, eventContext: EventContext): Response[Synchronizer] = {

      event match {
        case create: FileWithContent => addArticle(state, create, eventContext)
        case dir : Directory => addDirectory( state, dir, eventContext)
        case Dump => dump(state)
      }
    }


    private def addArticle(state: Synchronizer, create: FileWithContent, eventContext: EventContext): Response[Synchronizer] = {
      syncLogger.debug(create.path.mkString(","))
      addDomain(state, create.path, Some(create.txt), create.uuid, eventContext)
    }

    private def addDirectory(state: Synchronizer, dir: Directory, eventContext: EventContext): Response[Synchronizer] = {
      syncLogger.debug(dir.path.mkString(","))
        addDomain(state, dir.path, None, dir.uuid, eventContext)
    }

    private def addDomain(state  :Synchronizer, path : Seq[String], txt  : Option[String], uuid  : UUID, ctx :  EventContext):Response[Synchronizer] = {
      val content = new ArticleContent(txt.getOrElse(""), uuid)
      val factory = (path: Seq[String]) => ctx.createDomain(InArticle.toArticlePath(path), new ArticleDomain(path, content))
      val newState = state.putMissingArticlesInHierarchy(path, factory)
      if (newState.synchronizer.knownArticles.size < lastSize) {
        println("niezla kutwa")
      } else {
        if ( newState.paths.size  >  0 && lastSize == newState.synchronizer.knownArticles.size) {
          println("niezla kutwa 2")
        } else {
          lastSize = newState.synchronizer.knownArticles.size
          println(s" size = ${lastSize}")
        }
      }
     // println(s"${this.hashCode()} have new state with: ${newState.synchronizer.knownArticles.size} arts +[${newState.paths.size}]" )
      if ( newState.paths.size > 0 ) {
        new DefaultResponse[Synchronizer](Some(newState.synchronizer))
      } else {
        new TransientEvent(None)
      }

    }
  }



  case class NewArticlesState(synchronizer: Synchronizer, paths: Seq[Seq[String]])
}

