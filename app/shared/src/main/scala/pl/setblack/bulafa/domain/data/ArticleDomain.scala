package pl.setblack.bulafa.domain.data

import java.util.UUID

import pl.setblack.bulafa.domain.data.ArticleDomain.ArticleEvent
import pl.setblack.bulafa.domain.data.state.Article
import pl.setblack.lsa.events._


object ArticleDomain {

  sealed trait ArticleEvent

  case class Create(txt : String, version : UUID) extends ArticleEvent

  case class Update(txt  :String, version : UUID, previous : Seq[UUID] ) extends ArticleEvent

  type  ArticleDomainRef = DomainRef[ArticleEvent]


  implicit object ArticleEventConverter extends EventConverter[ArticleEvent] {
    import upickle.default._

    override def readEvent(str: String): ArticleEvent = read[ArticleEvent](str)

    override def writeEvent(e: ArticleEvent): String = write(e)
  }


  class ArticleDomain(articlePath : Seq[String])
    extends Domain[Article, ArticleEvent](new Article(articlePath)) {


    override protected def processDomain(state: Article, event: ArticleEvent, eventContext: EventContext): Response = {

      DefaultResponse
    }
  }





}

