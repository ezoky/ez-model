package com.ezoky.ezmodel.core


private[core] trait Domains
  extends Atoms
    with UseCases
    with Entities
    with NaturalIds{

  case class Domain(name: Name,
                    useCases: UseCaseMap = UseCaseMap.empty,
                    entities: EntityMap = EntityMap.empty)
                   (implicit
                    useCaseId: UseCaseId,
                    entityId: EntityId,
                    entityMerger: Merger[Entity],
                    useCaseMerger: Merger[UseCase]) {

    def ownsUseCase(useCase: UseCase): Boolean =
      useCases.owns(useCase)

    def withUseCase(useCase: UseCase): Domain =
      copy(useCases = useCases.add(useCase))

    def mergeUseCase(useCase: UseCase): Domain =
      copy(useCases = useCases.merge(useCase))

    def ownsEntity(entity: Entity): Boolean =
      entities.owns(entity)

    def withEntity(entity: Entity): Domain =
      copy(entities = entities.add(entity))

    def mergeEntity(entity: Entity): Domain =
      copy(entities = entities.merge(entity))
  }

  object Domain {
    def apply()
             (implicit
              useCaseId: UseCaseId,
              entityId: EntityId,
              entityMerger: Merger[Entity],
              useCaseMerger: Merger[UseCase]): Domain =
      Domain(DefaultName)
  }


  type DomainId = NaturalId[Domain]
  type DomainMap = NaturalMap[DomainId, Domain]

  object DomainMap extends NaturalMapCompanion[DomainId, Domain]

}
