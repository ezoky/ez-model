package com.ezoky.ezmodel.core

object Constrains {
  
  import Entities._
  
  abstract case class ConstraintType(constraintType: String)
  object Pre extends ConstraintType("pre-condition")
  object Post extends ConstraintType("post-condition")

  trait Constrained {

    val constraints: Map[ConstraintType, List[EntityState]]

    def constrain(constrained: Any, constraintType: ConstraintType, state: EntityState) = {
      assume(constrained != null)

      try {
        val actuallyConstrained = constrained.asInstanceOf[Constrained]
        actuallyConstrained.constraints.get(constraintType) match {
          case None => actuallyConstrained.constraints + (constraintType -> List(state))
          case Some(states) => actuallyConstrained.constraints + (constraintType -> (state :: states))
        }
      }
      catch {
        case _: ClassCastException => Map(constraintType -> List(state))
      }
    }
  }
}