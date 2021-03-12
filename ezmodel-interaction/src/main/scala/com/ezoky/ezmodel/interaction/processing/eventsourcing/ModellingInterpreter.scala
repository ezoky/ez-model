package com.ezoky.ezmodel.interaction.processing.eventsourcing

import com.ezoky.ezmodel.core.Models._
import com.ezoky.ezmodel.interaction.ModellingState
import com.ezoky.ezmodel.interaction.interpreter.eventsourcing.{EventSourcing, Publishing}
import com.ezoky.ezmodel.interaction.processing.ModellingSyntax

/**
  * @author gweinbach on 07/03/2021
  * @since 0.2.0
  */
trait ModellingInterpreter
  extends ModellingEvents
    with PublishedModellingEvents
    with EventSourcingInterpreters

/**
  * These ar "Event sourcing events" i.e. Events that are the result of commands aimed to be persisted
  * and then projected on state.
  */
trait ModellingEvents {

  case class ModelDefined(model: Model)

  case class DomainDefined(domain: Domain)

  case class UseCaseDefined(useCase: UseCase)

  case class EntityDefined(entity: Entity)

}

/**
  * These should be published when Modelling Events are projected (or produced ?)
  * TODO: published events should be decorated with TenantId and UserId
  * Something like that is simple:
  * {{{
  *
  *type TenantId = String
  *type UserId = String
  *
  *case class ModelDefinedByModeller(tenantId: TenantId,
  *modellerId: UserId,
  *model: Model)
  *
  *...
  * }}}
  */
trait PublishedModellingEvents {

  case class ModelDefinedByModeller(model: Model)

  case class DomainDefinedByModeller[MID](inModel: Option[MID],
                                          domain: Domain)

  case class UseCaseDefinedByModeller[MID, DID](inModel: Option[MID],
                                                inDomain: Option[DID],
                                                useCase: UseCase)

  case class EntityDefinedByModeller[MID, DID](inModel: Option[MID],
                                               inDomain: Option[DID],
                                               entity: Entity)

}

trait EventSourcingInterpreters
  extends EventSourcing
    with ModellingSyntax
    with ModellingEvents
    with PublishedModellingEvents {

  implicit val defineModelInterpreter: EventSourcer[ModellingState, DefineAModel, ModelDefined, ModelDefinedByModeller] =
    EventSourcer.define(
      _ => command => ModelDefined(command.model),
      (state, event: ModelDefined) => state.setCurrentModel(event.model)
    )

  implicit val defineDomainInterpreter: EventSourcer[ModellingState, DefineADomain, DomainDefined, DomainDefinedByModeller[_]] =
    EventSourcer.define(
      _ => command => DomainDefined(command.domain),
      (state, event: DomainDefined) => state.setCurrentDomain(event.domain)
    )

  implicit val defineUseCaseInterpreter: EventSourcer[ModellingState, DefineAUseCase, UseCaseDefined, UseCaseDefinedByModeller[_, _]] =
    EventSourcer.define(
      _ => command => UseCaseDefined(command.useCase),
      (state, event: UseCaseDefined) => state.setCurrentUseCase(event.useCase)
    )

  implicit def defineEntityInterpreter: EventSourcer[ModellingState, DefineAnEntity, EntityDefined, EntityDefinedByModeller[_, _]] =
    EventSourcer.define(
      _ => command => EntityDefined(command.entity),
      (state, event: EntityDefined) => state.setCurrentEntity(event.entity)
    )

}

trait Publishers
  extends Publishing
    with ModellingSyntax
    with PublishedModellingEvents {

  implicit def defineModelPublisher: Publisher[ModellingState, DefineAModel, ModelDefinedByModeller] =
    Publisher.define(
      (_, event) => Some(ModelDefinedByModeller(event.model))
    )

  implicit def defineDomainPublisher[MId <: ModelId](implicit
                                                     modelId: MId): Publisher[ModellingState, DefineADomain, DomainDefinedByModeller[modelId.IdType]] =
    Publisher.define(
      (state, event) =>
        Some(
          DomainDefinedByModeller(
            state.currentModel.map(m => modelId(m)),
            event.domain
          )
        )
    )

  implicit def defineUseCasePublisher[MId <: ModelId, DId <: DomainId](implicit
                                                                       modelId: ModelId,
                                                                       domainId: DomainId): Publisher[ModellingState, DefineAUseCase, UseCaseDefinedByModeller[modelId.IdType, domainId.IdType]] =
    Publisher.define(
      (state, event) =>
        Some(
          UseCaseDefinedByModeller(
            state.currentModel.map(m => modelId(m)),
            state.currentDomain.map(d => domainId(d)),
            event.useCase
          )
        )
    )

  implicit def defineEntityPublisher[MId <: ModelId, DId <: DomainId](implicit
                                                                      modelId: ModelId,
                                                                      domainId: DomainId): Publisher[ModellingState, DefineAnEntity, EntityDefinedByModeller[modelId.IdType, domainId.IdType]] =
    Publisher.define(
      (state, event) =>
        Some(
          EntityDefinedByModeller(
            state.currentModel.map(m => modelId(m)),
            state.currentDomain.map(d => domainId(d)),
            event.entity
          )
        )
    )
}