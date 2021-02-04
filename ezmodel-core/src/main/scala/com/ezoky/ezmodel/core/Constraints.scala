package com.ezoky.ezmodel.core

object Constraints {

  import Entities._

  abstract case class ConstraintType(constraintType: String)

  object Pre extends ConstraintType("pre-condition")

  object Post extends ConstraintType("post-condition")

  type Constraints = Map[ConstraintType, List[EntityState]]

  val Empty: Constraints = Map.empty.withDefaultValue(List.empty)

  def apply(constrains: (ConstraintType, EntityState)*): Constraints =
    constrains.foldLeft(Empty){
      case (accConstraints, (constraintType, state)) =>
        accConstraints.get(constraintType) match {
          case None => accConstraints + (constraintType -> List(state))
          case Some(states) => accConstraints + (constraintType -> (state :: states))
        }
    }

  trait Constrained[T <: Constrained[T]] {

    val constraints: Constraints

    protected def constrain(constraintType: ConstraintType,
                            state: EntityState): Constraints =
      constraints.get(constraintType) match {
        case None => constraints + (constraintType -> List(state))
        case Some(states) => constraints + (constraintType -> (state :: states))
      }
  }

}