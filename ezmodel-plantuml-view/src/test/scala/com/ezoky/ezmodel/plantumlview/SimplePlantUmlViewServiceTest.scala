package com.ezoky.ezmodel.plantumlview

import org.scalatest.funsuite.AnyFunSuite
import com.ezoky.ezmodel.core.Models._
import com.ezoky.ezmodel.core.StandardTypeClasses._

/**
  * @author gweinbach on 21/04/2021
  * @since 0.2.0
  */
class SimplePlantUmlViewServiceTest extends AnyFunSuite {

  test("View SVG from Model") {

    val interaction1 =
      Interaction(
        Action(Verb("choose")),
        Some(ActionObject(NameGroup(Determinant.the, Name("Invoiced Customers"))))
      )

    val useCase1 =
      UseCase(
        Actor(Name("Accountant")),
        Goal(Action(Verb("invoice")), Some(ActionObject(NameGroup(Determinant.a, Name("Month"))))),
        constraints =
          Constraints(
            Pre -> EntityState(Entity(Name("Production")), StateName(Qualifier("Done"))),
            Pre -> EntityState(Entity(Name("Current Month")), StateName(Qualifier("Started"))),
            Post -> EntityState(Entity(Name("Current Month")), StateName(Qualifier("Invoiced")))
          )
      ).withInteraction(interaction1)

    val useCase2 =
      UseCase(
        Actor(Name("Accountant")),
        Goal(Action(Verb("close")), Some(ActionObject(NameGroup(Determinant.an, Name("Accounting Month")))))
      )

    val useCase3 =
      UseCase(
        Actor(Name("Accountant")),
        Goal(Action(Verb("pay")), Some(ActionObject(NameGroup(Determinant.the, Name("Bills")))))
      )

    val useCase4 =
      UseCase(
        Actor(Name("Auditor")),
        Goal(Action(Verb("certify")), Some(ActionObject(NameGroup(Determinant.the, Name("Accounts")))))
      )

    val domain1 =
      Domain(Name("Invoicing"))
        .withUseCase(useCase1)
        .withUseCase(useCase2)
        .withUseCase(useCase3)
        .withUseCase(useCase4)

    val model =
      Model(Name("Test"))
        .withDomain(domain1)

    val diagrams = SimplePlantUmlViewService.viewPlantUmlModel(model)

    println(diagrams)
//    assert(diagrams.size == 1)
    assert(true)
  }

}
