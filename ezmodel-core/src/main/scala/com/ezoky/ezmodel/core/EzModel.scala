package com.ezoky.ezmodel.core


object EzModel {

  import Atoms.Model
  import Domains._
  import UseCases._
  import Structures._

  import com.ezoky.ezmodel.storage.Repository
  
  val domainRepository = Repository[Domain](Model)
  val useCaseRepository = Repository[UseCase](Model)
  val entityRepository = Repository[Structure[Entity]](Model)
  val stateMachineRepository = Repository[StateMachine](Model)
}