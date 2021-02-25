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

  def addModel(model: Model): RootState
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
}

trait UseCaseState {

  val currentUseCase: Option[UseCase]

  def resetCurrentUseCase: RootState

  def setCurrentUseCase(useCase: UseCase): UseCaseState
}


trait EntityState {

  val currentEntity: Option[Entity]

  def resetCurrentEntity: RootState

  def setCurrentEntity(entity: Entity): EntityState

  def addAttribute(attribute: Attribute): EntityState

  def addAggregate(aggregate: Aggregate): EntityState

  def addReference(reference: Reference): EntityState
}

case class ModellingState(models: ModelMap,
                          currentModel: Option[Model],
                          currentDomain: Option[Domain],
                          currentUseCase: Option[UseCase],
                          currentEntity: Option[Entity])
  extends RootState
    with ModelState
    with DomainState
    with UseCaseState
    with EntityState {

  override def ownsModel(model: Model): Boolean =
    models.owns(model)

  override def addModel(model: Model): ModellingState =
    copy(models = models.add(model))

  override def resetCurrentModel: ModellingState =
    copy(currentModel = None)

  override def setCurrentModel(model: Model): ModellingState = {
    if (currentModel == Some(model)) {
      this
    }
    else {
      val modelWithDomain = currentDomain.fold(model)(domain => model.mergeDomain(domain))
      addModel(modelWithDomain).copy(currentModel = Some(modelWithDomain))
    }
  }

  override def mergeOrAddDomain(domain: Domain): (ModellingState, Domain) =
    currentModel.fold((this, domain)) {
      model =>
        val mergedModel = model.mergeDomain(domain)
        val mergedDomain = mergedModel.domains.getWithSameId(domain)
        (setCurrentModel(mergedModel), mergedDomain.getOrElse(domain)) // the orElse case is a bug
    }

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
           (domain, None, None)
        }
        else {
          val domainWithUseCase = currentUseCase.fold(domain)(domain.mergeUseCase(_))
          val domainWithEntity = currentEntity.fold(domainWithUseCase)(domainWithUseCase.mergeEntity(_))
          (domainWithEntity, currentUseCase, currentEntity)
        }

      val (stateWithMergedDomain, mergedDomain) = mergeOrAddDomain(augmentedDomain)
      val stateWithSelectedDomain = stateWithMergedDomain.copy(currentDomain = Some(mergedDomain))
      val stateWithSelectedUseCase =
        useCaseToSelect.orElse(augmentedDomain.useCases.some).fold(stateWithSelectedDomain.resetCurrentUseCase) {
          useCase => stateWithSelectedDomain.setCurrentUseCase(useCase)
        }
      val stateWithSelectedEntity = {
        entityToSelect.orElse(augmentedDomain.entities.some).fold(stateWithSelectedUseCase.resetCurrentEntity) {
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

  override def resetCurrentUseCase: ModellingState =
    copy(currentUseCase = None)

  override def setCurrentUseCase(useCase: UseCase): ModellingState = {
    if (currentUseCase == Some(useCase)) {
      this
    }
    else {
      val (stateWithMergedUseCase, mergedUseCase) = mergeOrAddUseCase(useCase)
      stateWithMergedUseCase.copy(currentUseCase = Some(mergedUseCase))
    }
  }

  override def resetCurrentEntity: ModellingState =
    copy(currentEntity = None)

  override def setCurrentEntity(entity: Entity): ModellingState = {
    if (currentEntity == Some(entity)) {
      this
    }
    else {
      val (stateWithMergedEntity, mergedEntity) = mergeOrAddEntity(entity)
      stateWithMergedEntity.copy(currentEntity = Some(mergedEntity))
    }
  }

  override def addAttribute(attribute: Attribute): ModellingState = ???

  override def addAggregate(aggregate: Aggregate): ModellingState = ???

  override def addReference(reference: Reference): ModellingState = ???
}

object ModellingState {

  val Empty: ModellingState =
    ModellingState(
      models = ModelMap.empty,
      currentModel = None,
      currentDomain = None,
      currentUseCase = None,
      currentEntity = None
    )
}
