package pl.setblack.bulafa.domain.data.state

import java.util.UUID

case class ArticleContent(
                           text: String = "",
                           version: UUID,
                           previous: Seq[UUID] = Seq()) {


}
