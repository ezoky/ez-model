package com.ezoky.ezmodel.interaction.processing.eventsourcing

import com.ezoky.ezmodel.core.Models._
import com.ezoky.ezmodel.interaction.ModellingState
import com.ezoky.ezinterpreter.eventsourcing.{EventSourcing, Publishing}
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

  case class InteractionDefined(interaction: Interaction)

  case class EntityDefined(entity: Entity)

  case class InteractionDescribed(interactionDescriptor: AnyInteractionDescriptor)

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

  case class InteractionDefinedByModeller[MID, DID, UCID](inModel: Option[MID],
                                                          inDomain: Option[DID],
                                                          inUseCase: Option[UCID],
                                                          interaction: Interaction)

  case class EntityDefinedByModeller[MID, DID](inModel: Option[MID],
                                               inDomain: Option[DID],
                                               entity: Entity)

  case class InteractionDescribedByModeller[MID, DID](inModel: Option[MID],
                                                      inDomain: Option[DID],
                                                      interactionDescriptor: AnyInteractionDescriptor)

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

  implicit val defineInteractionInterpreter: EventSourcer[ModellingState, DefineAnInteraction, InteractionDefined, InteractionDefinedByModeller[_, _, _]] =
    EventSourcer.define(
      _ => command => InteractionDefined(command.interaction),
      (state, event: InteractionDefined) => state.setCurrentInteraction(event.interaction)
    )

  implicit val defineEntityInterpreter: EventSourcer[ModellingState, DefineAnEntity, EntityDefined, EntityDefinedByModeller[_, _]] =
    EventSourcer.define(
      _ => command => EntityDefined(command.entity),
      (state, event: EntityDefined) => state.setCurrentEntity(event.entity)
    )

  implicit val describeInteractionInterpreter: EventSourcer[ModellingState, DescribeAnInteraction, InteractionDescribed, InteractionDescribedByModeller[_, _]] =
    EventSourcer.define(
      _ => command => InteractionDescribed(command.interactionDescriptor),
      (state, event: InteractionDescribed) => state.setCurrentInteractionDescriptor(event.interactionDescriptor)
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

  implicit def defineDomainPublisher(implicit
                                     modelId: ModelId): Publisher[ModellingState, DefineADomain, DomainDefinedByModeller[modelId.IdType]] =
    Publisher.define(
      (state, event) =>
        Some(
          DomainDefinedByModeller(
            state.currentModel.map(m => modelId(m)),
            event.domain
          )
        )
    )

  implicit def defineUseCasePublisher(implicit
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

  implicit def defineInteractionPublisher(implicit
                                          modelId: ModelId,
                                          domainId: DomainId,
                                          useCaseId: UseCaseId): Publisher[ModellingState, DefineAnInteraction, InteractionDefinedByModeller[modelId.IdType, domainId.IdType, useCaseId.IdType]] =
    Publisher.define(
      (state, event) =>
        Some(
          InteractionDefinedByModeller(
            state.currentModel.map(m => modelId(m)),
            state.currentDomain.map(d => domainId(d)),
            state.currentUseCase.map(uc => useCaseId(uc)),
            event.interaction
          )
        )
    )

  implicit def defineEntityPublisher(implicit
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

  implicit def describeInteractionPublisher(implicit
                                            modelId: ModelId,
                                            domainId: DomainId): Publisher[ModellingState, DescribeAnInteraction, InteractionDescribedByModeller[modelId.IdType, domainId.IdType]] =
    Publisher.define(
      (state, event) =>
        Some(
          InteractionDescribedByModeller(
            state.currentModel.map(m => modelId(m)),
            state.currentDomain.map(d => domainId(d)),
            event.interactionDescriptor
          )
        )
    )
}