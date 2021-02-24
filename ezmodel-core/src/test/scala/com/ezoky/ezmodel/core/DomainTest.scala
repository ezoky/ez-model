package com.ezoky.ezmodel.core

import org.scalatest.funsuite.AnyFunSuite

/**
  * @author gweinbach
  */
class DomainTest
  extends AnyFunSuite
    with StandardDomain
    with StandardUseCase
    with StandardEntity {


  test("Domain elaboration") {

    val dom =
      Domain(Name("Driving"))
        .withUseCase(
          UseCase(Actor(Name("Driver")), Goal(Action(Verb("change")), Some(ActionObject(NameGroup(Determinant.the, Name("Gear"))))))
        )
        .withUseCase(
          UseCase(Actor(Name("Driver")), Goal(Action(Verb("brake"))))
        )
        .withEntity(
          Entity(Name("Gear"))
        )

    assert(dom.useCases.size === 2)
    assert(dom.entities.size === 1)
  }
}
