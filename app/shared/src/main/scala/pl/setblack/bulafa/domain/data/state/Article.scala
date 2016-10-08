package pl.setblack.bulafa.domain.data.state

/**
  * Article has PATH and some Content
  *
  */
class Article(
               val path: Seq[String],
               val currentVersions: Seq[ArticleContent]  = Seq(),
               val directKnownSubarticles : Set[String] = Set()
               ) {

}
