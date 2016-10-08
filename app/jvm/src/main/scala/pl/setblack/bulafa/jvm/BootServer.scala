package pl.setblack.bulafa.jvm

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import pl.setblack.bulafa.domain.data.ArticleDomain.{ArticleDomain, ArticleEvent}
import pl.setblack.bulafa.jvm.watcher.Watcher
import pl.setblack.log.LogConfig
import pl.setblack.lsa.events.DomainRef
import pl.setblack.lsa.server.JVMNexus


object BootServer {
  def main(s: Array[String]): Unit = {

    implicit val system = ActorSystem()
    implicit val materializer = ActorMaterializer()

    LogConfig.initConfig()

    val server = new JVMNexus()
    val node = server.start(system, materializer)

    val rootArticlePath = Seq("articles", "root");
    val rootArticle:DomainRef[ArticleEvent] = node.registerDomain(rootArticlePath, new ArticleDomain(rootArticlePath))


    val watcher = new Watcher("/home/jarek/tmp")
    watcher.start(rootArticle)
  }


}