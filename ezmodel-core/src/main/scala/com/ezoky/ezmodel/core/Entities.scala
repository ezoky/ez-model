package com.ezoky.ezmodel.core

import com.ezoky.commons.NaturalIds
import com.ezoky.ezmodel.core.requirements.Atoms

import scala.collection.SortedSet

private[core] trait Entities
  extends Atoms
    with NaturalIds {

  case class Entity(name: Name,
                    attributes: AttributeMap = AttributeMap.empty,
                    aggregated: AggregateMap = AggregateMap.empty,
                    referenced: ReferenceMap = ReferenceMap.empty)
                   (implicit
                    attributeId: AttributeId,
                    aggregateId: AggregateId,
                    referenceId: ReferenceId) {

    def withAttribute(attributeName: Name,
                      multiplicity: Multiplicity = single,
                      mandatory: Boolean = false): Entity =
      copy(
        attributes = attributes.add(Attribute(attributeName, multiplicity, mandatory))
      )

    def withAggregate(aggregateName: Name,
                      leaf: Entity,
                      multiplicity: Multiplicity = single,
                      mandatory: Boolean = false): Entity =
      copy(
        aggregated = aggregated.add(Aggregate(aggregateName, leaf, multiplicity, mandatory))
      )

    def withReference(referenceName: Name,
                      referencedEntity: Entity,
                      multiplicity: Multiplicity = single,
                      mandatory: Boolean = false): Entity =
      copy(
        referenced = referenced.add(Reference(referenceName, referencedEntity, multiplicity, mandatory))
      )
  }

  type EntityId = NaturalId[Entity]
  type EntityMap = NaturalMap[EntityId, Entity]

  object EntityMap extends NaturalMapCompanion[EntityId, Entity]


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

  object EntityStateMap extends NaturalMapCompanion[EntityStateId, EntityState]


  /**
    * TODO: need to add transitions
    * TODO: states Map should not be indexed by StateName as it can change.
    */
  case class StateMachine(entity: Entity,
                          states: EntityStateMap = EntityStateMap.empty)
                         (implicit
                          entityStateId: EntityStateId) {

    def state(entityState: EntityState) =
      copy(states = states.add(entityState))
  }



  // Multiplicity
  sealed abstract class Multiplicity(multiplicity: String) {
    val min: Int
    val max: Int
  }

  case object single extends Multiplicity("single") {
    override val min: Int = 0
    override val max: Int = 1
  }

  case object multiple extends Multiplicity("multiple") {
    override val min: Int = 0
    override val max: Int = Int.MaxValue
  }

  case class exactly private(value: Int) extends Multiplicity(s"$value") {
    assume(value >= 0)
    override val min: Int = value
    override val max: Int = value
  }

  object exactly {
    def apply(value: Int): exactly = {
      val positiveValue = math.max(0, value)
      new exactly(positiveValue)
    }
  }

  case class among private(values: SortedSet[Int]) extends Multiplicity(s"$values") {
    values.map(value => assume(value >= 0))
    override val min: Int = values.min
    override val max: Int = values.max
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

  implicit val MultiplicityOrdering: Ordering[Multiplicity] =
    Ordering.by(m => (m.max - m.min).abs)

  case class Attribute(name: Name,
                       multiplicity: Multiplicity,
                       mandatory: Boolean)

  type AttributeId = NaturalId[Attribute]
  type AttributeMap = NaturalMap[AttributeId, Attribute]

  object AttributeMap extends NaturalMapCompanion[AttributeId, Attribute]


  case class Reference(name: Name,
                       referenced: Entity,
                       multiplicity: Multiplicity,
                       mandatory: Boolean)

  type ReferenceId = NaturalId[Reference]
  type ReferenceMap = NaturalMap[ReferenceId, Reference]

  object ReferenceMap extends NaturalMapCompanion[ReferenceId, Reference]


  case class Aggregate(name: Name,
                       leaf: Entity,
                       multiplicity: Multiplicity,
                       mandatory: Boolean)

  type AggregateId = NaturalId[Aggregate]
  type AggregateMap = NaturalMap[AggregateId, Aggregate]

  object AggregateMap extends NaturalMapCompanion[AggregateId, Aggregate]

}