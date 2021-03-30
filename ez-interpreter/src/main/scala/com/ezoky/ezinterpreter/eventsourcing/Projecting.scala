package com.ezoky.ezinterpreter.eventsourcing

/**
  * @author gweinbach on 07/03/2021
  * @since 0.2.0
  */
trait Projecting {

  trait Projector[StateType, -EventType] {

    def project(state: StateType,
                event: EventType): StateType

  }

  object Projector {

    def identity[StateType]: Projector[StateType, Any] =
      define((state, _) => state)

    def define[StateType, EventType](projecting: (StateType, EventType) => StateType): Projector[StateType, EventType] =
      (state, event) => projecting(state, event)

    def apply[StateType, EventType](state: StateType,
                                    event: EventType)
                                   (implicit projector: Projector[StateType, EventType]): StateType =
      projector.project(state, event)
  }
}