package com.ezoky.ezmodel.interaction.dsl

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


  implicit class EntityHelper(entity: Entity) {

    def hasA(attributeName: String): Entity =
      entity.withAttribute(Name(attributeName))

    def hasAn(attributeName: String): Entity =
      hasA(attributeName)

    def references(fluentReference: FluentEntityReference): UnnamedReference =
      UnnamedReference(
        entity,
        fluentReference.leaf,
        fluentReference.multiplicity,
        fluentReference.mandatory
      )

    def aggregates(fluentAggregate: FluentEntityReference): UnnamedAggregate =
      UnnamedAggregate(
        entity,
        fluentAggregate.leaf,
        fluentAggregate.multiplicity,
        fluentAggregate.mandatory
      )
  }

  sealed trait FluentEntityReference {
    val leaf: Entity
    val multiplicity: Multiplicity
    val mandatory: Boolean
  }

  case class one(leaf: Entity)
    extends FluentEntityReference {
    override val multiplicity: Multiplicity = single
    override val mandatory: Boolean = false
  }

  case class oneAndOnlyOne(leaf: Entity)
    extends FluentEntityReference {
    override val multiplicity: Multiplicity = single
    override val mandatory: Boolean = true
  }

  case class many(leaf: Entity)
    extends FluentEntityReference {
    override val multiplicity: Multiplicity = multiple
    override val mandatory: Boolean = false
  }

  case class atLeastOne(leaf: Entity)
    extends FluentEntityReference {
    override val multiplicity: Multiplicity = multiple
    override val mandatory: Boolean = true
  }

  // Reference Fluent DSL
  case class UnnamedReference(root: Entity,
                              leaf: Entity,
                              multiplicity: Multiplicity,
                              mandatory: Boolean) {

    def as(aggregateName: Name): Entity =
      root
        .withReference(
          aggregateName,
          leaf,
          multiplicity,
          mandatory
        )
  }

  object UnnamedReference {
    def apply(root: Entity,
              fluentReference: FluentEntityReference): UnnamedReference =
      UnnamedReference(
        root,
        fluentReference
      )
  }

  implicit def UnnamedReferenceToEntity(unnamedReference: UnnamedReference): Entity =
    unnamedReference.root
      .withReference(
        unnamedReference.leaf.name,
        unnamedReference.leaf,
        unnamedReference.multiplicity,
        unnamedReference.mandatory
      )

  // Aggregate Fluent DSL
  case class UnnamedAggregate(root: Entity,
                              leaf: Entity,
                              multiplicity: Multiplicity,
                              mandatory: Boolean) {

    def as(aggregateName: Name): Entity =
      root
        .withAggregate(
          aggregateName,
          leaf,
          multiplicity,
          mandatory
        )
  }

  object UnnamedAggregate {
    def apply(root: Entity,
              fluentAggregate: FluentEntityReference): UnnamedAggregate =
      UnnamedAggregate(
        root,
        fluentAggregate
      )
  }

  implicit def UnnamedAggregateToEntity(unnamedAggregate: UnnamedAggregate): Entity =
    unnamedAggregate.root
      .withAggregate(
        unnamedAggregate.leaf.name,
        unnamedAggregate.leaf,
        unnamedAggregate.multiplicity,
        unnamedAggregate.mandatory
      )
}
