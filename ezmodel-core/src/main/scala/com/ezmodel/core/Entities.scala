package com.ezmodel.core


object Entities {

  import com.ezmodel.core.Atoms._
  import com.ezmodel.storage.EventStore

import scala.language.{existentials, implicitConversions}

  case class StateName(qualifier: Qualifier)

  object UnknownStateName extends StateName(Qualifier("<unknown>"))

  object InitialStateName extends StateName(Qualifier("<initial>"))

  implicit def stringState(qualifier: String) = new StateName(Qualifier(qualifier))

  case class StateMachine(entity: Entity, states: Map[StateName, EntityState] = Map()) {

    EventStore(Model).store(this)

    def state(entityState: EntityState) = copy(states = states + (entityState.state -> entityState))
  }

  class EntityState(val entity: Entity, val state: StateName) extends Entity(Name(s"${entity.name} [${state.qualifier}]"))

  object EntityState {
    def apply(entity: Entity, state: StateName) = new EntityState(entity,state)
  }

  class InitialEntityState(entity: Entity) extends EntityState(entity, InitialStateName)

  abstract case class Multiplicity(multiplicity: String)

  object single extends Multiplicity("single")

  object multiple extends Multiplicity("multiple")

  class Exactly(value: Int) extends Multiplicity(s"$value")

  class Range(min: Int, max: Int) extends Multiplicity(s"$min-$max") {
    assume(min >= 0 && min <= max)
  }


  case class Attribute(name: Name, multiplicity: Multiplicity, mandatory: Boolean)

  case class Reference(name: Name, referenced: Entity, multiplicity: Multiplicity, mandatory: Boolean)

  case class Aggregate(root: Entity, name: Name, leaf: Entity, multiplicity: Multiplicity, mandatory: Boolean)

  case class Entity(name: Name, attributes: Map[Name, Attribute] = Map[Name, Attribute](), aggregates: Map[Name, Aggregate] = Map[Name, Aggregate](), references: Map[Name, Reference] = Map[Name, Reference]()) {

    EventStore(Model).store(this)

    def attribute(attributeName: Name, multiplicity: Multiplicity = single, mandatory: Boolean = false) = copy(attributes = attributes + (attributeName -> Attribute(attributeName, multiplicity, mandatory)))

    def aggregate(aggregateName: Name, leaf: Entity, multiplicity: Multiplicity = single, mandatory: Boolean = false) = copy(aggregates = aggregates + (aggregateName -> Aggregate(this, aggregateName, leaf, multiplicity, mandatory)))

    def aggregate(multiplicity: Multiplicity, leaf: Entity): Entity = aggregate(DefaultName, leaf, multiplicity)

    def reference(referenceName: Name, referenced: Entity, multiplicity: Multiplicity = single, mandatory: Boolean = false) = copy(references = references + (referenceName -> Reference(referenceName, referenced, multiplicity, mandatory)))

    def reference(multiplicity: Multiplicity, referenced: Entity): Entity = reference(DefaultName, referenced, multiplicity)

    override def toString = s"${getClass.getSimpleName}($name)"

    override def equals(other: Any): Boolean =
      other match {
        case that: Entity => that.name == this.name
        case _ => false
      }

    override def hashCode: Int =
      41 * name.hashCode
  }

  implicit def stringEntity(name: String) = Entity(Name(name))
}