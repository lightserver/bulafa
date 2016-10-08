package pl.setblack.bulafa.domain.run.state

import org.scalatest.{FunSpec, Matchers}
import pl.setblack.bulafa.domain.data.ArticleDomain._
import pl.setblack.bulafa.domain.run.Synchronizer.CreatedArticles
import pl.setblack.lsa.concurrency.BadActorRef
import pl.setblack.lsa.events.impl.NodeEvent

class SynchronizerTest extends FunSpec with Matchers {
 val rootArticleRef = new ArticleDomainRef(Seq(),   new BadActorRef[NodeEvent] {
   override def send(event: NodeEvent): Unit =  ???
 })

    describe( "synchronizer" ) {
      val synchron = new Synchronizer ( rootArticleRef )


      it ("should return no new articles root article" ) {
        synchron.whichArticlesToPut(Seq() ).paths should be (empty)
      }

      it ("should return new one new articles first level entry" ) {
        synchron.whichArticlesToPut(Seq("firstLevel") ) should be (CreatedArticles(Seq(Seq("firstLevel"))) )
      }

      it ("should return new two new articles for first and second level entry" ) {
        synchron.whichArticlesToPut(Seq("firstLevel", "secondLevel") ) should be (CreatedArticles(Seq(Seq("firstLevel"), Seq("firstLevel","secondLevel"))) )
      }


    }
}
