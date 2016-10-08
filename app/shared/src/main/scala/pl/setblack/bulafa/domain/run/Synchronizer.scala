package pl.setblack.bulafa.domain.run


object Synchronizer {

  sealed trait SynchronizerEvent

  case class Create(txt: String, path: Seq[String]) extends SynchronizerEvent

  class SynchronizerDomain {

  }

  case class CreatedArticles(paths: Seq[Seq[String]]) {
    def this () = this( Seq())
  }

}

