package pl.setblack.bulafa.domain.run

import pl.setblack.bulafa.domain.run.state.Synchronizer


object Synchronizer {

  sealed trait SynchronizerEvent

  case class Create(txt: String, path: Seq[String]) extends SynchronizerEvent

  class SynchronizerDomain {

  }

  case class NewArticlesState(synchronizer: Synchronizer, paths: Seq[Seq[String]])

}

