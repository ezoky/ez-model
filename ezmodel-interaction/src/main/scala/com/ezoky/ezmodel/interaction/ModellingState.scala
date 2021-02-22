package com.ezoky.ezmodel.interaction

import com.ezoky.ezmodel.core.Models._
import com.ezoky.ezmodel.interaction.Models._

/**
  * @author gweinbach on 21/02/2021
  * @since 0.2.0
  */
trait RootState {

  val models: Models

  def addModel(model: Model): RootState

  def updateModel(model: Model): RootState
}

object Models {

  type Models = Map[Name, Model]

  val Empty: Models = Map.empty

  implicit class ModelsHelper(models: Models) {
    def add(model: Model): Models =
      models + (model.name -> model)
  }

}

trait ModelState {

  val currentModel: Option[Model]

  def setCurrentModel(model: Model): ModelState

  def addDomain(domain: Domain): ModelState

  def updateDomain(domain: Domain): ModelState
}


trait DomainState {

  val currentDomain: Option[Domain]

  def setCurrentDomain(domain: Domain): DomainState

  def addUseCase(useCase: UseCase): DomainState

  def updateUseCase(useCase: UseCase): DomainState

  def addEntity(entity: Entity): DomainState

  def updateEntity(entity: Entity): DomainState
}

trait UseCaseState {

  val currentUseCase: Option[UseCase]

  def setCurrentUseCase(useCase: UseCase): UseCaseState
}


trait EntityState {

  val currentEntity: Option[Entity]

  def setCurrentEntity(entity: Entity): EntityState

  def addAttribute(attribute: Attribute): EntityState

  def addAggregate(aggregate: Aggregate): EntityState

  def addReference(reference: Reference): EntityState
}

case class ModellingState(models: Models,
                          currentModel: Option[Model],
                          currentDomain: Option[Domain],
                          currentUseCase: Option[UseCase],
                          currentEntity: Option[Entity])
  extends RootState
    with ModelState
    with DomainState
    with UseCaseState
    with EntityState {

  override def addModel(model: Model): ModellingState =
    copy(models = models.add(model))

  override def updateModel(model: Model): ModellingState = ???

  override def setCurrentModel(model: Model): ModellingState =
//    addModel(model).copy(currentModel = Some(model))
    copy(currentModel = Some(model))

  override def addDomain(domain: Domain): ModellingState = ???

  override def setCurrentDomain(domain: Domain): ModellingState =
//    addDomain(domain).copy(currentDomain = Some(domain))
    copy(currentDomain = Some(domain))

  override def addUseCase(useCase: UseCase): ModellingState = ???

  override def addEntity(entity: Entity): ModellingState = ???

  override def setCurrentUseCase(useCase: UseCase): ModellingState =
//    addUseCase(useCase).copy(currentUseCase = Some(useCase))
    copy(currentUseCase = Some(useCase))

  override def setCurrentEntity(entity: Entity): ModellingState =
//    addEntity(entity).copy(currentEntity = Some(entity))
    copy(currentEntity = Some(entity))

  override def addAttribute(attribute: Attribute): ModellingState = ???

  override def addAggregate(aggregate: Aggregate): ModellingState = ???

  override def addReference(reference: Reference): ModellingState = ???

  override def updateDomain(domain: Domain): ModelState = ???

  override def updateUseCase(useCase: UseCase): DomainState = ???

  override def updateEntity(entity: Entity): DomainState = ???
}

object ModellingState {

  val Empty: ModellingState =
    ModellingState(
      models = Models.Empty,
      currentModel = None,
      currentDomain = None,
      currentUseCase = None,
      currentEntity = None
    )
}
