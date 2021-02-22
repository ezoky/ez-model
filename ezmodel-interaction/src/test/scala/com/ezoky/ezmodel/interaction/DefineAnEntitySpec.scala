package com.ezoky.ezmodel.interaction

import com.ezoky.ezmodel.core.Models._
import com.ezoky.ezmodel.interaction.dsl.DSL._
import org.scalatest.GivenWhenThen
import org.scalatest.wordspec.AnyWordSpec

/**
  * @author gweinbach on 22/02/2021
  * @since 0.2.0
  */
class DefineAnEntitySpec
  extends AnyWordSpec
    with GivenWhenThen {

  import Modelling._

  "the Processor" when {
    "the Modeller defines an Entity" should {
      "define it as the current Entity" in {

        Given("a modelling state")
        val initialModellingState = ModellingState.Empty

        When("the Modeller describes an Entity with the DSL")
        val whatISay =
          Say {
            theEntity("Invoice") hasA ("number") references one("Customer") as "invoiced" aggregates atLeastOne("Invoice Line") as "lines"
          }

        Then("the modelling state changes accordingly")
        val definedEntity = {
          Entity(
            Name("Invoice"),
            attributes = Map(Name("number") -> Attribute(Name("number"), single, mandatory = false)),
            aggregated = Map(Name("lines") -> Aggregate(Name("lines"), Entity("Invoice Line"), multiple, mandatory = true)),
            referenced = Map(Name("invoiced") -> Reference(Name("invoiced"), Entity("Customer"), single, mandatory = false))
          )
        }

        val modellingState = Processor(initialModellingState).process(whatISay).state
        assert(modellingState.currentEntity === Some(definedEntity))
      }
    }
  }
}
