package com.ezoky.ezmodel.core

import com.ezoky.ezmodel.core.NaturalId.NaturalMap

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

  type DomainId = NaturalId[Domain]
  type DomainMap = NaturalMap[DomainId, Domain]

  object DomainMap {
    def empty: DomainMap =
      NaturalMap.empty[DomainId, Domain]

    def apply(domains: Domain*)
             (implicit
              id: DomainId): DomainMap =
      NaturalMap(domains: _*)
  }

}
