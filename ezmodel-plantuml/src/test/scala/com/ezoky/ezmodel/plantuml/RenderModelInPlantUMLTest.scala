package com.ezoky.ezmodel.plantuml

import com.ezoky.architecture.zioapi.ZIOAPI
import com.ezoky.ezmodel.core.Models._
import com.ezoky.ezmodel.core.StandardTypeClasses._
import com.ezoky.ezplantuml.PlantUMLWrapper
import org.scalatest.funsuite.AnyFunSuite
import zio.Runtime

/**
  * @author gweinbach on 07/04/2021
  * @since 0.2.0
  */
class RenderModelInPlantUMLTest
  extends AnyFunSuite {

  test("Generate SVG from Model") {

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

    val domain1 =
      Domain(Name("Invoicing"))
        .withUseCase(useCase1)
        .withUseCase(useCase2)

    val model =
      Model(Name("Test"))
        .withDomain(domain1)

    val result =
      Runtime.default.unsafeRun(ZIOImpl.ZIORenderModelInPlantUML.generateSVG(model))

    println(result)
    assert(result.size == 1)

  }
}


object ZIOImpl {

  val api = new ZIOAPI[Any] {}

  import api._

  implicit val ZIOPlantUMLService: PlantUMLWrapper[api.QueryProducing] =
    new PlantUMLWrapper[api.QueryProducing]

  implicit val ZIORenderModelInPlantUML: RenderModelInPlantUML[api.QueryProducing] =
    new RenderModelInPlantUML[api.QueryProducing](ZIOPlantUMLService)
}