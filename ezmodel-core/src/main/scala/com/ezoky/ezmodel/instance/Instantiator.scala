package com.ezoky.ezmodel.instance

object Instantiator {

  //  def instantiate(entity: Entity): StateMachine = {
  //    StateMachine(InstanceState(entity), None)
  //  }
  //
  //  case class InstanceState(previousState: InstanceState)
  //  object InstanceState {
  //    def apply(entity: Entity) = {
  //      //InstanceState(entity.intialState)
  //    }
  //  }
  //
  //  case class StateMachine(stateOption: InstanceState, previousState: Option[StateMachine]) {
  //
  //    val history: List[StateMachine] = previousState match {
  //      case None => List(this)
  //      case Some(stateMachine) => this :: stateMachine.history
  //    }
  //  }

}