package pl.setblack.bulafa.domain.run.state

import pl.setblack.bulafa.domain.data.state.Article

case class PresentationRun(
                            currentPage: Option[Article] = None,
                            nextPages: List[Article] = Nil,
                            prevPages : List[Article] = Nil
                          ) {

}
