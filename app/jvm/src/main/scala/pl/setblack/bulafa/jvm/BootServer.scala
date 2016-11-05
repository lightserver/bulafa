package pl.setblack.bulafa.jvm

import java.util.UUID

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import pl.setblack.bulafa.domain.data.InArticle
import pl.setblack.bulafa.domain.data.InArticle.{ArticleDomain, ArticleEvent}
import pl.setblack.bulafa.domain.data.state.ArticleContent
import pl.setblack.bulafa.domain.run.InSynchronizer
import pl.setblack.bulafa.domain.run.InSynchronizer.SynchronizerDomain
import pl.setblack.bulafa.jvm.watcher.Watcher
import pl.setblack.log.LogConfig
import pl.setblack.lsa.events.DomainRef
import pl.setblack.lsa.server.JVMNexus
import slogging.{LazyLogging, LogLevel, LoggerConfig, SLF4JLoggerFactory}


object BootServer extends LazyLogging {
  def main(s: Array[String]): Unit = {

    implicit val system = ActorSystem()
    implicit val materializer = ActorMaterializer()

    LoggerConfig.factory = SLF4JLoggerFactory()
    LoggerConfig.level = LogLevel.DEBUG



    val server = new JVMNexus()
    val node = server.start(system, materializer)

    val rootArticlePath = Seq( "root");
    val rootArticleContent =new ArticleContent("", UUID.randomUUID())
    val rootArticle:DomainRef[ArticleEvent] = node.registerDomain(InArticle.toArticlePath(rootArticlePath),
      new ArticleDomain(rootArticlePath, rootArticleContent))

    val synchronizerRef = node.registerDomain(Seq("synchronizer"), new SynchronizerDomain(rootArticle))
    synchronizerRef.restoreDomain()


    val watcher = new Watcher("/home/jarek/dev/presentations/bulafa")
    node.registerDomainListener(watcher, synchronizerRef)

    watcher.start(synchronizerRef)
  }


}