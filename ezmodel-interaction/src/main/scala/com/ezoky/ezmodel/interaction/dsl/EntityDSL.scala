package com.ezoky.ezmodel.interaction.dsl

import com.ezoky.ezmodel.core.Models
import com.ezoky.ezmodel.core.Models._

/**
  * @author gweinbach on 04/02/2021
  * @since 0.2.0
  */
trait EntityDSL {

  // Entity State
  implicit def stringState(qualifier: String): StateName =
    StateName(Qualifier(qualifier))

  implicit class EntityStateHelper(entityName: String) {

    def is(stateName: String): EntityState =
      EntityState(Entity(Name(entityName)), StateName(Qualifier(stateName)))
  }

  // Entities
  implicit def stringEntity(name: String): Entity =
    Entity(Name(name))

  def theEntity(name: String): Entity =
    Entity(Name(name))


  implicit class EntityHelper(entity: Entity)
    extends EntityActions {

    def currentEntity: Entity =
      entity

    def hasA(attributeName: String): Entity =
      entity.withAttribute(Name(attributeName))

    def hasAn(attributeName: String): Entity =
      hasA(attributeName)
  }

  trait EntityActions {

    def currentEntity: Entity

    def has(fluentAttribute: FluentMultiplicity[Name]): AttributeReference =
      AttributeReference(
        currentEntity,
        fluentAttribute.target,
        fluentAttribute.multiplicity,
        fluentAttribute.mandatory
      )

    def references(fluentTarget: FluentMultiplicity[Entity]): EntityReference =
      EntityReference(
        currentEntity,
        fluentTarget.target,
        fluentTarget.multiplicity,
        fluentTarget.mandatory
      )

    def aggregates(fluentAggregate: FluentMultiplicity[Entity]): AggregateReference =
      AggregateReference(
        currentEntity,
        fluentAggregate.target,
        fluentAggregate.multiplicity,
        fluentAggregate.mandatory
      )
  }

  sealed trait FluentMultiplicity[T] {
    val target: T
    val multiplicity: Multiplicity
    val mandatory: Boolean
    val name: Option[String] = None

    def as(newName: String): FluentMultiplicity[T] =
      FluentMultiplicity[T](
        target,
        multiplicity,
        mandatory,
        newName
      )
  }

  sealed trait FluentReference[T <: FluentReference[T]]
    extends EntityActions {

    val root: Entity
    val multiplicity: Multiplicity
    val mandatory: Boolean

    def as(newName: Name): T

    def toEntity: Entity

    final override def currentEntity: Entity =
      toEntity
  }


  object FluentMultiplicity {
    def apply[T](t: T,
                 targetMultiplicity: Multiplicity,
                 targetMandatory: Boolean,
                 targetName: String): FluentMultiplicity[T] =
      new FluentMultiplicity[T] {
        override val target: T = t
        override val multiplicity: Models.Multiplicity = targetMultiplicity
        override val mandatory: Boolean = targetMandatory
        override val name: Option[String] = Some(targetName)
      }
  }

  case class one[T](target: T)
    extends FluentMultiplicity[T] {
    override val multiplicity: Multiplicity = single
    override val mandatory: Boolean = false
  }

  case class oneAndOnlyOne[T](target: T)
    extends FluentMultiplicity[T] {
    override val multiplicity: Multiplicity = single
    override val mandatory: Boolean = true
  }

  case class many[T](target: T)
    extends FluentMultiplicity[T] {
    override val multiplicity: Multiplicity = multiple
    override val mandatory: Boolean = false
  }

  case class atLeastOne[T](target: T)
    extends FluentMultiplicity[T] {
    override val multiplicity: Multiplicity = multiple
    override val mandatory: Boolean = true
  }

  case class exactly[T](n: Int, target: T)
    extends FluentMultiplicity[T] {
    override val multiplicity: Multiplicity = Models.exactly(n)
    override val mandatory: Boolean = true
  }

  case class among[T](multiplicities: List[Int], target: T)
    extends FluentMultiplicity[T] {
    override val multiplicity: Multiplicity = Models.among(multiplicities: _*)
    override val mandatory: Boolean = true
  }

  case class between[T](min: Int, max: Int, target: T)
    extends FluentMultiplicity[T] {
    override val multiplicity: Multiplicity = Models.range(min, max)
    override val mandatory: Boolean = true
  }



  // Attribute Fluent DSL
  case class AttributeReference(root: Entity,
                                attributeName: Name,
                                multiplicity: Multiplicity,
                                mandatory: Boolean)
    extends FluentReference[AttributeReference] {

    override def toEntity: Entity =
      root
      .withAttribute(
        attributeName,
        multiplicity,
        mandatory
      )

    override def as(newName: Name): AttributeReference=
      copy(attributeName = newName)
  }

  implicit def attributeReferenceToEntity(attributeReference: AttributeReference): Entity =
    attributeReference.currentEntity


  // Reference Fluent DSL
  case class EntityReference(root: Entity,
                             leaf: Entity,
                             multiplicity: Multiplicity,
                             mandatory: Boolean,
                             name: Option[Name] = None)
    extends FluentReference[EntityReference] {

    override def toEntity: Entity =
      root
        .withReference(
          name.getOrElse(leaf.name),
          leaf,
          multiplicity,
          mandatory
        )

    def as(referenceName: Name): EntityReference =
      copy(name = Some(referenceName))
  }

  implicit def unnamedReferenceToEntity(unnamedReference: EntityReference): Entity =
    unnamedReference.currentEntity


  // Aggregate Fluent DSL
  case class AggregateReference(root: Entity,
                                leaf: Entity,
                                multiplicity: Multiplicity,
                                mandatory: Boolean,
                                name: Option[Name] = None)
    extends FluentReference[AggregateReference] {

    override def toEntity: Entity =
      root
        .withAggregate(
          name.getOrElse(leaf.name),
          leaf,
          multiplicity,
          mandatory
        )

    def as(aggregateName: Name): AggregateReference =
      copy(name = Some(aggregateName))
  }

  implicit def unnamedAggregateToEntity(unnamedAggregate: AggregateReference): Entity =
    unnamedAggregate.currentEntity
}
