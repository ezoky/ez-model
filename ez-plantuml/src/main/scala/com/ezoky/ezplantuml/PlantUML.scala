package com.ezoky.ezplantuml

import com.ezoky.ezplantuml.PlantUMLNode._

/**
  * @author gweinbach on 05/04/2021
  * @since 0.2.0
  */
sealed trait PlantUML

sealed trait PlantUMLContainer
  extends PlantUML {

  val packages: Set[PlantUMLPackage]
  val actors: Set[PlantUMLActor]
  val useCases: Set[PlantUMLUseCase]

  lazy val allNodes: Set[PlantUMLNode] =
    packages ++ actors ++ useCases

  def nodeReferences(parentPath: List[PlantUMLContainer]): Map[PlantUMLReference, PlantUMLNode] = {
    val path = parentPath :+ this
    allNodes.foldLeft(
      packages.foldLeft(Map.empty[PlantUMLReference, PlantUMLNode])((map, umlPackage) =>
        map ++ umlPackage.nodeReferences(path)
      )
    )((map, umlNode) =>
      map + (PlantUMLReference(path, umlNode) -> umlNode)
    )
  }

  val relations: Set[PlantUMLRelation]

  val pathElementId: String
}

sealed trait PlantUMLNode
  extends PlantUML {

  val name: String
  val nodeType: PlantUMLNodeType

  def reference(path: PlantUMLPath): PlantUMLReference =
    PlantUMLReference(path, this)
}

case class PlantUMLReference(path: PlantUMLPath,
                             element: PlantUML) {
  override def toString: String =
    path.id + "." + element
}

object PlantUMLReference {

  implicit val PlantUMLReferenceOrdering: Ordering[PlantUMLReference] =
    (x: PlantUMLReference, y: PlantUMLReference) =>
      if (x.path.size == y.path.size) {
        Ordering[String].compare(x.element.toString, y.element.toString)
      }
      else {
        Ordering[Int].compare(x.path.size, y.path.size)
      }
}

object PlantUMLNode {

  val UnknownId: PlantUMLNodeId =
    "<unknown id>"

  implicit class PlantUMLNodeId(val id: String) extends AnyVal {
    override def toString: String = id
  }

  type PlantUMLPath = List[PlantUMLContainer]

  implicit class PlantUMLPathHelper(path: PlantUMLPath) {
    def id: String =
      path.foldLeft("")((s, container) => s + "." + container.pathElementId)
  }

}

sealed trait PlantUMLNodeType

object PlantUMLNodeType {

  final case object Package extends PlantUMLNodeType

  final case object UseCase extends PlantUMLNodeType

  final case object Actor extends PlantUMLNodeType

  final case object Abstract extends PlantUMLNodeType

  final case object Annotation extends PlantUMLNodeType

  final case object Circle extends PlantUMLNodeType

  final case object Class extends PlantUMLNodeType

  final case object Diamond extends PlantUMLNodeType

  final case object Entity extends PlantUMLNodeType

  final case object Enum extends PlantUMLNodeType

  final case object Interface extends PlantUMLNodeType

}

sealed trait PlantUMLRelation
  extends PlantUML {
  val srcNode: PlantUMLNode
  val destNode: PlantUMLNode
  val srcMultiplicity: Option[PlantUMLMultiplicity]
  val destMultiplicity: Option[PlantUMLMultiplicity]
  val oriented: Boolean
  val dash: Boolean
  val label: Option[String]
  val shared: PlantUMLSharing
  val targetType: PlantUMLTargetType
}

sealed trait PlantUMLTargetType

object PlantUMLTargetType {

  final case object None extends PlantUMLTargetType

  final case object Arrow extends PlantUMLTargetType

  final case object Extension extends PlantUMLTargetType

  // TODO: Rename following according to UML semantics
  final case object Cube extends PlantUMLTargetType

  final case object Cross extends PlantUMLTargetType

  final case object DuckFoot extends PlantUMLTargetType

  final case object CircleCross extends PlantUMLTargetType

}

sealed trait PlantUMLSharing

object PlantUMLSharing {

  case object None extends PlantUMLSharing

  case object Aggregated extends PlantUMLSharing

  case object Composed extends PlantUMLSharing

}

sealed trait PlantUMLCardinality

object PlantUMLCardinality {

  final case object Zero extends PlantUMLCardinality

  final case object One extends PlantUMLCardinality

  final case object Many extends PlantUMLCardinality

  final case class Value(cardinality: Int)

  final case class Custom(cardinality: String)

}

sealed trait PlantUMLMultiplicity {
  val minCardinality: Option[PlantUMLCardinality]
  val maxCardinality: PlantUMLCardinality
}

object PlantUMLMultiplicity {

  final case class Custom(minCardinality: Option[PlantUMLCardinality],
                          maxCardinality: PlantUMLCardinality)
    extends PlantUMLMultiplicity

  final case object One extends PlantUMLMultiplicity {
    override val minCardinality: Option[PlantUMLCardinality] = None
    override val maxCardinality: PlantUMLCardinality = PlantUMLCardinality.One
  }

  final case object OneOne extends PlantUMLMultiplicity {
    override val minCardinality: Option[PlantUMLCardinality] = Some(PlantUMLCardinality.One)
    override val maxCardinality: PlantUMLCardinality = PlantUMLCardinality.One
  }

  final case object ZeroOne extends PlantUMLMultiplicity {
    override val minCardinality: Option[PlantUMLCardinality] = Some(PlantUMLCardinality.Zero)
    override val maxCardinality: PlantUMLCardinality = PlantUMLCardinality.One
  }

  final case object Many extends PlantUMLMultiplicity {
    override val minCardinality: Option[PlantUMLCardinality] = None
    override val maxCardinality: PlantUMLCardinality = PlantUMLCardinality.Many
  }

  final case object OneMany extends PlantUMLMultiplicity {
    override val minCardinality: Option[PlantUMLCardinality] = Some(PlantUMLCardinality.One)
    override val maxCardinality: PlantUMLCardinality = PlantUMLCardinality.Many
  }

  final case object ZeroMany extends PlantUMLMultiplicity {
    override val minCardinality: Option[PlantUMLCardinality] = Some(PlantUMLCardinality.Zero)
    override val maxCardinality: PlantUMLCardinality = PlantUMLCardinality.Many
  }

}


// Containers

final case class PlantUMLDiagram(packages: Set[PlantUMLPackage] = Set.empty,
                                 actors: Set[PlantUMLActor] = Set.empty,
                                 useCases: Set[PlantUMLUseCase] = Set.empty,
                                 relations: Set[PlantUMLRelation] = Set.empty)
  extends PlantUMLContainer {
  override val pathElementId: String = "_root_"
}

final case class PlantUMLPackage(name: String,
                                 packages: Set[PlantUMLPackage] = Set.empty,
                                 actors: Set[PlantUMLActor] = Set.empty,
                                 useCases: Set[PlantUMLUseCase] = Set.empty,
                                 relations: Set[PlantUMLRelation] = Set.empty)
  extends PlantUMLContainer
    with PlantUMLNode {
  override val nodeType: PlantUMLNodeType = PlantUMLNodeType.Package
  override val pathElementId: String = name
}


// Nodes

final case class PlantUMLUseCase(name: String)
  extends PlantUMLNode {
  override val nodeType: PlantUMLNodeType = PlantUMLNodeType.UseCase
}

final case class PlantUMLActor(name: String)
  extends PlantUMLNode {
  override val nodeType: PlantUMLNodeType = PlantUMLNodeType.Actor
}


// Relations

final case class PlantUMLink(srcNode: PlantUMLNode,
                             destNode: PlantUMLNode,
                             srcMultiplicity: Option[PlantUMLMultiplicity] = None,
                             destMultiplicity: Option[PlantUMLMultiplicity] = None,
                             dash: Boolean = false,
                             label: Option[String] = None)
  extends PlantUMLRelation {
  override val targetType: PlantUMLTargetType = PlantUMLTargetType.None
  override val shared: PlantUMLSharing = PlantUMLSharing.None
  override val oriented: Boolean = false
}

final case class PlantUMLOrientedLink(srcNode: PlantUMLNode,
                                      destNode: PlantUMLNode,
                                      srcMultiplicity: Option[PlantUMLMultiplicity] = None,
                                      destMultiplicity: Option[PlantUMLMultiplicity] = None,
                                      dash: Boolean = false,
                                      label: Option[String] = None)
  extends PlantUMLRelation {
  override val targetType: PlantUMLTargetType = PlantUMLTargetType.Arrow
  override val shared: PlantUMLSharing = PlantUMLSharing.None
  override val oriented: Boolean = true
}

final case class PlantUMLExtension(srcNode: PlantUMLNode,
                                   destNode: PlantUMLNode,
                                   srcMultiplicity: Option[PlantUMLMultiplicity] = None,
                                   destMultiplicity: Option[PlantUMLMultiplicity] = None,
                                   dash: Boolean = false,
                                   label: Option[String] = None)
  extends PlantUMLRelation {
  override val targetType: PlantUMLTargetType = PlantUMLTargetType.Extension
  override val shared: PlantUMLSharing = PlantUMLSharing.None
  override val oriented: Boolean = false
}

final case class PlantUMLDependency(srcNode: PlantUMLNode,
                                    destNode: PlantUMLNode,
                                    srcMultiplicity: Option[PlantUMLMultiplicity] = None,
                                    destMultiplicity: Option[PlantUMLMultiplicity] = None,
                                    label: Option[String] = None,
                                    oriented: Boolean = true)
  extends PlantUMLRelation {
  override lazy val targetType: PlantUMLTargetType =
    if (oriented) {
      PlantUMLTargetType.Arrow
    }
    else {
      PlantUMLTargetType.None
    }
  override val shared: PlantUMLSharing = PlantUMLSharing.None
  override val dash: Boolean = true
}

final case class PlantUMLComposition(srcNode: PlantUMLNode,
                                     destNode: PlantUMLNode,
                                     srcMultiplicity: Option[PlantUMLMultiplicity] = None,
                                     destMultiplicity: Option[PlantUMLMultiplicity] = None,
                                     dash: Boolean = false,
                                     label: Option[String] = None,
                                     oriented: Boolean = false)
  extends PlantUMLRelation {
  override lazy val targetType: PlantUMLTargetType =
    if (oriented) {
      PlantUMLTargetType.Arrow
    }
    else {
      PlantUMLTargetType.None
    }
  override val shared: PlantUMLSharing = PlantUMLSharing.Composed
}

final case class PlantUMLAggregation(srcNode: PlantUMLNode,
                                     destNode: PlantUMLNode,
                                     srcMultiplicity: Option[PlantUMLMultiplicity] = None,
                                     destMultiplicity: Option[PlantUMLMultiplicity] = None,
                                     dash: Boolean = false,
                                     label: Option[String] = None,
                                     oriented: Boolean = false)
  extends PlantUMLRelation {
  override lazy val targetType: PlantUMLTargetType =
    if (oriented) {
      PlantUMLTargetType.Arrow
    }
    else {
      PlantUMLTargetType.None
    }
  override val shared: PlantUMLSharing = PlantUMLSharing.Aggregated
}
