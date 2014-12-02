package com.ezmodel.core


object EzModel {

  import Atoms.Model
  import Domains._
  import UseCases._
  import Entities._

  import com.ezmodel.storage.Repository

  var domainRepository = Repository[Domain](Model)
  var useCaseRepository = Repository[UseCase](Model)
  var entityRepository = Repository[Entity](Model)
  var stateMachineRepository = Repository[StateMachine](Model)

  def reset() {
    domainRepository = Repository[Domain](Model)
    useCaseRepository = Repository[UseCase](Model)
    entityRepository = Repository[Entity](Model)
    stateMachineRepository = Repository[StateMachine](Model)
  }
}