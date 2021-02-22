package com.ezoky.ezmodel.interaction

import com.ezoky.ezmodel.core.Models._
import com.ezoky.ezmodel.interaction.dsl.DSL._
import org.scalatest.GivenWhenThen
import org.scalatest.wordspec.AnyWordSpec

/**
  * @author gweinbach on 22/02/2021
  * @since 0.2.0
  */
class DefineADomainSpec
  extends AnyWordSpec
    with GivenWhenThen {

  import Modelling._

  "the Processor" when {
    "the Modeller defines a Domain" should {
      "sets it as the current Domain" in {

        Given("a modelling state")
        val initialModellingState = ModellingState.Empty

        When("the Modeller describes a Domain with the DSL")
        val whatISay =
          Say {
            inDomain("Invoicing")
          }

        Then("the modelling state changes accordingly")
        val definedDomain =
          Domain(
            Name("Invoicing")
          )

        val modellingState = Processor(initialModellingState).process(whatISay).state
        assert(modellingState.currentDomain === Some(definedDomain))
      }
    }
  }
}