package pl.setblack.bulafa.domain.run

import pl.setblack.bulafa.domain.data.ArticleDomain.ArticleDomainRef
import pl.setblack.bulafa.domain.run.state.Synchronizer
import pl.setblack.lsa.events.{Domain, EventContext, EventConverter, Response}


object InSynchronizer {

  sealed trait SynchronizerEvent

  case class Create(txt: String, path: Seq[String]) extends SynchronizerEvent

  implicit object SynchronizerEventSerializer extends EventConverter[SynchronizerEvent] {
    import upickle.default._
    override def readEvent(str: String): SynchronizerEvent = read[SynchronizerEvent](str)

    override def writeEvent(e: SynchronizerEvent): String = write(e)
  }

  class SynchronizerDomain(rootArticle: ArticleDomainRef, path : Seq[String]) extends  Domain[Synchronizer, SynchronizerEvent](
    new Synchronizer(rootArticle )){
    override protected def processDomain(state: Synchronizer, event: SynchronizerEvent, eventContext: EventContext): Response = ???
  }

  case class NewArticlesState(synchronizer: Synchronizer, paths: Seq[Seq[String]])

}

