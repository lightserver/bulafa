package pl.setblack.bulafa.domain.run



import java.util.UUID

import pl.setblack.bulafa.domain.run.state.PresentationRun
import pl.setblack.lsa.events._



object PresentationRunDomain {
  sealed  trait PresentationRunEvent

  case class NextStep(currentPage :Int, currentStep : Int) extends PresentationRunEvent




  implicit object PresentationRunEventConverter  extends EventConverter[PresentationRunEvent]{
     import upickle.default._
    override def readEvent(str: String): PresentationRunEvent = read[PresentationRunEvent](str)

    override def writeEvent(e: PresentationRunEvent): String = write(e)
  }

  class PresentationRunDomain(val presentationId : UUID)
    extends Domain[PresentationRun, PresentationRunEvent]( new PresentationRun(), Seq("presentation", presentationId.toString)) {
    override protected def processDomain(state: PresentationRun, event: PresentationRunEvent, eventContext: EventContext): Response = {

      DefaultResponse
    }
  }

}
