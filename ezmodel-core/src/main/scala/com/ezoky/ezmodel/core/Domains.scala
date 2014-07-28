package com.ezoky.ezmodel.core

object Domains {

  import Atoms._
  import UseCases._
  import Structures._
  
  import com.ezoky.ezmodel.storage.EventStore

  case class Domain(name: Name) {

    val useCases: List[UseCase] = List()
    val entities: List[Entity] = List()

    EventStore(Model).store(this)

    def this(domain: Domain) {
      this(domain.name)
    }

    def this() {
      this(DefaultName)
    }

    def useCase(uc: UseCase) = {
      val parentUseCases = useCases
      val parentEntities = entities
      new Domain(name) {
        override val useCases = uc :: parentUseCases
        override val entities = parentEntities
      }
    }

    def entity(ent: Entity) = {
      val parentUseCases = useCases
      val parentEntities = entities
      new Domain(name) {
        override val useCases = parentUseCases
        override val entities = ent :: parentEntities
      }
    }
  }
}