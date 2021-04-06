
import com.ezoky.ezmodel.core.Models._
import com.ezoky.ezmodel.core.interactions._
import com.ezoky.ezmodel.interaction.Modelling._
import com.ezoky.ezmodel.interaction.dsl.DSL._
import shapeless._

import java.time.Month

val previous = Determinant("previous")

type ReferenceTo[T] = Id[T]

case class InvoicedMonth(month: Month,
                         invoicableContracts: List[InvoicableContract],
                         nonInvoicableMissions: List[NonInvoicableMission]) {

  def invoiceChosenContracts: Unit = {}
}

case class InvoicableContract(contract: ReferenceTo[ActiveContract],
                              chosen: Boolean)

case class NonInvoicableMission(mission: ReferenceTo[ActiveMission])

case class ActiveContract(missions: List[ActiveMission])

case class ActiveMission(consultant: ReferenceTo[ActiveConsultant])

case class ActiveConsultant()

val invoicingOfPreviousMonth = FormOf[InvoicedMonth](
  "Invoicing of previous Month"
).withTitle(controller => s"Invoicing ${controller.currentObject.fold("unknown month")(_.month.name)}")

val useCase = Say(
  inDomain("Invoicing") asAn ("Accountant") inOrderTo("invoice", previous, "Month") iWantTo("choose", "Invoicable Contracts"),
  theInteraction("choose", "Invoicable Contracts") uses ("Invoicing of previous Month")
)

