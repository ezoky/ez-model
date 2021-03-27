package com.ezoky.ezmodel.interaction

import com.ezoky.ezmodel.core.Models
import com.ezoky.ezmodel.core.Models._
import com.ezoky.ezmodel.interaction.dsl.DSL._
import org.scalatest.GivenWhenThen
import org.scalatest.wordspec.AnyWordSpec

import java.time.Month
import java.util.Date

/**
  * @author gweinbach on 26/03/2021
  * @since 0.2.0
  */
class DescribeAnInteractionSpec
  extends AnyWordSpec
    with GivenWhenThen {

  import Modelling._

  case class InvoicedMonth(month: Month)

  case class Invoice(number: String, date: Date)

  case class Customer(name: String)

  "the StateProcessor" when {
    "the Modeller describes an Interaction" should {
      "sets it as the current Interaction Descriptor" in {

        Given("a modelling state")
        val initialModellingState = ModellingState.Empty

        When("the Modeller describes an Interaction with the DSL")
        val whatISay =
          Say {
            FormOf[InvoicedMonth]("Invoicing") withTitle (c => s"Invoicing on ${c.currentObject.fold("unknown date")(_.month.toString)}")
          }

        Then("the modelling state changes accordingly")
        val describedInteraction =
          FormOf[InvoicedMonth]("Invoicing",
            InteractionRulesBag.Empty +
            ControllerTitle[InvoicedMonth, SingleInstanceController[InvoicedMonth]]("Invoicing", c => s"Invoicing on ${c.currentObject.fold("unknown date")(_.month.toString)}")
          )

        val modellingState = StateProcessor(initialModellingState).process(whatISay).state
        assert(modellingState.models.isEmpty)
        assert(modellingState.currentModel.isEmpty)
        assert(modellingState.currentDomain.isEmpty)
        assert(modellingState.currentUseCase.isEmpty)
        assert(modellingState.currentInteraction.isEmpty)
        assert(modellingState.currentEntity.isEmpty)
        // testing Rule equality is difficult as it includes Lambdas
        assert(modellingState.currentInteractionDescriptor.get.name === describedInteraction.name)
        assert(modellingState.currentInteractionDescriptor.get.rules.size === describedInteraction.rules.size)
      }
    }
  }
}
