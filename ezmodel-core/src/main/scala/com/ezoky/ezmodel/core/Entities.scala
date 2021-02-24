package com.ezoky.ezmodel.core

import com.ezoky.ezmodel.core.NaturalId.NaturalMap

import scala.collection.SortedSet

private[core] trait Entities
  extends Atoms {

  case class Entity(name: Name,
                    attributes: Map[Name, Attribute] = Map.empty[Name, Attribute],
                    aggregated: Map[Name, Aggregate] = Map.empty[Name, Aggregate],
                    referenced: Map[Name, Reference] = Map.empty[Name, Reference]) {

    def withAttribute(attributeName: Name,
                      multiplicity: Multiplicity = single,
                      mandatory: Boolean = false): Entity =
      copy(
        attributes = attributes + (attributeName -> Attribute(attributeName, multiplicity, mandatory))
      )

    def withAggregate(aggregateName: Name,
                      leaf: Entity,
                      multiplicity: Multiplicity = single,
                      mandatory: Boolean = false): Entity =
      copy(
        aggregated = aggregated + (aggregateName -> Aggregate(aggregateName, leaf, multiplicity, mandatory))
      )

    def withReference(referenceName: Name,
                      referencedEntity: Entity,
                      multiplicity: Multiplicity = single,
                      mandatory: Boolean = false): Entity =
      copy(
        referenced = referenced + (referenceName -> Reference(referenceName, referencedEntity, multiplicity, mandatory))
      )
  }

  type EntityId = NaturalId[Entity]
  type EntityMap = NaturalMap[EntityId, Entity]

  object EntityMap {
    def empty: EntityMap =
      NaturalMap.empty[EntityId, Entity]

    def apply(entities: Entity*)
             (implicit
              id: EntityId): EntityMap =
      NaturalMap(entities: _*)
  }


  case class StateName(qualifier: Qualifier)

  object UnknownStateName extends StateName(Qualifier("<unknown>"))

  object InitialStateName extends StateName(Qualifier("<initial>"))

  case class EntityState(entity: Entity,
                         state: StateName)

  object EntityState {

    def initial(entity: Entity): EntityState =
      EntityState(entity, InitialStateName)

    def unknown(entity: Entity): EntityState =
      EntityState(entity, UnknownStateName)
  }

  type EntityStateId = NaturalId[EntityState]
  type EntityStateMap = NaturalMap[EntityStateId, EntityState]

  object EntityStateMap {
    def empty: EntityStateMap =
      NaturalMap.empty[EntityStateId, EntityState]

    def apply(entityStates: EntityState*)
             (implicit
              id: EntityStateId): EntityStateMap =
      NaturalMap(entityStates: _*)
  }

  /**
    * TODO: need to add transitions
    * TODO: states Map should not be indexed by StateName as it can change.
    */
  case class StateMachine(entity: Entity,
                          states: Map[StateName, EntityState] = Map.empty) {

    def state(entityState: EntityState) =
      copy(states = states + (entityState.state -> entityState))
  }



  // Multiplicity
  sealed abstract class Multiplicity(multiplicity: String)

  case object single extends Multiplicity("single")

  case object multiple extends Multiplicity("multiple")

  case class exactly private(value: Int) extends Multiplicity(s"$value") {
    assume(value >= 0)
  }

  object exactly {
    def apply(value: Int): exactly = {
      val positiveValue = math.max(0, value)
      new exactly(positiveValue)
    }
  }

  case class among private(values: SortedSet[Int]) extends Multiplicity(s"$values") {
    values.map(value => assume(value >= 0))
  }

  object among {
    def apply(values: Int*): among = {
      val positiveValues = SortedSet.from(values.map(math.max(0, _)))
      new among(positiveValues)
    }
  }

  case class range private(min: Int,
                           max: Int) extends Multiplicity(s"$min-$max") {
    assume(min >= 0 && min <= max)
  }

  object range {
    def apply(min: Int,
              max: Int): range = {
      val positiveMin = math.max(min, 0)
      val positiveMax = math.max(max, 0)
      val actualMin = math.min(positiveMin, positiveMax)
      val actualMax = math.max(positiveMin, positiveMax)
      new range(actualMin, actualMax)
    }
  }

  case class Attribute(name: Name,
                       multiplicity: Multiplicity,
                       mandatory: Boolean)

  case class Reference(name: Name,
                       referenced: Entity,
                       multiplicity: Multiplicity,
                       mandatory: Boolean)

  case class Aggregate(name: Name,
                       leaf: Entity,
                       multiplicity: Multiplicity,
                       mandatory: Boolean)
}