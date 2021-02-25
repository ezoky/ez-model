package com.ezoky.ezmodel.interaction

import com.ezoky.ezmodel.core.Models._
import com.ezoky.ezmodel.interaction.dsl.DSL._
import org.scalatest.GivenWhenThen
import org.scalatest.wordspec.AnyWordSpec

/**
  * @author gweinbach on 04/02/2021
  * @since 0.2.0
  */
class DefineAUseCaseSpec
  extends AnyWordSpec
   with GivenWhenThen {

  import Modelling._

  "the Processor" when {
    "the Modeller defines a new Use Case" should {
      "sets it as the current Use Case" in {

        Given("a modelling state")
        val initialModellingState = ModellingState.Empty

        When("the Modeller describes a Use Case with the DSL")
        val whatISay =
          Say {
            asAn ("Accountant") iWantTo ("invoice" a "Month") provided ("Production" is "Done") resultingIn ("Current Month" is "Invoiced")
          }

        Then("the modelling state changes accordingly")
        val definedUseCase =
          UseCase(
            Actor(Name("Accountant")),
            Goal(Action(Verb("invoice")), Some(ActionObject(NameGroup(Determinant.a, Name("Month"))))),
            Constraints(
              Pre -> EntityState(Entity(Name("Production")), StateName(Qualifier("Done"))),
              Post -> EntityState(Entity(Name("Current Month")), StateName(Qualifier("Invoiced")))
            )
          )

        val modellingState = Processor(initialModellingState).process(whatISay).state
        assert(modellingState.models.isEmpty)
        assert(modellingState.currentModel.isEmpty)
        assert(modellingState.currentDomain.isEmpty)
        assert(modellingState.currentUseCase === Some(definedUseCase))
        assert(modellingState.currentEntity.isEmpty)


        When("the Modeller defines a Domain that already contains another UseCase")
        val iDefineADomain =
          Say {
            inDomain("Invoicing") asAn("Accountant") iWantTo("close" an "Accounting Month")
          }

        Then("current UseCase is kept as current and added to the Domain which is set to 'current'")
        val secondUseCase =
          UseCase("Accountant", Goal("close", Some(ActionObject(NameGroup(Determinant.an, "Accounting Month")))))
        val definedDomain =
          Domain("Invoicing")
            .withUseCase(secondUseCase)
            .withUseCase(definedUseCase)

        val modellingStateWithDomain = Processor(modellingState).process(iDefineADomain).state
        assert(modellingStateWithDomain.models.isEmpty)
        assert(modellingStateWithDomain.currentModel.isEmpty)
        assert(modellingStateWithDomain.currentDomain === Some(definedDomain))
        assert(modellingStateWithDomain.currentUseCase === Some(definedUseCase))
        assert(modellingStateWithDomain.currentEntity.isEmpty)


        When("the Modeller works on an already existing useCase")
        val iRedefineAnExistingUseCase =
          Say {
            theUseCase ("Accountant", "invoice" a "Month") resultsIn("Next Month" is "started")
          }

        Then("new definition is merged with existing")
        val mergedUseCase =
          definedUseCase.withPostCondition("Next Month" is "started")
        val domainWithMergedUseCase =
          definedDomain.withUseCase(mergedUseCase)

        val modellingStateWithMergedUseCase =
          Processor(modellingStateWithDomain).process(iRedefineAnExistingUseCase).state
        assert(modellingStateWithMergedUseCase.models.isEmpty)
        assert(modellingStateWithMergedUseCase.currentModel.isEmpty)
        assert(modellingStateWithMergedUseCase.currentDomain === Some(domainWithMergedUseCase))
        assert(modellingStateWithMergedUseCase.currentUseCase === Some(mergedUseCase))
        assert(modellingStateWithMergedUseCase.currentEntity.isEmpty)
      }
    }
  }
}
