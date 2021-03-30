package com.ezoky.ezinterpreter.eventsourcing

import com.ezoky.ezinterpreter.Interpreting
import shapeless._

/**
  * @author gweinbach on 09/03/2021
  * @since 0.2.0
  */
trait EventSourcing
  extends Interpreting
    with Projecting
    with Publishing {

  /**
    * This combines interpretation of Command into an Event and projection of the Event on the State in order to
    * change State. There might even be a Published Event during the process
    */
  trait EventSourcer[StateType, -CommandType, PersistentEventType, PublishedEventType] {

    val interpreter: Interpreter[StateType, CommandType, PersistentEventType]
    val projector: Projector[StateType, PersistentEventType]
    val publisher: Publisher[StateType, PersistentEventType, PublishedEventType]
  }

  object EventSourcer {

    /**
      * Never changes state but will still persist and publish!
      */
    def identity[StateType, CommandType, PersistentEvtType, PublishedEvtType](interpretation: StateType => CommandType => PersistentEvtType,
                                                                              publication: (StateType, PersistentEvtType) => Option[PublishedEvtType] = (_: StateType, _: PersistentEvtType) => None): EventSourcer[StateType, CommandType, PersistentEvtType, PublishedEvtType] =
      define(interpretation, (s: StateType, _: PersistentEvtType) => s, publication)

    def define[StateType, CommandType, PersistentEvtType, PublishedEvtType](interpretation: StateType => CommandType => PersistentEvtType,
                                                                            projection: (StateType, PersistentEvtType) => StateType,
                                                                            publication: (StateType, PersistentEvtType) => Option[PublishedEvtType] = (_: StateType, _: PersistentEvtType) => None): EventSourcer[StateType, CommandType, PersistentEvtType, PublishedEvtType] =
      new EventSourcer[StateType, CommandType, PersistentEvtType, PublishedEvtType] {

        override val interpreter: Interpreter[StateType, CommandType, PersistentEvtType] =
          Interpreter.define(interpretation)
        override val projector: Projector[StateType, PersistentEvtType] =
          Projector.define(projection)
        override val publisher: Publisher[StateType, PersistentEvtType, PublishedEvtType] =
          Publisher.define(publication)
      }


    /**
      * Provided for test purposes. Behaviour has nothing to do with actual Event Sourcing: real life would persist events.
      */
    def apply[StateType, CommandType, PersistentEvtType, PublishedEvtType](state: StateType,
                                                                           statement: Statement[CommandType])
                                                                          (implicit
                                                                           eventSourcing: EventSourcer[StateType, CommandType, PersistentEvtType, PublishedEvtType]): (PersistentEvtType, StateType, Option[PublishedEvtType]) = {
      val persistedEvent = eventSourcing.interpreter(state, statement)
      val publishedEvent = eventSourcing.publisher.publish(state, persistedEvent)
      val resultingState = eventSourcing.projector.project(state, persistedEvent)
      (persistedEvent, resultingState, publishedEvent)
    }
  }


  implicit def hNilEventSourcingInterpreter[StateType]: EventSourcer[StateType, HNil, HNil, HNil] =
    EventSourcer.identity(
      _ => _ => HNil
    )

  implicit def hListSingletonEventSourcing[StateType, HS, HE, HP](implicit
                                                                  eventSourcingH: EventSourcer[StateType, HS, HE, HP]): EventSourcer[StateType, HS :: HNil, HE :: HNil, Option[HP] :: HNil] = {
    new EventSourcer[StateType, HS :: HNil, HE :: HNil, Option[HP] :: HNil] {

      override val interpreter: Interpreter[StateType, HS :: HNil, HE :: HNil] =
        Interpreter.define { state =>
          statement =>
            eventSourcingH.interpreter.interpret(state, statement.head) :: HNil
        }

      override val projector: Projector[StateType, HE :: HNil] =
        Projector.define { (state, event) =>
          eventSourcingH.projector.project(state, event.head)
        }

      override val publisher: Publisher[StateType, HE :: HNil, Option[HP] :: HNil] =
        Publisher.define { (state, persistedEvent) =>
          val optHEvent = eventSourcingH.publisher.publish(state, persistedEvent.head)
          Some(optHEvent :: HNil)
        }
    }
  }

  implicit def hListEventSourcingInterpreter[StateType, HS, HHS, TStatementType <: HList, HHE, HE, TEventType <: HList, HP, HHP, TPublishedEvent <: HList](implicit
                                                                                                                                                           eventSourcingH: EventSourcer[StateType, HS, HE, HP],
                                                                                                                                                           eventSourcingT: EventSourcer[StateType, HHS :: TStatementType, HHE :: TEventType, HHP :: TPublishedEvent]): EventSourcer[StateType, HS :: HHS :: TStatementType, HE :: HHE :: TEventType, Option[HP] :: HHP :: TPublishedEvent] = {
    new EventSourcer[StateType, HS :: HHS :: TStatementType, HE :: HHE :: TEventType, Option[HP] :: HHP :: TPublishedEvent] {

      /**
        * `eventSourcingH.projector.project()` function must be pure as it is called to compute
        * input state of `eventSourcingT.interpreter.interpret()` function.
        */
      override val interpreter: Interpreter[StateType, HS :: HHS :: TStatementType, HE :: HHE :: TEventType] =
        Interpreter.define { state =>
          statement =>
            val hEvent: HE = eventSourcingH.interpreter.interpret(state, statement.head)
            hEvent :: eventSourcingT.interpreter
              .interpret(eventSourcingH.projector.project(state, hEvent), statement.tail)
        }

      override val projector: Projector[StateType, HE :: HHE :: TEventType] =
        Projector.define { (state, event) =>
          eventSourcingT.projector.project(eventSourcingH.projector.project(state, event.head), event.tail)
        }

      override val publisher: Publisher[StateType, HE :: HHE :: TEventType, Option[HP] :: HHP :: TPublishedEvent] =
        Publisher.define { (state, persistedEvent) =>
          val optHEvent = eventSourcingH.publisher.publish(state, persistedEvent.head)
          val tEvent = eventSourcingT
            .publisher
            .publish(eventSourcingH.projector.project(state, persistedEvent.head), persistedEvent.tail)
            .get //
          Some(optHEvent :: tEvent)
        }
    }
  }
}
