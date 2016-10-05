package pl.setblack.movedev.show

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import pl.setblack.lsa.resources.JVMResources
import pl.setblack.lsa.security._
import pl.setblack.lsa.server.JVMNexus
import pl.setblack.log.LogConfig
import upickle.default._

import scala.concurrent.Future
import scala.util.Try


object BootServer {
  import scala.concurrent.ExecutionContext.Implicits.global
  def main(s : Array[String]):Unit = {

    implicit val system = ActorSystem()
    implicit val materializer = ActorMaterializer()

    LogConfig.initConfig()

        val server =new JVMNexus()
        val node = server.start(system, materializer)





  }


}