package pl.setblack.bulafa.domain.run.state

import pl.setblack.bulafa.domain.data.ArticleDomain.ArticleDomainRef
import pl.setblack.bulafa.domain.run.Synchronizer.CreatedArticles

case class Synchronizer(knownArticles: Map[Seq[String], ArticleDomainRef]) {

  type SeqOfPaths = Seq[Seq[String]]

  def this(rootArticle: ArticleDomainRef) = this(Map(Seq[String]() -> rootArticle))


  def whichArticlesToPut(path: Seq[String]): CreatedArticles = {
    CreatedArticles(internalSelectArticlesToCreate(path))
  }



  private def internalSelectArticlesToCreate( path: Seq[String]) : SeqOfPaths = {
    val o: Option[SeqOfPaths] = knownArticles.get(path).map(_ => Seq())

    val result: SeqOfPaths = o.getOrElse(internalSelectArticlesToCreate(path.dropRight(1)) ++  Seq(path))
    result
  }


}

