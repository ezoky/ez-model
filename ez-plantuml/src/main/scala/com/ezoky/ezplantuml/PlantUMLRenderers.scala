package com.ezoky.ezplantuml

import com.ezoky.ezplantuml.PlantUMLNode.PlantUMLNodeId
import PlantUMLReference._

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

    def nodes: Set[PlantUMLNode] =
      plantUML match {
        case container: PlantUMLContainer =>
          container.allNodes
        case _ =>
          Set.empty
      }

    def nodeReferences: Map[PlantUMLReference, PlantUMLNode] =
      plantUML match {
        case diagram: PlantUMLDiagram =>
          diagram.nodeReferences(List.empty)
        case _ =>
          Map.empty[PlantUMLReference, PlantUMLNode]
      }

    def render(renderingContext: PlantUMLRenderingContext = PlantUMLRenderingContext(plantUML.nodeReferences))
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

  implicit class PlantUMLNodeIdHelper(nodeId: PlantUMLNodeId) {

    def render: String =
      nodeId.id
  }

  implicit class PlantUMLNodeHelper(node: PlantUMLNode) {

    def id(renderingContext: PlantUMLRenderingContext): String =
      renderingContext.nodeIdMap.getOrElse(renderingContext.reference(node), PlantUMLNode.UnknownId).render
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

    def nodeIdPrefix: String =
      plantUMLNodeType match {
        case PlantUMLNodeType.Package => "PK"
        case PlantUMLNodeType.UseCase => "UC"
        case PlantUMLNodeType.Actor => "AC"
        case PlantUMLNodeType.Abstract => "AB"
        case PlantUMLNodeType.Annotation => "AN"
        case PlantUMLNodeType.Circle => "CI"
        case PlantUMLNodeType.Class => "CL"
        case PlantUMLNodeType.Diamond => "DI"
        case PlantUMLNodeType.Entity => "EN"
        case PlantUMLNodeType.Enum => "EN"
        case PlantUMLNodeType.Interface => "IN"
      }

    def buildId(number: Int): PlantUMLNodeId =
      s"${nodeIdPrefix}${number}"

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

  case class PlantUMLRenderingContext private(tabs: Int,
                                              path: List[PlantUMLContainer],
                                              nextId: Int,
                                              nodeIdMap: Map[PlantUMLReference, PlantUMLNodeId]) {

    val spaces: String = " " * tabs * TabSize

    lazy val increment: PlantUMLRenderingContext = copy(tabs = tabs + 1)
    lazy val decrement: PlantUMLRenderingContext = copy(tabs = tabs - 1)

    def addNode(nodeReference: PlantUMLReference,
                node: PlantUMLNode): PlantUMLRenderingContext =
      copy(
        nextId = nextId + 1,
        nodeIdMap = nodeIdMap + (nodeReference -> node.nodeType.buildId(nextId))
      )

    def in(container: PlantUMLContainer): PlantUMLRenderingContext =
      copy(path = path :+ container)

    def reference(element: PlantUML): PlantUMLReference =
      PlantUMLReference(path, element)
  }

  object PlantUMLRenderingContext {

    lazy val Empty: PlantUMLRenderingContext =
      PlantUMLRenderingContext(
        tabs = 0,
        path = List.empty,
        nextId = 1,
        nodeIdMap = Map.empty
      )

    def apply(nodeReferences: Map[PlantUMLReference, PlantUMLNode]): PlantUMLRenderingContext =
      nodeReferences
        .toSeq.sortBy(_._1)
        .foldLeft(Empty)((ctxt, nodeReference) => ctxt.addNode(nodeReference._1, nodeReference._2))
  }

  private def renderPlantUMLContainerContent[U <: PlantUMLContainer](container: U,
                                                                     renderingContext: PlantUMLRenderingContext): String = {
    s"""${
      container.packages.render(renderingContext)
    }${
      container.actors.render(renderingContext)
    }${
      container.useCases.render(renderingContext)
    }${
      container.relations.render(renderingContext)
    }"""
  }

  private def renderPlantUMLNode[U <: PlantUMLNode](node: U,
                                                    renderingContext: PlantUMLRenderingContext): String =
    s"""${renderingContext.spaces}${node.nodeType.render} "${node.name}" as ${node.id(renderingContext)}"""


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
        s"${renderingContext.spaces}${umlRelation.srcNode.id(renderingContext)} ${
          umlRelation
            .shared
            .render
        }${relation}${umlRelation.targetType.render} ${umlRelation.destNode.id(renderingContext)}"
    }

  implicit val plantUMLPackageRenderer: PlantUMLRenderer[PlantUMLPackage] =
    PlantUMLRenderer.define {
      (umlPackage, renderingContext) =>

        s"""${renderPlantUMLNode(umlPackage, renderingContext)} {
           |${renderPlantUMLContainerContent(umlPackage, renderingContext.in(umlPackage).increment)}${renderingContext.spaces}}"""
          .stripMargin
    }

  implicit def plantUMLDiagramRenderer: PlantUMLRenderer[PlantUMLDiagram] =
    PlantUMLRenderer.define {
      (umlDiagram, renderingContext) =>

        s"""@startuml
           |${renderPlantUMLContainerContent(umlDiagram, renderingContext.in(umlDiagram))}@enduml
           |""".stripMargin
    }
}