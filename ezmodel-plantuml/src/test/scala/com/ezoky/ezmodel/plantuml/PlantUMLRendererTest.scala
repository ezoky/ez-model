package com.ezoky.ezmodel.plantuml

import org.scalatest.funsuite.AnyFunSuite

/**
  * @author gweinbach on 05/04/2021
  * @since 0.2.0
  */
class PlantUMLRendererTest
  extends AnyFunSuite {

  test("rendering a diagram") {

    val diagram = PlantUMLDiagram(
      packages = Set(
        PlantUMLPackage(
          "package 1",
          packages = Set(
            PlantUMLPackage(
              "package 1.1",
              actors = Set(
                PlantUMLActor("Actor 1")
              ),
              useCases = Set(
                PlantUMLUseCase("Use case 1")
              )
            )
          ),
          actors = Set(PlantUMLActor("Actor 2"))
        ),
        PlantUMLPackage("package 2")
      ),
      actors = Set(
        PlantUMLActor("Actor 1"),
        PlantUMLActor("Actor 2"),
        PlantUMLActor("Actor 1"),
        PlantUMLActor("Actor 3")
      ),
      useCases = Set(
        PlantUMLUseCase("Use case 1"),
        PlantUMLUseCase("Use case 2")
      )
    )

    val expectedRendering =
      s"""@startuml
         |package "package 1" {
         |  package "package 1.1" {
         |    actor "Actor 1"
         |    usecase "Use case 1"
         |  }
         |  actor "Actor 2"
         |}
         |package "package 2" {
         |}
         |actor "Actor 1"
         |actor "Actor 2"
         |actor "Actor 3"
         |usecase "Use case 1"
         |usecase "Use case 2"
         |@enduml
         |""".stripMargin

    assert(diagram.render() === expectedRendering)
  }
}
