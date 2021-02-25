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
    "the Modeller defines a new Entity" should {
      "sets it as the current Entity" in {

        Given("an empty modelling state")
        val initialModellingState = ModellingState.Empty

        When("the Modeller describes an Entity with the DSL")
        val whatISay =
          Say {
            theEntity("Invoice") hasA ("number") references one("Customer") as "invoiced" aggregates atLeastOne("Invoice Line") as "lines"
          }

        Then("the modelling state changes accordingly: Entity is set as 'current'")
        val definedEntity = {
          Entity(       
            Name("Invoice"),
            attributes = AttributeMap(Attribute(Name("number"), single, mandatory = false)),
            aggregated = AggregateMap(Aggregate(Name("lines"), Entity("Invoice Line"), multiple, mandatory = true)),
            referenced = ReferenceMap(Reference(Name("invoiced"), Entity("Customer"), single, mandatory = false))
          )
        }

        val modellingState = Processor(initialModellingState).process(whatISay).state
        assert(modellingState.models.isEmpty)
        assert(modellingState.currentModel.isEmpty)
        assert(modellingState.currentDomain.isEmpty)
        assert(modellingState.currentUseCase.isEmpty)
        assert(modellingState.currentEntity === Some(definedEntity))


        When("the Modeller defines a Domain that already contains another Entity")
        val iDefineADomain =
          Say {
            inDomain("Invoicing") theEntity("Accounting Month")
          }

        Then("Entity is added to the Domain which is set to 'current'")
        val definedEntity2 = Entity("Accounting Month")
        val definedDomain =
          Domain("Invoicing")
            .withEntity(definedEntity2)
            .withEntity(definedEntity)

        val modellingStateWithDomain = Processor(modellingState).process(iDefineADomain).state
        assert(modellingStateWithDomain.models.isEmpty)
        assert(modellingStateWithDomain.currentModel.isEmpty)
        assert(modellingStateWithDomain.currentDomain === Some(definedDomain))
        assert(modellingStateWithDomain.currentUseCase.isEmpty)
        assert(modellingStateWithDomain.currentEntity === Some(definedEntity))


        When("the Modeller works on an already existing Entity")
        val iRedefineAnExistingEntity =
          Say {
            theEntity("Invoice") has oneAndOnlyOne ("status")
          }

        Then("new definition is merged with existing")
        val mergedEntity =
          definedEntity.withAttribute("status", single, true)
        val domainWithMergedEntity =
          definedDomain.withEntity(mergedEntity)

        val modellingStateWithMergedEntity =
          Processor(modellingStateWithDomain).process(iRedefineAnExistingEntity).state
        assert(modellingStateWithMergedEntity.models.isEmpty)
        assert(modellingStateWithMergedEntity.currentModel.isEmpty)
        assert(modellingStateWithMergedEntity.currentDomain === Some(domainWithMergedEntity))
        assert(modellingStateWithMergedEntity.currentUseCase.isEmpty)
        assert(modellingStateWithMergedEntity.currentEntity === Some(mergedEntity))
      }
    }
  }
}
