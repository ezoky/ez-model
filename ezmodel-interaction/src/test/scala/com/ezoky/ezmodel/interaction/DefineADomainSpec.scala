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
    "the Modeller defines a new Domain" should {
      "sets it as the current Domain" in {

        Given("an empty modelling state")
        val initialModellingState = ModellingState.Empty

        When("the Modeller describes a Domain containing a Use Case with the DSL")
        val whatISay =
          Say {
            inDomain("Invoicing") asAn "Accountant" iWantTo ("invoice" a "Customer")
          }

        Then("the modelling state changes accordingly: Domain and Use Case are set as 'current'")
        val definedUseCase =
          UseCase(
            Actor("Accountant"),
            Goal(Action(Verb("invoice")), Some(ActionObject(NameGroup(Determinant.a, Name("Customer")))))
          )
        val definedDomain =
          Domain(
            Name("Invoicing"),
            useCases = UseCaseMap(
              definedUseCase
            )
          )

        val modellingState = Processor(initialModellingState).process(whatISay).state
        assert(modellingState.models.isEmpty)
        assert(modellingState.currentModel.isEmpty)
        assert(modellingState.currentDomain === Some(definedDomain))
        assert(modellingState.currentUseCase === Some(definedUseCase))
        assert(modellingState.currentEntity.isEmpty)

        When("the Modeller describes an Entity")
        val iDescribeAnEntity =
          Say {
            theEntity("Invoice") has one("number")
          }

        Then("the Entity is set as the current one and added to the current Domain")
        val definedEntity =
          Entity("Invoice").withAttribute("number")

        val modifiedDomain =
          definedDomain.withEntity(definedEntity)

        val modellingStateWithEntity = Processor(modellingState).process(iDescribeAnEntity).state
        assert(modellingStateWithEntity.models.isEmpty)
        assert(modellingStateWithEntity.currentModel.isEmpty)
        assert(modellingStateWithEntity.currentDomain === Some(modifiedDomain))
        assert(modellingStateWithEntity.currentUseCase === Some(definedUseCase))
        assert(modellingStateWithEntity.currentEntity === Some(definedEntity))


        When("the Modeller describes an other Entity")
        val iDescribeAnotherEntity =
          Say {
            theEntity("Invoiced Customer") has one("last invoicing date")
          }

        Then("the Entity is set as the current one and added to the current Domain")
        val definedEntity2 =
          Entity("Invoiced Customer").withAttribute("last invoicing date")

        val modifiedDomain2 =
          modifiedDomain.withEntity(definedEntity2)

        val modellingStateWithAnotherEntity = Processor(modellingStateWithEntity).process(iDescribeAnotherEntity).state
        assert(modellingStateWithAnotherEntity.models.isEmpty)
        assert(modellingStateWithAnotherEntity.currentModel.isEmpty)
        assert(modellingStateWithAnotherEntity.currentDomain === Some(modifiedDomain2))
        assert(modellingStateWithAnotherEntity.currentUseCase === Some(definedUseCase))
        assert(modellingStateWithAnotherEntity.currentEntity === Some(definedEntity2))

        When("the Modeller describes a Model containing another exiting Domain")
        val iDescribeAModel =
          Say {
            inModel("Finance Model") withDomain ("Cash Management")
          }

        Then("current Domain is added to the Model and it becomes current Model")
        val existingDomain = Domain("Cash Management")
        val definedModel =
          Model("Finance Model")
            .withDomain(existingDomain)
            .withDomain(modifiedDomain2)

        val modellingStateWithAModel = Processor(modellingStateWithAnotherEntity).process(iDescribeAModel).state
        assert(modellingStateWithAModel.ownsModel(definedModel))
        assert(modellingStateWithAModel.currentModel === Some(definedModel))
        assert(modellingStateWithAModel.currentDomain === Some(modifiedDomain2))
        assert(modellingStateWithAModel.currentUseCase === Some(definedUseCase))
        assert(modellingStateWithAModel.currentEntity === Some(definedEntity2))


        When("the Modeller works on an already existing Domain")
        val iRedefineAnExistingDomain =
          Say {
            inDomain("Invoicing") asAn "Accountant" iWantTo ("invoice" a "Month")
          }

        Then("new definition is merged with existing")
        val otherUseCase =
          UseCase(Actor("Accountant"), Goal("invoice", Some(a("Month"))))
        val mergedDomain =
          modifiedDomain2.withUseCase(otherUseCase)
        val modelWithMergedDomain =
          definedModel.mergeDomain(mergedDomain)

        val modellingStateWithMergedDomain =
          Processor(modellingStateWithAModel).process(iRedefineAnExistingDomain).state
        assert(modellingStateWithMergedDomain.models.owns(modelWithMergedDomain))
        assert(modellingStateWithMergedDomain.currentModel === Some(modelWithMergedDomain))
        assert(modellingStateWithMergedDomain.currentDomain === Some(mergedDomain))
        assert(modellingStateWithMergedDomain.currentUseCase === Some(otherUseCase))
        assert(modellingStateWithMergedDomain.currentEntity.isEmpty)
      }
    }
  }
}