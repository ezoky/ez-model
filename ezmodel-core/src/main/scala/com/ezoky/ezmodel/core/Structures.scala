package com.ezoky.ezmodel.core

object Structures {

  import com.ezoky.ezmodel.storage.EventStore

  import scala.language.implicitConversions
  import scala.language.existentials

  import com.ezoky.ezmodel.storage.EventStore

  import Atoms._

  case class State(qualifier: Qualifier)
  object UnknownState extends State(Qualifier("<unknown>"))
  object InitialState extends State(Qualifier("<initial>"))
  implicit def stringState(qualifier: String) = new State(Qualifier(qualifier))

  case class Entity(name: Name) extends Structure[Entity] with Attributing[Entity] with Referencing[Entity] with Aggregating[Entity]
  implicit def stringEntity(name: String) = Entity(Name(name))

  case class StateMachine(entity: Entity) {
    val states: Map[State, EntityState] = Map()

    EventStore(Model).store(this)

    def state(entityState: EntityState) {
      val parentStates = states
      new StateMachine(entity) {
        override val states = parentStates + (entityState.state -> entityState)
      }
    }
  }

  case class EntityState(entity: Entity, state: State) extends Structure[EntityState] with Attributing[EntityState] with Referencing[EntityState] with Aggregating[EntityState] {
    override val name = Name(s"${entity.name.name} [${state.qualifier.qualifier}]")
  }
  class InitialEntityState(entity: Entity) extends EntityState(entity, InitialState)

  abstract case class Multiplicity(multiplicity: String)
  object single extends Multiplicity("single")
  object multiple extends Multiplicity("multiple")
  class Range(min: Int, max: Int) extends Multiplicity(s"$min-$max") {
    assume(min >= 0 && min <= max)
  }

  trait Structure[T] {

    val name: Name
    val attributes = List[Attribute]()
    val aggregates = List[Aggregate]()
    val references = List[Reference]()

    def attribute(name: Name, multiplicity: Multiplicity = single, mandatory: Boolean = false) = Attributing(this, name, multiplicity, mandatory)

    def aggregate(name: Name, leaf: Structure[T], multiplicity: Multiplicity = single, mandatory: Boolean = false) = Aggregating(this, name, leaf, multiplicity, mandatory)
    def aggregate(multiplicity: Multiplicity, leaf: Structure[T]) = Aggregating(this, DefaultName, leaf)

    def reference(name: Name, referenced: Structure[T], multiplicity: Multiplicity = single, mandatory: Boolean = false) = Referencing(this, name, referenced, multiplicity, mandatory)
    def reference(multiplicity: Multiplicity, referenced: Structure[T]) = Referencing(this, DefaultName, referenced)

    override def toString = s"${getClass.getSimpleName}(${name})"
    EventStore(Model).store(this)

    override def equals(other: Any): Boolean =
      other match {
        case that: Structure[T] => that.name == this.name
        case _ => false
      }
    override def hashCode: Int =
      41 * name.hashCode
  }
  case class Attribute(name: Name, multiplicity: Multiplicity, mandatory: Boolean)
  case class Reference(name: Name, referenced: Structure[_], multiplicity: Multiplicity, mandatory: Boolean)
  case class Aggregate(root: Structure[_], name: Name, leaf: Structure[_], multiplicity: Multiplicity, mandatory: Boolean)

  trait Attributing[T] extends Structure[T]
  object Attributing {
    def apply[T](owner: Structure[T], attributeName: Name, multiplicity: Multiplicity = single, mandatory: Boolean = false) = {
      new Structure[T] {
        override val name = owner.name
        override val attributes = Attribute(attributeName, multiplicity, mandatory) :: owner.attributes
        override val aggregates = owner.aggregates
        override val references = owner.references
      }
    }
  }
  trait Aggregating[T] extends Structure[T]
  object Aggregating {
    def apply[T](root: Structure[T], aggregateName: Name, leaf: Structure[_], multiplicity: Multiplicity = single, mandatory: Boolean = false) = {
      new Structure[T] {
        override val name = root.name
        override val attributes = root.attributes
        override val aggregates = Aggregate(this, aggregateName, leaf, multiplicity, mandatory) :: root.aggregates
        override val references = root.references
      }
    }
  }
  trait Referencing[T] extends Structure[T]
  object Referencing {
    def apply[T](root: Structure[T], referenceName: Name, referenced: Structure[_], multiplicity: Multiplicity = single, mandatory: Boolean = false) = {
      new Structure[T] {
        override val name = root.name
        override val attributes = root.attributes
        override val aggregates = root.aggregates
        override val references = Reference(referenceName, referenced, multiplicity, mandatory) :: root.references
      }
    }
  }
}
