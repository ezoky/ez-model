package com.ezoky.ezmodel.core

import com.ezoky.ezmodel.core.NaturalId.NaturalMap

private[core] trait Constraints
  extends Entities {

  abstract case class ConstraintType(constraintType: String)

  object Pre extends ConstraintType("pre-condition")

  object Post extends ConstraintType("post-condition")

  type Constraints = Map[ConstraintType, EntityStateMap]

  object Constraints {
    
    val empty: Constraints = Map.empty.withDefaultValue(EntityStateMap.empty)

    def apply(constrains: (ConstraintType, EntityState)*)
             (implicit
             entityStateId: EntityStateId): Constraints =
      constrains.foldLeft(empty) {
        case (accConstraints, (constraintType, state)) =>
          accConstraints.get(constraintType) match {
            case None => accConstraints + (constraintType -> EntityStateMap(state))
            case Some(states) => accConstraints + (constraintType -> states.add(state))
          }
      }
  }

  trait Constrained[T <: Constrained[T]] {

    val constraints: Constraints

    protected def constrain(constraintType: ConstraintType,
                            state: EntityState)
                           (implicit
                            entityStateId: EntityStateId): Constraints =
      constraints.get(constraintType) match {
        case None => constraints + (constraintType -> EntityStateMap(state))
        case Some(states) => constraints + (constraintType -> states.add(state))
      }
  }
}