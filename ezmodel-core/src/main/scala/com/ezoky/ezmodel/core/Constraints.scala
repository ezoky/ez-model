package com.ezoky.ezmodel.core

object Constraints {

  import Entities._

  abstract case class ConstraintType(constraintType: String)

  object Pre extends ConstraintType("pre-condition")

  object Post extends ConstraintType("post-condition")

  val Empty: Map[ConstraintType, List[EntityState]] = Map.empty.withDefaultValue(List.empty)

  trait Constrained[T <: Constrained[T]] {

    val constraints: Map[ConstraintType, List[EntityState]]

    protected def constrain(constrained: T,
                            constraintType: ConstraintType,
                            state: EntityState): Map[ConstraintType, List[EntityState]] =
      constrained.constraints.get(constraintType) match {
        case None => constrained.constraints + (constraintType -> List(state))
        case Some(states) => constrained.constraints + (constraintType -> (state :: states))
      }
  }

}