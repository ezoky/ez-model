package com.ezoky.ezinterpreter.eventsourcing

import shapeless._

/**
  * @author gweinbach on 07/03/2021
  * @since 0.2.0
  */
trait Publishing
  extends Projecting {

  trait Publisher[-StateType, -PersistentEventType, +PublishedEventType] {

    def publish(state: StateType,
                persistentEvent: PersistentEventType): Option[PublishedEventType]

    def apply(state: StateType,
              persistentEvent: PersistentEventType): Option[PublishedEventType] =
      publish(state, persistentEvent)

  }

  object Publisher {

    def OfNothing[PublishedEventType]: Publisher[Any, Any, PublishedEventType] =
      define[Any, Any, Nothing]((_, _) => None)

    def define[StateType, PersistentEventType, PublishedEventType](publication: (StateType, PersistentEventType) => Option[PublishedEventType]): Publisher[StateType, PersistentEventType, PublishedEventType] =
      (state, persistentEvent) => publication(state, persistentEvent)

    def apply[StateType, PersistentEventType, PublishedEventType](state: StateType,
                                                                  persistentEvent: PersistentEventType)
                                                                 (implicit
                                                                  publisher: Publisher[StateType, PersistentEventType, PublishedEventType]): Option[PublishedEventType] =
      publisher(state, persistentEvent)
  }


  implicit val hNilPublisher: Publisher[Any, Any, HNil] =
    Publisher.OfNothing

  implicit def hListSingletonPublisher[StateType, HE, HP](implicit
                                                          publisherH: Publisher[StateType, HE, HP]): Publisher[StateType, HE :: HNil, Option[HP] :: HNil] = {
    Publisher.define[StateType, HE :: HNil, Option[HP] :: HNil](
      (state, persistentEvent) => {
        val optHEvent = publisherH.publish(state, persistentEvent.head)
        Some(optHEvent :: HNil)
      }
    )
  }

  implicit def hListPublisher[StateType, HE, TPersistentEventType <: HList, HP, TPublishedEventType <: HList](implicit
                                                                                                              publisherH: Publisher[StateType, HE, HP],
                                                                                                              publisherT: Publisher[StateType, TPersistentEventType, TPublishedEventType],
                                                                                                              projectorH: Projector[StateType, HE]): Publisher[StateType, HE :: TPersistentEventType, Option[HP] :: TPublishedEventType] = {
    Publisher.define[StateType, HE :: TPersistentEventType, Option[HP] :: TPublishedEventType](
      (state, persistentEvent) => {
        val optHEvent = publisherH.publish(state, persistentEvent.head)
        val tEvent =  publisherT.publish(projectorH.project(state, persistentEvent.head), persistentEvent.tail).get //
        Some(optHEvent :: tEvent)
      }
    )
  }
}