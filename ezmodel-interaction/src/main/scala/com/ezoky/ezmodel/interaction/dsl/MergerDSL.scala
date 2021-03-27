package com.ezoky.ezmodel.interaction.dsl

import com.ezoky.ezmodel.core.Models._

/**
  * @author gweinbach on 24/03/2021
  * @since 0.2.0
  */


trait MergerDSL
  extends NaturalIdDSL {

  implicit val ModelDSLMerger: Merger[Model] =
    Merger.define((model1, model2) =>
      model1.copy(
        name = model2.name,
        domains = model1.domains.mergeWith(model2.domains)
      )
    )

  implicit val DomainDSLMerger: Merger[Domain] =
    Merger.define((domain1, domain2) =>
      domain1.copy(
        name = domain2.name,
        useCases = domain1.useCases.mergeWith(domain2.useCases),
        entities = domain1.entities.mergeWith(domain2.entities)
      )
    )

  implicit val InteractionMerger: Merger[Interaction] =
    Merger.define((_, interaction2) => interaction2)

  implicit val UseCaseDSLMerger: Merger[UseCase] =
    Merger.define((useCase1, useCase2) =>
      useCase1.copy(
        actor = useCase2.actor,
        goal = useCase2.goal,
        interaction = useCase1.interaction.mergeWith(useCase2.interaction),
        constraints = useCase2.constraints.foldLeft(useCase1.constraints) {
          case (map, (constraintType, entityStateMap)) =>
            map + (constraintType -> map.get(constraintType).fold(entityStateMap)(existingEntityStateMap =>
              existingEntityStateMap.mergeWith(entityStateMap)
            ))
        }
      )
    )

  implicit val EntityDSLMerger: Merger[Entity] =
    Merger.define((entity1, entity2) =>
      entity1.copy(
        name = entity2.name,
        attributes = entity1.attributes.mergeWith(entity2.attributes),
        aggregated = entity1.aggregated.mergeWith(entity2.aggregated),
        referenced = entity1.referenced.mergeWith(entity2.referenced)
      )
    )

  implicit val EntityStateDSLMerger: Merger[EntityState] =
    Merger.define { (t1, t2) => t2 }

  implicit val AttributeDSLMerger: Merger[Attribute] =
    Merger.define((t1, t2) =>
      t1.copy(
        name = t2.name,
        multiplicity = Ordering[Multiplicity].max(t1.multiplicity, t2.multiplicity),
        mandatory = t1.mandatory || t2.mandatory
      )
    )

  implicit val AggregateDSLMerger: Merger[Aggregate] =
    Merger.define((t1, t2) =>
      t1.copy(
        name = t2.name,
        leaf = t2.leaf,
        multiplicity = Ordering[Multiplicity].max(t1.multiplicity, t2.multiplicity),
        mandatory = t1.mandatory || t2.mandatory
      )
    )

  implicit val ReferenceDSLMerger: Merger[Reference] =
    Merger.define((t1, t2) =>
      t1.copy(
        name = t2.name,
        referenced = t2.referenced,
        multiplicity = Ordering[Multiplicity].max(t1.multiplicity, t2.multiplicity),
        mandatory = t1.mandatory || t2.mandatory
      )
    )

  implicit val AnyInteractionDescriptorDSLMerger: Merger[AnyInteractionDescriptor] =
    Merger.define((_, descriptor2) => descriptor2)
}
