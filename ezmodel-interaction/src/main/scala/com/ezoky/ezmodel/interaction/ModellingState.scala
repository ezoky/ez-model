package com.ezoky.ezmodel.interaction

import com.ezoky.ezmodel.core.Models._
import com.ezoky.ezmodel.interaction.dsl.DSL._

/**
  * @author gweinbach on 21/02/2021
  * @since 0.2.0
  */
trait RootState {

  val models: ModelMap

  def addModel(model: Model): RootState

  def updateModel(model: Model): RootState
}

trait ModelState {

  val currentModel: Option[Model]

  def resetCurrentModel: RootState

  def setCurrentModel(model: Model): ModelState

  def addDomain(domain: Domain): ModelState

  def updateDomain(domain: Domain): ModelState
}


trait DomainState {

  val currentDomain: Option[Domain]

  def resetCurrentDomain: RootState

  def setCurrentDomain(domain: Domain): DomainState

  def addUseCase(useCase: UseCase): DomainState

  def updateUseCase(useCase: UseCase): DomainState

  def addEntity(entity: Entity): DomainState

  def updateEntity(entity: Entity): DomainState
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

  override def addModel(model: Model): ModellingState =
    copy(models = models.add(model))

  override def updateModel(model: Model): ModellingState = ???

  override def resetCurrentModel: ModellingState =
    copy(currentModel = None)

  override def setCurrentModel(model: Model): ModellingState =
    addModel(model).copy(currentModel = Some(model))

  override def addDomain(domain: Domain): ModellingState =
    currentModel.fold(this)(model => setCurrentModel(model.withDomain(domain)))

  override def resetCurrentDomain: ModellingState =
    copy(currentDomain = None)

  override def setCurrentDomain(domain: Domain): ModellingState = {
    val withDomain = addDomain(domain).copy(currentDomain = Some(domain))
    val withUseCase =
      domain.useCases.headOption.fold(withDomain.resetCurrentUseCase) {
        case (_, useCase) => withDomain.setCurrentUseCase(useCase)
      }
    val withEntity = {
      domain.entities.headOption.fold(withUseCase.resetCurrentEntity) {
        case (_, entity) => withUseCase.setCurrentEntity(entity)
      }
    }
    withEntity
  }

  override def addUseCase(useCase: UseCase): ModellingState = ???

  override def addEntity(entity: Entity): ModellingState = ???

  override def resetCurrentUseCase: ModellingState =
    copy(currentUseCase = None)

  override def setCurrentUseCase(useCase: UseCase): ModellingState =
//    addUseCase(useCase).copy(currentUseCase = Some(useCase))
    copy(currentUseCase = Some(useCase))

  override def resetCurrentEntity: ModellingState =
    copy(currentEntity = None)

  override def setCurrentEntity(entity: Entity): ModellingState =
//    addEntity(entity).copy(currentEntity = Some(entity))
    copy(currentEntity = Some(entity))

  override def addAttribute(attribute: Attribute): ModellingState = ???

  override def addAggregate(aggregate: Aggregate): ModellingState = ???

  override def addReference(reference: Reference): ModellingState = ???

  override def updateDomain(domain: Domain): ModelState = ???

  override def updateUseCase(useCase: UseCase): ModellingState = ???

  override def updateEntity(entity: Entity): ModellingState = ???
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
