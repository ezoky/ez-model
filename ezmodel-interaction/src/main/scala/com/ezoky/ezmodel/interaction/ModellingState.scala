package com.ezoky.ezmodel.interaction

import com.ezoky.ezmodel.core.Models
import com.ezoky.ezmodel.core.Models._
import com.ezoky.ezmodel.interaction.dsl.DSL._

/**
  * @author gweinbach on 21/02/2021
  * @since 0.2.0
  */
trait RootState {

  val models: ModelMap

  def ownsModel(model: Model): Boolean

  def mergeOrAddModel(model: Model): (RootState, Model)
}

trait ModelState {

  val currentModel: Option[Model]

  def resetCurrentModel: RootState

  def setCurrentModel(model: Model): ModelState

  def mergeOrAddDomain(domain: Domain): (ModelState, Domain)
}


trait DomainState {

  val currentDomain: Option[Domain]

  def resetCurrentDomain: RootState

  def setCurrentDomain(domain: Domain): DomainState

  def mergeOrAddUseCase(useCase: UseCase): (DomainState, UseCase)

  def mergeOrAddEntity(entity: Entity): (DomainState, Entity)

  def mergeOrAddInteractionDescriptor(interactionDescriptor: AnyInteractionDescriptor): (DomainState, AnyInteractionDescriptor)
}

trait UseCaseState {

  val currentUseCase: Option[UseCase]

  def resetCurrentUseCase: RootState

  def setCurrentUseCase(useCase: UseCase): UseCaseState

  def mergeOrAddInteraction(interaction: Interaction): (UseCaseState, Interaction)
}


trait EntityState {

  val currentEntity: Option[Entity]

  def resetCurrentEntity: RootState

  def setCurrentEntity(entity: Entity): EntityState

  def addAttribute(attribute: Attribute): EntityState

  def addAggregate(aggregate: Aggregate): EntityState

  def addReference(reference: Reference): EntityState
}

trait InteractionState {

  val currentInteraction: Option[Interaction]

  def resetCurrentInteraction: InteractionState

  def setCurrentInteraction(interaction: Interaction): InteractionState
}

trait InteractionDescriptorState {

  val currentInteractionDescriptor: Option[AnyInteractionDescriptor]

  def resetCurrentInteractionDescriptor: InteractionDescriptorState

  def setCurrentInteractionDescriptor(interactionDescriptor: AnyInteractionDescriptor): InteractionDescriptorState

}

case class ModellingState(models: ModelMap,
                          currentModel: Option[Model],
                          currentDomain: Option[Domain],
                          currentUseCase: Option[UseCase],
                          currentInteraction: Option[Interaction],
                          currentEntity: Option[Entity],
                          currentInteractionDescriptor: Option[AnyInteractionDescriptor])
  extends RootState
    with ModelState
    with DomainState
    with UseCaseState
    with InteractionState
    with EntityState
    with InteractionDescriptorState {

  // RootState
  override def ownsModel(model: Model): Boolean =
    models.owns(model)

  override def mergeOrAddModel(model: Model): (ModellingState, Model) = {
    val mergedState = copy(models = models.merge(model))
    val mergedModel = mergedState.models.getWithSameId(model)
    (mergedState, mergedModel.getOrElse(model)) // the orElse case is a bug
  }

  override def resetCurrentModel: ModellingState =
    copy(currentModel = None)

  override def setCurrentModel(model: Model): ModellingState = {
    if (currentModel == Some(model)) {
      this
    }
    else {
      val (augmentedModel, domainToSelect: Option[Domain]) =
        if (currentModel.isDefined) {
          (model, model.domains.some)
        }
        else {
          val modelWithDomain = currentDomain.fold(model)(model.mergeDomain(_))
          (modelWithDomain, model.domains.some.orElse(currentDomain))
        }

      val (stateWithMergedModel, mergedModel) = mergeOrAddModel(augmentedModel)

      val stateWithSelectedModel = stateWithMergedModel.copy(currentModel = Some(mergedModel))
      val stateWithSelectedDomain =
        domainToSelect.orElse(mergedModel.domains.some).fold(stateWithSelectedModel.resetCurrentDomain) {
          domain => stateWithSelectedModel.setCurrentDomain(domain)
        }
      stateWithSelectedDomain
    }
  }

  override def mergeOrAddDomain(domain: Domain): (ModellingState, Domain) =
    currentModel.fold((this, domain)) {
      model =>
        val mergedModel = model.mergeDomain(domain)
        val mergedDomain = mergedModel.domains.getWithSameId(domain)
        (setCurrentModel(mergedModel), mergedDomain.getOrElse(domain)) // the orElse case is a bug
    }


  // DomainState

  override def resetCurrentDomain: ModellingState =
    copy(currentDomain = None)

  override def setCurrentDomain(domain: Domain): ModellingState = {
    if (currentDomain == Some(domain)) {
      this
    }
    else {
      // if there is no current Domain, potentially existing UseCase and Entity are merged to Domain being set as current
      val (augmentedDomain, useCaseToSelect: Option[Models.UseCase], entityToSelect: Option[Models.Entity]) =
        if (currentDomain.isDefined) {
          (domain, domain.useCases.some, domain.entities.some)
        }
        else {
          val domainWithUseCase = currentUseCase.fold(domain)(domain.mergeUseCase(_))
          val domainWithEntity = currentEntity.fold(domainWithUseCase)(domainWithUseCase.mergeEntity(_))
          (domainWithEntity, domain.useCases.some.orElse(currentUseCase), domain.entities.some.orElse(currentEntity))
        }

      val (stateWithMergedDomain, mergedDomain) = mergeOrAddDomain(augmentedDomain)
      val stateWithSelectedDomain = stateWithMergedDomain.copy(currentDomain = Some(mergedDomain))
      val stateWithSelectedUseCase =
        useCaseToSelect.orElse(mergedDomain.useCases.some).fold(stateWithSelectedDomain.resetCurrentUseCase) {
          useCase => stateWithSelectedDomain.setCurrentUseCase(useCase)
        }
      val stateWithSelectedEntity = {
        entityToSelect.orElse(mergedDomain.entities.some).fold(stateWithSelectedUseCase.resetCurrentEntity) {
          entity => stateWithSelectedUseCase.setCurrentEntity(entity)
        }
      }
      stateWithSelectedEntity
    }
  }

  override def mergeOrAddUseCase(useCase: UseCase): (ModellingState, UseCase) =
    currentDomain.fold((this, useCase)) {
      domain =>
        val mergedDomain = domain.mergeUseCase(useCase)
        val mergedUseCase = mergedDomain.useCases.getWithSameId(useCase)
        (setCurrentDomain(mergedDomain), mergedUseCase.getOrElse(useCase)) // the orElse case is a bug
    }


  override def mergeOrAddEntity(entity: Entity): (ModellingState, Entity) =
    currentDomain.fold((this, entity)) {
      domain =>
        val mergedDomain = domain.mergeEntity(entity)
        val mergedEntity = mergedDomain.entities.getWithSameId(entity)
        (setCurrentDomain(mergedDomain), mergedEntity.getOrElse(entity)) // the orElse case is a bug
    }


  override def mergeOrAddInteractionDescriptor(interactionDescriptor: AnyInteractionDescriptor): (ModellingState, AnyInteractionDescriptor) =
    currentDomain.fold((this, interactionDescriptor)) {
      domain =>
        val mergedDomain = domain.mergeInteractionDescriptor(interactionDescriptor)
        val mergedInteractionDescriptor = mergedDomain.interactionDescriptors.getWithSameId(interactionDescriptor)
        (setCurrentDomain(mergedDomain), mergedInteractionDescriptor.getOrElse(interactionDescriptor)) // the orElse case is a bug
    }
    
    
    
  // UseCaseState

  override def resetCurrentUseCase: ModellingState =
    copy(currentUseCase = None)

  override def setCurrentUseCase(useCase: UseCase): ModellingState = 
    if (currentUseCase == Some(useCase)) {
      this
    }
    else {
      // if there is no current UseCase, potentially existing Interaction is merged to UseCase being set as current
      val (augmentedUseCase, interactionToSelect: Option[Models.Interaction]) =
        if (currentUseCase.isDefined) {
          (useCase, useCase.interaction)
        }
        else {
          val useCaseWithInteraction = currentInteraction.fold(useCase)(useCase.mergeInteraction(_))
          (useCaseWithInteraction, useCase.interaction.orElse(currentInteraction))
        }

      val (stateWithMergedUseCase, mergedUseCase) = mergeOrAddUseCase(augmentedUseCase)
      val stateWithSelectedUseCase = stateWithMergedUseCase.copy(currentUseCase = Some(mergedUseCase))
      val stateWithSelectedInteraction =
        interactionToSelect.orElse(mergedUseCase.interaction).fold(stateWithSelectedUseCase.resetCurrentInteraction) {
          interaction => stateWithSelectedUseCase.setCurrentInteraction(interaction)
        }
      stateWithSelectedInteraction
    }
  
  override def mergeOrAddInteraction(interaction: Interaction): (ModellingState, Interaction) =
    currentUseCase.fold((this, interaction)) {
      useCase =>
        val mergedUseCase = useCase.mergeInteraction(interaction)
        (setCurrentUseCase(mergedUseCase), mergedUseCase.interaction.getOrElse(interaction)) // the orElse case is a bug
    }
    
  // InteractionState

  override def resetCurrentInteraction: ModellingState =
    copy(currentInteraction = None)

  override def setCurrentInteraction(interaction: Interaction): ModellingState =
    if (currentInteraction == Some(interaction)) {
      this
    }
    else {
      val (stateWithMergedInteraction, mergedInteraction) = mergeOrAddInteraction(interaction)
      stateWithMergedInteraction.copy(currentInteraction = Some(mergedInteraction))
    }


  // EntityState

  override def resetCurrentEntity: ModellingState =
    copy(currentEntity = None)

  override def setCurrentEntity(entity: Entity): ModellingState = 
    if (currentEntity == Some(entity)) {
      this
    }
    else {
      val (stateWithMergedEntity, mergedEntity) = mergeOrAddEntity(entity)
      stateWithMergedEntity.copy(currentEntity = Some(mergedEntity))
    }

  override def addAttribute(attribute: Attribute): ModellingState = ???

  override def addAggregate(aggregate: Aggregate): ModellingState = ???

  override def addReference(reference: Reference): ModellingState = ???


  // InteractionDescription

  override def resetCurrentInteractionDescriptor: ModellingState =
    copy(currentInteractionDescriptor = None)

  override def setCurrentInteractionDescriptor(interactionDescriptor: AnyInteractionDescriptor): ModellingState =
    if (currentInteractionDescriptor == Some(interactionDescriptor)) {
      this
    }
    else {
      val (stateWithMergedInteractionDescriptor, mergedInteractionDescriptor) = mergeOrAddInteractionDescriptor(interactionDescriptor)
      stateWithMergedInteractionDescriptor.copy(currentInteractionDescriptor = Some(mergedInteractionDescriptor))
    }
}

object ModellingState {

  val Empty: ModellingState =
    ModellingState(
      models = ModelMap.empty,
      currentModel = None,
      currentDomain = None,
      currentUseCase = None,
      currentInteraction = None,
      currentEntity = None,
      currentInteractionDescriptor = None
    )
}
