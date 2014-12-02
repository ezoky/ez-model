package com.ezmodel.core

object Domains {

  import com.ezmodel.core.Atoms._
  import com.ezmodel.core.Entities._
  import com.ezmodel.core.UseCases._
  import com.ezmodel.storage.EventStore

  case class Domain(name: Name, useCases: List[UseCase] = List(), entities: List[Entity] = List()) {

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