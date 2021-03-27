package com.ezoky.ezmodel.core

import com.ezoky.commons.NaturalIds


private[core] trait Domains
  extends requirements.Models
    with interactions.Models
    with Entities
    with NaturalIds {

  case class Domain(name: Name,
                    useCases: UseCaseMap = UseCaseMap.empty,
                    entities: EntityMap = EntityMap.empty,
                    interactionDescriptors: AnyInteractionDescriptorMap = AnyInteractionDescriptorMap.empty)
                   (implicit
                    useCaseId: UseCaseId,
                    entityId: EntityId,
                    interactionDescriptorId: AnyInteractionDescriptorId,
                    entityMerger: Merger[Entity],
                    useCaseMerger: Merger[UseCase],
                    interactionDescriptorMerger: Merger[AnyInteractionDescriptor]) {

//    type InteractionDescriptorType =

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

    def ownsInteractionDescriptor(interactionDescriptor: AnyInteractionDescriptor): Boolean =
      interactionDescriptors.owns(interactionDescriptor)

    def withInteractionDescriptor(interactionDescriptor: AnyInteractionDescriptor): Domain =
      copy(interactionDescriptors = interactionDescriptors.add(interactionDescriptor))

    def mergeInteractionDescriptor(interactionDescriptor: AnyInteractionDescriptor): Domain =
      copy(interactionDescriptors = interactionDescriptors.merge(interactionDescriptor))
  }

  object Domain {
    def apply()
             (implicit
              useCaseId: UseCaseId,
              entityId: EntityId,
              interactionDescriptorId: AnyInteractionDescriptorId,
              entityMerger: Merger[Entity],
              useCaseMerger: Merger[UseCase],
              interactionDescriptorMerger: Merger[AnyInteractionDescriptor]): Domain =
      Domain(DefaultName)
  }


  type DomainId = NaturalId[Domain]
  type DomainMap = NaturalMap[DomainId, Domain]

  object DomainMap extends NaturalMapCompanion[DomainId, Domain]

}
