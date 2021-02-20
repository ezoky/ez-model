package com.ezoky.ezmodel.core

private[core] trait Domains
  extends Atoms
    with UseCases
    with Entities {

  case class Domain(name: Name,
                    useCases: List[UseCase] = List.empty,
                    entities: List[Entity] = List.empty) {

    def withUseCase(uc: UseCase): Domain = {
      copy(useCases = uc :: useCases)
    }

    def withEntity(ent: Entity): Domain = {
      copy(entities = ent :: entities)
    }
  }

  object Domain {

    def apply(): Domain =
      Domain(DefaultName)
  }

}
