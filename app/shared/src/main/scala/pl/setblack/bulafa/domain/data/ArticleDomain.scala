package pl.setblack.bulafa.domain.data

import java.util.UUID

import pl.setblack.bulafa.domain.data.ArticleDomain.ArticleEvent
import pl.setblack.bulafa.domain.data.state.Article
import pl.setblack.lsa.events._

class ArticleDomain(articlePath : Seq[String])
  extends Domain[Article, ArticleEvent](new Article(articlePath), Seq("articles") ++ articlePath )(ArticleDomain.ArticleEventConverter) {


  override protected def processDomain(state: Article, event: ArticleEvent, eventContext: EventContext): Response = {

    DefaultResponse
  }
}


object ArticleDomain {
  sealed trait ArticleEvent

  case class Update(txt  :String, version : UUID, previous : Seq[UUID] ) extends ArticleEvent


  implicit object ArticleEventConverter extends EventConverter[ArticleEvent] {
     import upickle.default._

    override def readEvent(str: String): ArticleEvent = read[ArticleEvent](str)

    override def writeEvent(e: ArticleEvent): String = write(e)
  }
}

