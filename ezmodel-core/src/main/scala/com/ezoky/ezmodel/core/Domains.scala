package com.ezoky.ezmodel.core

import com.ezoky.ezmodel.core.NaturalId.NaturalMap

private[core] trait Domains
  extends Atoms
    with UseCases
    with Entities {

  case class Domain(name: Name,
                    useCases: UseCaseMap = UseCaseMap.empty,
                    entities: EntityMap = EntityMap.empty)
                   (implicit
                    useCaseId: UseCaseId,
                    entityId: EntityId) {

    def withUseCase(useCase: UseCase): Domain = {
      copy(useCases = useCases.add(useCase))
    }

    def withEntity(entity: Entity): Domain = {
      copy(entities = entities.add(entity))
    }
  }

  object Domain {
    def apply()
             (implicit
              useCaseId: UseCaseId,
              entityId: EntityId): Domain =
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
