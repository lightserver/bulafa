package pl.setblack.bulafa.domain.run.state

import pl.setblack.bulafa.domain.data.ArticleDomain.ArticleDomainRef
import pl.setblack.bulafa.domain.run.InSynchronizer.NewArticlesState

case class Synchronizer(knownArticles: Map[Seq[String], ArticleDomainRef]) {

  type SeqOfPaths = Seq[Seq[String]]
  type ArticleFactory = (Seq[String]) => ArticleDomainRef



  def this(rootArticle: ArticleDomainRef) = this(Map(Seq[String]() -> rootArticle))



  def putMissingArticlesInHierarchy(path: Seq[String],
                                    articleCreator : ArticleFactory): NewArticlesState =
    (NewArticlesState.apply _ ).tupled( internalSelectArticlesToCreate(path, articleCreator))


 private def putArticle( path: Seq[String], articleCreator : ArticleFactory) : Synchronizer = {
   copy(knownArticles +  (path -> articleCreator(path) ))
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


}

