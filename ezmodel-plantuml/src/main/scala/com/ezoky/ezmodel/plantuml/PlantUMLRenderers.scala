package com.ezoky.ezmodel.plantuml

/**
  * @author gweinbach on 05/04/2021
  * @since 0.2.0
  */
trait PlantUMLRenderers {

  trait PlantUMLRenderer[U <: PlantUML] {

    def render(plantUML: U,
               renderingContext: PlantUMLRenderingContext): String

  }

  object PlantUMLRenderer {

    def apply[U <: PlantUML : PlantUMLRenderer]: PlantUMLRenderer[U] =
      implicitly[PlantUMLRenderer[U]]

    def define[U <: PlantUML](renderer: (U, PlantUMLRenderingContext) => String): PlantUMLRenderer[U] = {
      (plantUML, renderingContext) => renderer(plantUML, renderingContext)
    }
  }


  implicit class PlantUMLHelper[U <: PlantUML](plantUML: U) {

    def render(renderingContext: PlantUMLRenderingContext = PlantUMLRenderingContext(0))
              (implicit
               renderer: PlantUMLRenderer[U]): String =
      PlantUMLRenderer[U].render(plantUML, renderingContext)
  }

  implicit class PlantUMLSetHelper[U <: PlantUML](plantUMLSet: Set[U]) {

    def render(renderingContext: PlantUMLRenderingContext)
              (implicit
               renderer: PlantUMLRenderer[U]): String =
      if (plantUMLSet.isEmpty) {
        ""
      }
      else {
        plantUMLSet.map(_.render(renderingContext)).mkString("", "\n", "\n")
      }
  }


  private val TabSize = 2

  case class PlantUMLRenderingContext(tabs: Int) {
    lazy val increment: PlantUMLRenderingContext = copy(tabs = tabs + 1)
    lazy val decrement: PlantUMLRenderingContext = copy(tabs = tabs - 1)
    val spaces: String = " " * tabs * TabSize
  }

  private def renderPlantUMLContainerContent[U <: PlantUMLContainer](container: U,
                                                                     renderingContext: PlantUMLRenderingContext): String =
    s"""${container.packages.render(
      renderingContext)}${container.actors.render(
      renderingContext)}${container.useCases.render(
      renderingContext)
    }"""

  private def renderPlantUMLNode[U <: PlantUMLNode](nodeType: String,
                                                    node: U,
                                                    renderingContext: PlantUMLRenderingContext): String =
    s"""${renderingContext.spaces}$nodeType "${node.name}""""


  implicit val plantUMLActorRenderer: PlantUMLRenderer[PlantUMLActor] =

    PlantUMLRenderer.define {
      (umlActor, renderingContext) =>
        s"""${renderPlantUMLNode("actor", umlActor, renderingContext)}""".stripMargin
    }

  implicit val plantUMLUseCaseRenderer: PlantUMLRenderer[PlantUMLUseCase] =

    PlantUMLRenderer.define {
      (umlUseCase, renderingContext) =>
        s"""${renderPlantUMLNode("usecase", umlUseCase, renderingContext)}""".stripMargin
    }

  implicit val plantUMLPackageRenderer: PlantUMLRenderer[PlantUMLPackage] =
    PlantUMLRenderer.define {
      (umlPackage, renderingContext) =>

        s"""${renderPlantUMLNode("package", umlPackage, renderingContext)} {
           |${renderPlantUMLContainerContent(umlPackage, renderingContext.increment)}${renderingContext.spaces}}""".stripMargin
    }

  implicit def plantUMLDiagramRenderer: PlantUMLRenderer[PlantUMLDiagram] =
    PlantUMLRenderer.define {
      (umlDiagram, renderingContext) =>

        s"""@startuml
           |${renderPlantUMLContainerContent(umlDiagram, renderingContext)}@enduml
           |""".stripMargin
    }
}