package com.ezoky.ezmodel.interaction.dsl

import org.scalatest.funsuite.AnyFunSuite

/**
  * @author gweinbach on 17/02/2021
  * @since 0.2.0
  */
class UseCaseDSLTest
extends AnyFunSuite {

  import com.ezoky.ezmodel.core.Models._

  object Say extends AtomsDSL with UseCaseDSL with EntityDSL

  import Say._

  test("UseCases can be described in a fluent way") {

    val actor = Actor(Name("Accountant"))
    val goal = Goal(Action(Verb("invoice")), Some(ActionObject(NameGroup(Determinant.a, "customer"))))
    val interaction = Interaction(Action(Verb("choose")), Some(ActionObject(NameGroup(Determinant.some, "contracts"))))

    val useCase = UseCase(actor, goal)
    val useCaseWithInteraction = UseCase(actor, goal, Some(interaction))
    val useCaseWithInteractionAndConstraints =
      UseCase(
        actor,
        goal,
        Some(interaction),
        Constraints(
          Pre -> EntityState(Entity(Name("month")), StateName(Qualifier("started"))),
          Post -> EntityState(Entity(Name("customer")), StateName(Qualifier("invoiced")))
        )
      )

    val fluentActor = Say asAn "Accountant"
    assert(fluentActor === actor)

    val fluentUseCase = Say asAn "Accountant" inOrderTo ("invoice" a "customer")
    assert(fluentUseCase === useCase)

    val fluentUseCaseWithInteraction =
      Say asAn "Accountant" inOrderTo ("invoice" a "customer") iWantTo ("choose" some "contracts")
    assert(fluentUseCaseWithInteraction === useCaseWithInteraction)

    val fluentUseCaseWithInteractionAndConstraints =
      Say asAn "Accountant" inOrderTo ("invoice" a "customer") iWantTo ("choose" some "contracts") provided ("month" is "started") resultingIn ("customer" is "invoiced")
    assert(fluentUseCaseWithInteractionAndConstraints === useCaseWithInteractionAndConstraints)
  }
}
