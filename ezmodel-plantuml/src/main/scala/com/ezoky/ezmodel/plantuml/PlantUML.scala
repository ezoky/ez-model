package com.ezoky.ezmodel.plantuml

import com.ezoky.ezmodel.plantuml.PlantUMLNode.PlantUMLNodeId

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
  val relations: Set[PlantUMLRelation]
}

sealed trait PlantUMLNode
  extends PlantUML {
  val name: String
  val nodeType: PlantUMLNodeType
}

object PlantUMLNode {

  implicit class PlantUMLNodeId(val id: String) extends AnyVal

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
  val srcNode: PlantUMLNodeId
  val destNode: PlantUMLNodeId
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
  extends PlantUMLContainer

final case class PlantUMLPackage(name: String,
                                 packages: Set[PlantUMLPackage] = Set.empty,
                                 actors: Set[PlantUMLActor] = Set.empty,
                                 useCases: Set[PlantUMLUseCase] = Set.empty,
                                 relations: Set[PlantUMLRelation] = Set.empty)
  extends PlantUMLContainer
    with PlantUMLNode {
  override val nodeType: PlantUMLNodeType = PlantUMLNodeType.Package
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

final case class PlantUMLink(srcNode: PlantUMLNodeId,
                             destNode: PlantUMLNodeId,
                             srcMultiplicity: Option[PlantUMLMultiplicity] = None,
                             destMultiplicity: Option[PlantUMLMultiplicity] = None,
                             dash: Boolean = false,
                             label: Option[String] = None)
  extends PlantUMLRelation {
  override val targetType: PlantUMLTargetType = PlantUMLTargetType.None
  override val shared: PlantUMLSharing = PlantUMLSharing.None
  override val oriented: Boolean = false
}

final case class PlantUMLOrientedLink(srcNode: PlantUMLNodeId,
                                      destNode: PlantUMLNodeId,
                                      srcMultiplicity: Option[PlantUMLMultiplicity] = None,
                                      destMultiplicity: Option[PlantUMLMultiplicity] = None,
                                      dash: Boolean = false,
                                      label: Option[String] = None)
  extends PlantUMLRelation {
  override val targetType: PlantUMLTargetType = PlantUMLTargetType.Arrow
  override val shared: PlantUMLSharing = PlantUMLSharing.None
  override val oriented: Boolean = true
}

final case class PlantUMLExtension(srcNode: PlantUMLNodeId,
                                   destNode: PlantUMLNodeId,
                                   srcMultiplicity: Option[PlantUMLMultiplicity] = None,
                                   destMultiplicity: Option[PlantUMLMultiplicity] = None,
                                   dash: Boolean = false,
                                   label: Option[String] = None)
  extends PlantUMLRelation {
  override val targetType: PlantUMLTargetType = PlantUMLTargetType.Extension
  override val shared: PlantUMLSharing = PlantUMLSharing.None
  override val oriented: Boolean = false
}

final case class PlantUMLDependency(srcNode: PlantUMLNodeId,
                                    destNode: PlantUMLNodeId,
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

final case class PlantUMLComposition(srcNode: PlantUMLNodeId,
                                     destNode: PlantUMLNodeId,
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

final case class PlantUMLAggregation(srcNode: PlantUMLNodeId,
                                     destNode: PlantUMLNodeId,
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
