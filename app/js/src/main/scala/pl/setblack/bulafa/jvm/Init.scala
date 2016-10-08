package pl.setblack.bulafa.jvm

import japgolly.scalajs.react.{ReactComponentB, ReactDOM}
import org.scalajs.dom._
import pl.setblack.bulafa.domain.run.state.PresentationRun
import pl.setblack.bulafa.jvm.ui.Backends
import pl.setblack.lsa.browser.JSNexus
import pl.setblack.lsa.events.Node
import pl.setblack.lsa.resources.{JSResources, UniResource}
import pl.setblack.lsa.security.SignedCertificate
import pl.setblack.log.LogConfig
import upickle.default._

import scala.scalajs.js
import scala.util.Try

object Init extends js.JSApp {
  def main(): Unit = {
    import scala.concurrent.ExecutionContext.Implicits.global

    LogConfig.initConfig()

    val res = new JSResources()
    res.getResource("certificate.json").onSuccess {
      case res: Try[UniResource] => {
        println(s"mam ${res.get.asString}")
        val rootCertificate = read[SignedCertificate](res.get.asString)
        val jsbeeing = new JSNexus()
        val node: Node = jsbeeing.start()
       // node.registerDomain(FitbitDomainConfig.path, new FitbitDomain())
        initReactComponents

      }
      case _ => println("jakis bug")
    }


  }

  private def initReactComponents = {
    import japgolly.scalajs.react.vdom.prefix_<^._
    import org.scalajs.dom.document

    val initState: PresentationRun = new PresentationRun

    val InitPresentation = ReactComponentB[Unit]("InitButton")

    val MainComponent = ReactComponentB[Unit]("MainComponent")
      .initialState(initState)
      .backend(new Backends.Backend(_))
      .renderS((_, myState) => <.div("yupi", myState.toString))
      .build

    ReactDOM.render(MainComponent(), document.getElementById("react"))
  }


}
