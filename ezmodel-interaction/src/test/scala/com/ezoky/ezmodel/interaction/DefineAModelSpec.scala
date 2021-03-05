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

  "the StateProcessor" when {
    "the Modeller defines a new Model" should {
      "sets it as the current Model" in {

        Given("a modelling state")
        val initialModellingState = ModellingState.Empty

        When("the Modeller describes a Model with the DSL")
        val whatISay =
          Say {
            inModel("Existing Information System")
          }

        Then("the modelling state changes accordingly")
        val definedModel = Model(Name("Existing Information System"))

        val modellingState = StateProcessor(initialModellingState).process(whatISay).state
        assert(modellingState.ownsModel(definedModel))
        assert(modellingState.currentModel === Some(definedModel))
        assert(modellingState.currentDomain.isEmpty)
        assert(modellingState.currentUseCase.isEmpty)
        assert(modellingState.currentEntity.isEmpty)


        When("the Modeller describes an Entity")
        val iDescribeAnEntity =
          Say {
            theEntity("Account") has oneAndOnlyOne ("number")
          }

        Then("the Entity is selected but not added to current Model because there is no Domain")
        val definedEntity =
          Entity("Account")
            .withAttribute("number", single, mandatory = true)

        val modellingStateWithEntity = StateProcessor(modellingState).process(iDescribeAnEntity).state
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
          definedDomain.withEntity(definedEntity)
        val modelWithDomain =
          definedModel.withDomain(domainWithEntity)

        val modellingStateWithDomain = StateProcessor(modellingStateWithEntity).process(iDescribeADomain).state
        assert(modellingStateWithDomain.ownsModel(definedModel))
        assert(modellingStateWithDomain.currentModel === Some(modelWithDomain))
        assert(modellingStateWithDomain.currentDomain === Some(domainWithEntity))
        assert(modellingStateWithDomain.currentUseCase.isEmpty)
        assert(modellingStateWithDomain.currentEntity === Some(definedEntity))


        When("the Modeller describes another Model including a Domain")
        val iDescribeAnotherModel =
          Say {
            // same domain name but domain is different since the model is different
            inModel ("Target Information System") withDomain "Accounting"
          }

        Then("the Model is added to the owned Models and the Model and the Domain are selected")
        val aDomainInTheNewModel = Domain("Accounting")
        val otherModel =
          Model("Target Information System")
            .withDomain(aDomainInTheNewModel)

        val modellingStateWithAnotherModel = StateProcessor(modellingStateWithDomain).process(iDescribeAnotherModel).state
        assert(modellingStateWithAnotherModel.ownsModel(modelWithDomain))
        assert(modellingStateWithAnotherModel.ownsModel(otherModel))
        assert(modellingStateWithAnotherModel.currentModel === Some(otherModel))
        assert(modellingStateWithAnotherModel.currentDomain === Some(aDomainInTheNewModel))
        assert(modellingStateWithAnotherModel.currentUseCase.isEmpty)
        assert(modellingStateWithAnotherModel.currentEntity.isEmpty)


        When("the Modeller works on an another already existing Model and adds a Domain")
        val iRedefineAnExistingModel =
          Say {
            inModel("Existing Information System") withDomain "Cash Management"
          }

        Then("new definition is merged with existing and the Model and the Domain are selected")
        val otherDomain =
          Domain("Cash Management")
        val mergedModel =
          modelWithDomain.withDomain(otherDomain)

        val modellingStateWithMergedModel =
          StateProcessor(modellingStateWithAnotherModel).process(iRedefineAnExistingModel).state
        assert(modellingStateWithMergedModel.models.owns(mergedModel))
        assert(modellingStateWithMergedModel.models.owns(modelWithDomain))
        assert(modellingStateWithMergedModel.currentModel === Some(mergedModel))
        assert(modellingStateWithMergedModel.currentDomain === Some(otherDomain))
        assert(modellingStateWithMergedModel.currentUseCase.isEmpty)
        assert(modellingStateWithMergedModel.currentEntity.isEmpty)


        When("the Modeller comes back to previous Domain")
        val iWorkAgainOnPrevious =
          Say {
            inDomain("Accounting") withEntity "Account"
          }

        Then("previous Domain and Entity are selected")

        val modellingStateBackToFirstDomain =
          StateProcessor(modellingStateWithMergedModel).process(iWorkAgainOnPrevious).state
        assert(modellingStateBackToFirstDomain.models.owns(mergedModel))
        assert(modellingStateBackToFirstDomain.models.owns(modelWithDomain))
        assert(modellingStateBackToFirstDomain.currentModel === Some(mergedModel))
        assert(modellingStateBackToFirstDomain.currentDomain === Some(domainWithEntity))
        assert(modellingStateBackToFirstDomain.currentUseCase.isEmpty)
        assert(modellingStateBackToFirstDomain.currentEntity === Some(definedEntity))
      }
    }
  }
}