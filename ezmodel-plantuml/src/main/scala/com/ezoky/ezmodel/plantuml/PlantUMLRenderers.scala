package com.ezoky.ezmodel.plantuml

import com.ezoky.ezmodel.plantuml.PlantUMLNode.PlantUMLNodeId

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

  implicit class PlantUMLNodeIdHelper(plantUMLNodeId: PlantUMLNodeId) {

    def render: String =
      s""""${plantUMLNodeId.id}""""
  }

  implicit class PlantUMLNodeTypeHelper(plantUMLNodeType: PlantUMLNodeType) {

    def render: String =
      plantUMLNodeType match {
        case PlantUMLNodeType.Package => "package"
        case PlantUMLNodeType.UseCase => "usecase"
        case PlantUMLNodeType.Actor => "actor"
        case PlantUMLNodeType.Abstract => "abstract"
        case PlantUMLNodeType.Annotation => "annotation"
        case PlantUMLNodeType.Circle => "circle"
        case PlantUMLNodeType.Class => "class"
        case PlantUMLNodeType.Diamond => "diamond"
        case PlantUMLNodeType.Entity => "entity"
        case PlantUMLNodeType.Enum => "enum"
        case PlantUMLNodeType.Interface => "interface"
      }
  }

  implicit class PlantUMLSharingHelper(plantUMLSharing: PlantUMLSharing) {

    def render: String =
      plantUMLSharing match {
        case PlantUMLSharing.None => ""
        case PlantUMLSharing.Aggregated => "o"
        case PlantUMLSharing.Composed => "*"
      }
  }

  implicit class PlantUMLTargetTypeHelper(plantUMLTargetType: PlantUMLTargetType) {

    def render: String =
      plantUMLTargetType match {
        case PlantUMLTargetType.None => ""
        case PlantUMLTargetType.Arrow => ">"
        case PlantUMLTargetType.Extension => "|>"
        case PlantUMLTargetType.Cube => "#"
        case PlantUMLTargetType.Cross => "x"
        case PlantUMLTargetType.DuckFoot => "}"
        case PlantUMLTargetType.CircleCross => "^"
      }
  }


  private val TabSize = 2

  case class PlantUMLRenderingContext(tabs: Int) {
    lazy val increment: PlantUMLRenderingContext = copy(tabs = tabs + 1)
    lazy val decrement: PlantUMLRenderingContext = copy(tabs = tabs - 1)
    val spaces: String = " " * tabs * TabSize
  }

  private def renderPlantUMLContainerContent[U <: PlantUMLContainer](container: U,
                                                                     renderingContext: PlantUMLRenderingContext): String = {
    s"""${
      container.packages.render(
        renderingContext)
    }${
      container.actors.render(
        renderingContext)
    }${
      container.useCases.render(
        renderingContext)
    }${
      container.relations.render(
        renderingContext)
    }"""
  }

  private def renderPlantUMLNode[U <: PlantUMLNode](node: U,
                                                    renderingContext: PlantUMLRenderingContext): String =
    s"""${renderingContext.spaces}${node.nodeType.render} "${node.name}""""


  implicit def plantUMLNodeRenderer[U <: PlantUMLNode]: PlantUMLRenderer[U] =

    PlantUMLRenderer.define {
      (umlNode, renderingContext) =>
        s"""${renderPlantUMLNode(umlNode, renderingContext)}""".stripMargin
    }

  implicit val plantUMLRelationRenderer: PlantUMLRenderer[PlantUMLRelation] =

    PlantUMLRenderer.define {
      (umlRelation, renderingContext) =>
        val relation =
          if (umlRelation.dash) {
            ".."
          }
          else {
            "--"
          }
        s"${renderingContext.spaces}${umlRelation.srcNode.render} ${umlRelation.shared.render}${relation}${umlRelation.targetType.render} ${umlRelation.destNode.render}"
    }

  implicit val plantUMLPackageRenderer: PlantUMLRenderer[PlantUMLPackage] =
    PlantUMLRenderer.define {
      (umlPackage, renderingContext) =>

        s"""${renderPlantUMLNode(umlPackage, renderingContext)} {
           |${renderPlantUMLContainerContent(umlPackage, renderingContext.increment)}${renderingContext.spaces}}"""
          .stripMargin
    }

  implicit def plantUMLDiagramRenderer: PlantUMLRenderer[PlantUMLDiagram] =
    PlantUMLRenderer.define {
      (umlDiagram, renderingContext) =>

        s"""@startuml
           |${renderPlantUMLContainerContent(umlDiagram, renderingContext)}@enduml
           |""".stripMargin
    }
}