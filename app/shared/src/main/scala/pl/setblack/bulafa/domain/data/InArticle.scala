package pl.setblack.bulafa.domain.data

import java.util.UUID

import pl.setblack.bulafa.domain.data.state.{Article, ArticleContent}
import pl.setblack.lsa.events._


object InArticle {

  sealed trait ArticleEvent

  case class Create(txt: String, version: UUID) extends ArticleEvent

  case class Update(txt: String, version: UUID, previous: Seq[UUID]) extends ArticleEvent

  type ArticleDomainRef = DomainRef[ArticleEvent]

  val articlesPrefix = "articles"

  def toArticlePath(path : Seq[String]) = Seq(articlesPrefix) ++ path

  implicit object ArticleEventConverter extends EventConverter[ArticleEvent] {

    import upickle.default._

    override def readEvent(str: String): ArticleEvent = read[ArticleEvent](str)

    override def writeEvent(e: ArticleEvent): String = write(e)
  }


  class ArticleDomain(articlePath: Seq[String], content :ArticleContent)
    extends Domain[Article, ArticleEvent](new Article(articlePath, Seq(content))) {


    override protected def processDomain(state: Article, event: ArticleEvent, eventContext: EventContext): Response[Article] = {

      new DefaultResponse
    }
  }


}

