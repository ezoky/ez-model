package com.ezoky.ezmodel.interaction.dsl

import com.ezoky.ezmodel.core.Models._

/**
  * @author gweinbach on 04/02/2021
  * @since 0.2.0
  */
trait EntityDSL {

  // Entities
  implicit def stringState(qualifier: String): StateName =
    StateName(Qualifier(qualifier))

  implicit def stringEntity(name: String): Entity =
    Entity(Name(name))

  implicit class EntityStateHelper(entityName: String) {

    def is(stateName: String): EntityState =
      EntityState(Entity(Name(entityName)), StateName(Qualifier(stateName)))
  }


  case class UnnamedAggregate(root: Entity,
                              leaf: Entity,
                              multiplicity: Multiplicity,
                              mandatory: Boolean) {

    def as(aggregateName: Name): Entity =
      root
        .withAggregate(
          leaf.name,
          leaf,
          multiplicity,
          mandatory
        )
  }

  object UnnamedAggregate {
    def apply(root: Entity,
              fluentAggregate: FluentAggregate): UnnamedAggregate =
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

  sealed trait FluentAggregate {
    val leaf: Entity
    val multiplicity: Multiplicity
    val mandatory: Boolean
  }

  case class one(leaf: Entity)
    extends FluentAggregate {
    override val multiplicity: Multiplicity = single
    override val mandatory: Boolean = false
  }

  case class many(leaf: Entity)
    extends FluentAggregate {
    override val multiplicity: Multiplicity = multiple
    override val mandatory: Boolean = false
  }

  implicit class EntityHelper(entity: Entity) {

    def aggregates(fluentAggregate: FluentAggregate): UnnamedAggregate = {
      UnnamedAggregate(
        entity,
        fluentAggregate.leaf,
        fluentAggregate.multiplicity,
        fluentAggregate.mandatory
      )
    }

    def references(multiplicity: Multiplicity,
                   referenced: Entity): Entity =
      entity.withReference(referenced.name, referenced, multiplicity)
  }
}
