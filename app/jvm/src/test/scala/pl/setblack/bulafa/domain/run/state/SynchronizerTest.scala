package pl.setblack.bulafa.domain.run.state

import org.scalatest.{FunSpec, Matchers}
import pl.setblack.bulafa.domain.data.ArticleDomain._
import pl.setblack.bulafa.domain.run.InSynchronizer.NewArticlesState
import pl.setblack.lsa.concurrency.BadActorRef
import pl.setblack.lsa.events.impl.NodeEvent

class SynchronizerTest extends FunSpec with Matchers {
  val fakeNodeRef = new BadActorRef[NodeEvent] {
    override def send(event: NodeEvent): Unit = ???
  }
  val rootArticleRef = new ArticleDomainRef(Seq(), fakeNodeRef)

  describe("synchronizer") {
    val synchron = new Synchronizer(rootArticleRef)
    val creator = (x: Seq[String]) => new ArticleDomainRef(x, fakeNodeRef)

    it("should return no new articles root article") {
      synchron.putMissingArticlesInHierarchy(Seq(), creator).paths should be(empty)
    }

    it("should return new one new articles first level entry") {
      synchron.putMissingArticlesInHierarchy(Seq("firstLevel"), creator).paths should be( Seq(Seq("firstLevel")))
    }

    it("should contain new one  article ref after first level insert") {
      ( synchron.putMissingArticlesInHierarchy(Seq("firstLevel"), creator)
          .synchronizer.knownArticles.get(Seq("firstLevel"))
          shouldBe defined )
    }

    it("should return new two new articles for first and second level entry") {
      ( synchron.putMissingArticlesInHierarchy(Seq("firstLevel", "secondLevel"), creator).paths
        should be( Seq(Seq("firstLevel"), Seq("firstLevel", "secondLevel"))) )
    }

    it("should return only one articles for second level entry") {
      val newSynch =  synchron.putMissingArticlesInHierarchy(Seq("firstLevel", "secondLevel1"), creator).synchronizer
      ( newSynch.putMissingArticlesInHierarchy(Seq("firstLevel", "secondLevel2"), creator).paths
        should be (Seq(Seq("firstLevel", "secondLevel2"))))
    }



  }
}
