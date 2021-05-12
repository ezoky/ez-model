package com.ezoky.ezmodel.plantumlview

import com.ezoky.architecture.zioapi.ZIOAPI
import org.scalatest.funsuite.AnyFunSuite
import com.ezoky.ezmodel.core.Models._
import com.ezoky.ezmodel.core.StandardTypeClasses._
import com.ezoky.ezmodel.plantuml.RenderModelInPlantUML
import com.ezoky.ezplantuml.PlantUMLWrapper
import sttp.client3.SttpBackend
import sttp.client3.asynchttpclient.zio.AsyncHttpClientZioBackend
import sttp.model.StatusCode
import zio.internal.Platform
import zio.{Exit, Runtime, Task, ZIO}

/**
  * @author gweinbach on 21/04/2021
  * @since 0.2.0
  */
class ViewModelInPlantUMLTest extends AnyFunSuite {

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

    val serverConfig = ViewModelInPlantUMLServerConfig("http://localhost:7071")

    val backendStub =
      AsyncHttpClientZioBackend.stub
      .whenRequestMatches(_.uri == serverConfig.sendSVGEndPoint)
      .thenRespond("", StatusCode.Ok)
      .whenRequestMatches(_.uri != serverConfig.sendSVGEndPoint)
      .thenRespondServerError()

    val zioImpl = new ZIOImpl(backendStub)

    val result =
      Runtime(serverConfig, Platform.default)
        .unsafeRunSync(zioImpl.ZIOViewModelInPlantUML.viewModelInPlantUML(model))

    assert(result == Exit.Success(()))
  }

}


class ZIOImpl(backend: SttpBackend[Task, Any]) {

  val Api = new ZIOAPI[ViewModelInPlantUMLConfig] {}

  import Api._

  implicit val ZIOPlantUMLService: PlantUMLWrapper[QueryProducing] =
    new PlantUMLWrapper[QueryProducing]

  implicit val ZIORenderModelInPlantUML: RenderModelInPlantUML[QueryProducing] =
    new RenderModelInPlantUML[QueryProducing](ZIOPlantUMLService)

  implicit val ZIOViewModelInPlantUML: ViewModelInPlantUML[Effect] =
    new ViewModelInPlantUML[Effect](ZIORenderModelInPlantUML) {

      override def configReader[A](configure: ViewModelInPlantUMLConfig => A): Effect[A] =
        ZIO.access(configure)

      override def useBackend(action: SttpBackend[Effect, Any] => Effect[Unit]): Effect[Unit] =
        action(backend.asInstanceOf[SttpBackend[Effect, Any]])
    }

}