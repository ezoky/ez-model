package com.ezoky.ezmodel.core

import com.ezoky.commons.NaturalIds

private[core] trait Constraints
  extends Entities
    with NaturalIds {

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
          accConstraints + (constraintType -> accConstraints(constraintType).add(state))
      }
  }

  trait Constrained[T <: Constrained[T]] {

    val constraints: Constraints

    protected def constrain(constraintType: ConstraintType,
                            state: EntityState)
                           (implicit
                            entityStateId: EntityStateId): Constraints =
      constraints + (constraintType -> constraints(constraintType).add(state))
  }
}