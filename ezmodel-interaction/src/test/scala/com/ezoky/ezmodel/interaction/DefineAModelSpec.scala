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
    "the Modeller defines a new Model" should {
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
        assert(modellingState.ownsModel(definedModel))
        assert(modellingState.currentModel === Some(definedModel))
        assert(modellingState.currentDomain.isEmpty)
        assert(modellingState.currentUseCase.isEmpty)
        assert(modellingState.currentEntity.isEmpty)


        When("the Modeller describes an Entity")
        val iDescribeAnEntity =
          Say {
            theEntity("Account")
          }

        Then("the Entity is selected but not added to current Model because ther is no Domain")
        val definedEntity =
          Entity("Account")

        val modellingStateWithEntity = Processor(modellingState).process(iDescribeAnEntity).state
        assert(modellingStateWithEntity.ownsModel(definedModel))
        assert(modellingStateWithEntity.currentModel === Some(definedModel))
        assert(modellingStateWithEntity.currentDomain.isEmpty)
        assert(modellingStateWithEntity.currentUseCase.isEmpty)
        assert(modellingStateWithEntity.currentEntity === Some(definedEntity))


        When("the Modeller describes a Domain")
        val iDescribeADomain =
          Say {
            inDomain("Accounting")
          }

        Then("the Domain is selected and added to the current Model")
        val definedDomain =
          Domain("Accounting")
        val domainWithEntity =
          definedDomain.withEntity(Entity("Account"))
        val modelWithDomain =
          definedModel.withDomain(domainWithEntity)

        val modellingStateWithDomain = Processor(modellingStateWithEntity).process(iDescribeADomain).state
        assert(modellingStateWithDomain.ownsModel(definedModel))
        assert(modellingStateWithDomain.currentModel === Some(modelWithDomain))
        assert(modellingStateWithDomain.currentDomain === Some(domainWithEntity))
        assert(modellingStateWithDomain.currentUseCase.isEmpty)
        assert(modellingStateWithEntity.currentEntity === Some(definedEntity))
      }
    }
  }
}