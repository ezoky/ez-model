package com.ezoky.ezmodel.core

object Domains {

  import Atoms._
  import UseCases._
  import Structures._
  
  import com.ezoky.ezmodel.storage.EventStore

  case class Domain(name: Name,useCases: List[UseCase] = List(),entities: List[Entity] = List()) {

    EventStore(Model).store(this)

    def this(domain: Domain) {
      this(domain.name)
    }

    def this() {
      this(DefaultName)
    }

    def useCase(uc: UseCase) = {
      copy(useCases = uc :: useCases)
    }

    def entity(ent: Entity) = {
      copy(entities = ent :: entities)
    }
  }
}