package com.ezoky.ezmodel.storage

import com.ezoky.ezmodel.core.Atoms.Model
import com.ezoky.ezmodel.core.Domains.Domain
import com.ezoky.ezmodel.core.Entities.{Entity, StateMachine}
import com.ezoky.ezmodel.core.UseCases.UseCase

import java.util.UUID

object EzModel {

  def reset(): EzModel = EzModel()
}

case class EzModel(domainRepository: Repository[Domain] = Repository[Domain](Model),
                   useCaseRepository: Repository[UseCase] = Repository[UseCase](Model),
                   entityRepository: Repository[Entity] = Repository[Entity](Model),
                   stateMachineRepository: Repository[StateMachine] = Repository[StateMachine](Model)) {

  lazy val id: UUID = UUID.randomUUID()
}