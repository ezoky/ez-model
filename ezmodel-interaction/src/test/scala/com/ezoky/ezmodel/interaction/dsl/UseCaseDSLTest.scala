package com.ezoky.ezmodel.interaction.dsl

import org.scalatest.funsuite.AnyFunSuite

/**
  * @author gweinbach on 17/02/2021
  * @since 0.2.0
  */
class UseCaseDSLTest
extends AnyFunSuite {

  import com.ezoky.ezmodel.core.Models._

  object Say extends AtomsDSL with UseCaseDSL

  import Say._

  test("UseCases can be described in a fluent way") {

    val actor = Actor(Name("Accountant"))
    val goal = Goal(Action(Verb("invoice")), Some(ActionObject(NameGroup(Determinant.a, "customer"))))
    val useCase = UseCase(actor, goal)

    val fluentActor = Say asAn "Accountant"
    assert(fluentActor == actor)

    val fluentUseCase = Say asAn "Accountant" iWantTo ("invoice" a "customer")
    assert(fluentUseCase == useCase)
  }
}
