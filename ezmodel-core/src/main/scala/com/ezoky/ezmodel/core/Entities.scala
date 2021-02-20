package com.ezoky.ezmodel.core

private[core] trait Entities
  extends Atoms {

  case class StateName(qualifier: Qualifier)

  object UnknownStateName extends StateName(Qualifier("<unknown>"))

  object InitialStateName extends StateName(Qualifier("<initial>"))


  case class StateMachine(entity: Entity,
                          states: Map[StateName, EntityState] = Map.empty) {

    def state(entityState: EntityState) =
      copy(states = states + (entityState.state -> entityState))
  }

  case class EntityState(entity: Entity,
                         state: StateName) {
    override def toString: String =
      s"${entity} is ${state}"
  }

  class InitialEntityState(entity: Entity)
    extends EntityState(entity, InitialStateName)


  // Multiplicity
  abstract case class Multiplicity(multiplicity: String)

  object single extends Multiplicity("single")

  object multiple extends Multiplicity("multiple")

  class Exactly(value: Int) extends Multiplicity(s"$value")

  class Range(min: Int, max: Int) extends Multiplicity(s"$min-$max") {
    assume(min >= 0 && min <= max)
  }


  // Entity
  case class Attribute(name: Name, multiplicity: Multiplicity, mandatory: Boolean)

  case class Reference(name: Name, referenced: Entity, multiplicity: Multiplicity, mandatory: Boolean)

  case class Aggregate(root: Entity, name: Name, leaf: Entity, multiplicity: Multiplicity, mandatory: Boolean)

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
        aggregated = aggregated + (aggregateName -> Aggregate(this, aggregateName, leaf, multiplicity, mandatory))
      )

    def withReference(referenceName: Name,
                      referencedEntity: Entity,
                      multiplicity: Multiplicity = single,
                      mandatory: Boolean = false): Entity =
      copy(
        referenced = referenced + (referenceName -> Reference(referenceName, referencedEntity, multiplicity, mandatory))
      )

    override def toString =
      s"${getClass.getSimpleName}($name)"

    override def equals(other: Any): Boolean =
      other match {
        case that: Entity => that.name == this.name
        case _ => false
      }

    override def hashCode: Int =
      41 * name.hashCode
  }

}