package pl.setblack.bulafa.domain.run

import java.util.UUID

import pl.setblack.bulafa.domain.data.InArticle
import pl.setblack.bulafa.domain.data.InArticle.{ArticleDomain, ArticleDomainRef}
import pl.setblack.bulafa.domain.data.state.ArticleContent
import pl.setblack.bulafa.domain.run.state.Synchronizer
import pl.setblack.lsa.events._

object InSynchronizer {

  sealed trait SynchronizerEvent

  case class Create(txt: String, path: Seq[String], uuid  : UUID) extends SynchronizerEvent

  implicit object SynchronizerEventSerializer extends EventConverter[SynchronizerEvent] {
    import upickle.default._
    override def readEvent(str: String): SynchronizerEvent = read[SynchronizerEvent](str)
    override def writeEvent(e: SynchronizerEvent): String = write(e)
  }

  class SynchronizerDomain(rootArticle: ArticleDomainRef) extends Domain[Synchronizer, SynchronizerEvent](
    new Synchronizer(rootArticle)) {

    override protected def processDomain(state: Synchronizer, event: SynchronizerEvent, eventContext: EventContext): Response[Synchronizer] = {
      event match {
        case create: Create => synchronizeArticle(state, create, eventContext)
      }
    }

    private def synchronizeArticle(state: Synchronizer, create: Create, eventContext: EventContext): Response[Synchronizer] = {
      val content = new ArticleContent(create.txt, create.uuid)
      val factory = (path: Seq[String]) => eventContext.createDomain(InArticle.toArticlePath(path), new ArticleDomain(path, content))
      val newState = state.putMissingArticlesInHierarchy(create.path, factory)
      new DefaultResponse[Synchronizer](Some(newState.synchronizer))
    }
  }

  case class NewArticlesState(synchronizer: Synchronizer, paths: Seq[Seq[String]])
}

