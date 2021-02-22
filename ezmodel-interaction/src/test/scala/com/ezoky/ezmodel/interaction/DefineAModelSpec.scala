package com.ezoky.ezmodel.interaction

import com.ezoky.ezmodel.core.Models._
import com.ezoky.ezmodel.interaction.dsl.DSL._
import org.scalatest.GivenWhenThen
import org.scalatest.wordspec.AnyWordSpec

/**
  * @author gweinbach on 22/02/2021
  * @since 0.2.0
  */
class DefineAModelSpec
  extends AnyWordSpec
    with GivenWhenThen {

  import Modelling._

  "the Processor" when {
    "the Modeller defines a Model" should {
      "sets it as the current Model" in {

        Given("a modelling state")
        val initialModellingState = ModellingState.Empty

        When("the Modeller describes a Model with the DSL")
        val whatISay =
          Say {
            inModel("Software Project")
          }

        Then("the modelling state changes accordingly")
        val definedModel = Model(Name("Software Project"))

        val modellingState = Processor(initialModellingState).process(whatISay).state
        assert(modellingState.currentModel === Some(definedModel))
      }
    }
  }
}