package pl.setblack.bulafa.domain.run.state

import pl.setblack.bulafa.domain.data.InArticle.ArticleDomainRef
import pl.setblack.bulafa.domain.run.InSynchronizer.NewArticlesState

case class Synchronizer(knownArticles: Map[Seq[String], ArticleDomainRef]) {

  type SeqOfPaths = Seq[Seq[String]]
  type ArticleFactory = (Seq[String]) => Option[ArticleDomainRef]

  def this(rootArticle: ArticleDomainRef) = this(Map(Seq[String]() -> rootArticle))



  def putMissingArticlesInHierarchy(path: Seq[String],
                                    articleCreator : ArticleFactory): NewArticlesState =
    (NewArticlesState.apply _ ).tupled( internalSelectArticlesToCreate(path, articleCreator))


 private def putArticle( path: Seq[String], articleCreator : ArticleFactory) : Synchronizer = {
   articleCreator(path).map( artRef => copy(knownArticles +  (path -> artRef))).getOrElse(this)
 }

  private def internalSelectArticlesToCreate( path: Seq[String] , articleFactory: ArticleFactory) : (Synchronizer, SeqOfPaths) = {
    val o: Option[(Synchronizer,SeqOfPaths)] = knownArticles.get(path).map(_ => (this, Seq()))

    val result: (Synchronizer, SeqOfPaths) = o.getOrElse{
      val newState = putArticle(path, articleFactory )
      val combine = newState.internalSelectArticlesToCreate(path.dropRight(1), articleFactory)
      (combine._1, combine._2 ++ Seq(path))
    }
    result
  }

  def dump  = this.knownArticles

}

