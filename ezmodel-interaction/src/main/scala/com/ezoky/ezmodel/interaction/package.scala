package com.ezoky.ezmodel

import com.ezoky.ezmodel
import com.ezoky.ezmodel.core.Models._

/**
  * @author gweinbach on 04/02/2021
  * @since 0.2.0 */
package object interaction {



  abstract class InteractiveShell[S]() {
    var currentState: S
  }



  type Models = Map[Name, Model]

  implicit class ModelsHelper(models: Models) {
    def add(model: Model): Models =
      models + (model.name -> model)
  }

  trait RootState {

    val models: Models

    val currentModel: Model

    def addModel(model: Model): RootState

    def updateModel(model: Model): RootState
  }

  trait ModelState {

    val currentModel: Model

    def setCurrentModel(model: Model): ModelState

    def addDomain(domain: Domain): ModelState

    def updateDomain(domain: Domain): ModelState
  }

  trait DomainState {

    val currentDomain: Domain

    def setCurrentDomain(domain: Domain): DomainState

    def addUseCase(useCase: UseCase): DomainState

    def updateUseCase(useCase: UseCase): DomainState

    def addEntity(entity: Entity): DomainState

    def updateEntity(entity: Entity): DomainState
  }

  trait UseCaseState {

    val currentUseCase: UseCase

    def setCurrentUseCase(useCase: UseCase): UseCaseState
  }


  trait EntityState {

    val currentEntity: Entity

    def setCurrentEntity(entity: Entity): EntityState

    def addAttribute(attribute: Attribute): EntityState

    def addAggregate(aggregate: Aggregate): EntityState

    def addReference(reference: Reference): EntityState
  }

  case class ModellingState(models: Models,
                            currentModel: Model,
                            currentDomain: Domain,
                            currentUseCase: UseCase,
                            currentEntity: Entity)
    extends RootState
      with ModelState
      with DomainState
      with UseCaseState
      with EntityState {

    override def addModel(model: Model): ModellingState =
      copy(models = models.add(model))

    override def updateModel(model: Model): ModellingState = ???

    override def setCurrentModel(model: Model): ModellingState =
      addModel(model).copy(currentModel = model)

    override def addDomain(domain: Domain): ModellingState = ???

    override def setCurrentDomain(domain: Domain): ModellingState =
      addDomain(domain).copy(currentDomain = domain)

    override def addUseCase(useCase: UseCase): ModellingState = ???

    override def addEntity(entity: Entity): ModellingState = ???

    override def setCurrentUseCase(useCase: UseCase): ModellingState =
      addUseCase(useCase).copy(currentUseCase = useCase)

    override def setCurrentEntity(entity: Entity): ModellingState =
      addEntity(entity).copy(currentEntity = entity)

    override def addAttribute(attribute: Attribute): ModellingState = ???

    override def addAggregate(aggregate: Aggregate): ModellingState = ???

    override def addReference(reference: Reference): ModellingState = ???

    override def updateDomain(domain: ezmodel.core.Models.Domain): ModelState = ???

    override def updateUseCase(useCase: ezmodel.core.Models.UseCase): DomainState = ???

    override def updateEntity(entity: ezmodel.core.Models.Entity): DomainState = ???
  }


  case object CurrentModel {
    def contains[T](something: T): Boolean =
      false
  }

}
