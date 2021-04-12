package com.ezoky.ezmodel.core


import com.ezoky.ezmodel.core.Models._

/**
  * Some common type classes (see [[com.ezoky.ezmodel.core.StandardTypeClasses]]) are provided for default
  * usage.
  * Import them in the context of use:
  * {{{
  * object MyTypeClasses extends com.ezoky.ezmodel.core.NaturalId.StandardTypeClasses
  * import MyTypeClasses._
  * }}}
  *
  * Can be used for standard usages or for testing.
  *
  * @author gweinbach on 24/02/2021
  * @since 0.2.0
  */
object StandardTypeClasses
  extends StandardTypeClasses

trait StandardTypeClasses
  extends StandardModel
    with StandardDomain
    with StandardUseCase
    with StandardEntity

private[core] trait StandardModel
  extends StandardDomain {

  implicit val ModelNaturalId: NaturalId[Model] =
    NaturalId.define(_.name)

  implicit val ModelMerger: Merger[Model] =
    Merger.define((model1, model2) =>
      model1.copy(
        name = model2.name,
        domains = model1.domains.mergeWith(model2.domains)
      )
    )
}

private[core] trait StandardDomain
  extends StandardEntity
    with StandardUseCase
    with StandardDescriptors {

  implicit val DomainNaturalId: NaturalId[Domain] =
    NaturalId.define(_.name)

  implicit val DomainMerger: Merger[Domain] =
    Merger.define((domain1, domain2) =>
      domain1.copy(
        name = domain2.name,
        useCases = domain1.useCases.mergeWith(domain2.useCases),
        entities = domain1.entities.mergeWith(domain2.entities),
        interactionDescriptors = domain1.interactionDescriptors.mergeWith(domain2.interactionDescriptors)
      )
    )
}

private[core] trait StandardEntity {

  implicit val AttributeNaturalId: NaturalId[Attribute] =
    NaturalId.define(_.name)

  implicit val AttributeMerger: Merger[Attribute] =
    Merger.define((t1, t2) =>
      t1.copy(
        name = t2.name,
        multiplicity = Ordering[Multiplicity].max(t1.multiplicity, t2.multiplicity),
        mandatory = t1.mandatory || t2.mandatory
      )
    )

  implicit val AggregateNaturalId: NaturalId[Aggregate] =
    NaturalId.define(_.name)

  implicit val AggregateMerger: Merger[Aggregate] =
    Merger.define((t1, t2) =>
      t1.copy(
        name = t2.name,
        leaf = t2.leaf,
        multiplicity = Ordering[Multiplicity].max(t1.multiplicity, t2.multiplicity),
        mandatory = t1.mandatory || t2.mandatory
      )
    )

  implicit val ReferenceNaturalId: NaturalId[Reference] =
    NaturalId.define(_.name)

  implicit val ReferenceMerger: Merger[Reference] =
    Merger.define((t1, t2) =>
      t1.copy(
        name = t2.name,
        referenced = t2.referenced,
        multiplicity = Ordering[Multiplicity].max(t1.multiplicity, t2.multiplicity),
        mandatory = t1.mandatory || t2.mandatory
      )
    )

  implicit val EntityNaturalId: NaturalId[Entity] =
    NaturalId.define(_.name)

  implicit val EntityMerger: Merger[Entity] =
    Merger.define { (t1, t2) =>
      t1.copy(
        name = t2.name,
        attributes = t1.attributes.mergeWith(t2.attributes),
        aggregated = t1.aggregated.mergeWith(t2.aggregated),
        referenced = t1.referenced.mergeWith(t2.referenced)
      )
    }

  implicit def EntityStateNaturalId(implicit
                                    entityId: NaturalId[Entity]): NaturalId[EntityState] =
    NaturalId.define(entityState => (entityId(entityState.entity), entityState.state.qualifier))

  implicit val EntityStateMerger: Merger[EntityState] =
    Merger.define { (t1, t2) => t2 }
}

private[core] trait StandardUseCase
  extends StandardEntity {

  implicit val ActorNaturalId: NaturalId[Actor] =
    NaturalId.define(_.name)

  implicit val GoalNaturalId: NaturalId[Goal] =
    NaturalId.define { goal =>
      (goal.action.verb, goal.actionObject.map { actionObject =>
        (actionObject.nameGroup.determinant, actionObject.nameGroup.name)
      })
    }

  implicit val InteractionNaturalId: NaturalId[Interaction] =
    NaturalId.define { interaction =>
      (interaction.action.verb, interaction.actionObject.map { actionObject =>
        (actionObject.nameGroup.determinant, actionObject.nameGroup.name)
      })
    }

  implicit val InteractionMerger: Merger[Interaction] =
    Merger.define((_, interaction2) =>
      interaction2
    )


  implicit def UseCaseNaturalId(implicit
                                actorId: NaturalId[Actor],
                                goalId: NaturalId[Goal]): NaturalId[UseCase] =
    NaturalId.define { useCase =>
      (actorId.apply(useCase.actor), goalId.apply(useCase.goal))
    }

  implicit val UseCaseMerger: Merger[UseCase] =
    Merger.define((t1, t2) =>
      t1.copy(
        actor = t2.actor,
        goal = t2.goal,
        interaction = t1.interaction.mergeWith(t2.interaction),
        constraints = t2.constraints.foldLeft(t1.constraints) {
          case (map, (constraintType, entityStateMap)) =>
            map + (constraintType -> map.get(constraintType).fold(entityStateMap)(existingEntityStateMap =>
              existingEntityStateMap.mergeWith(entityStateMap)
            ))
        }
      )
    )
}


private[core] trait StandardDescriptors {

  implicit val AnyInteractionDescriptorNaturalId: NaturalId[AnyInteractionDescriptor] =
    NaturalId.define(_.name)

  implicit val AnyInteractionDescriptorMerger: Merger[AnyInteractionDescriptor] =
    Merger.define((_, descriptor2) => descriptor2)
}
