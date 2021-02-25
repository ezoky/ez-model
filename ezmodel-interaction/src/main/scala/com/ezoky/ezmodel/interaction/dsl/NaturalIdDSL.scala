package com.ezoky.ezmodel.interaction.dsl

import com.ezoky.ezmodel.core.Models._

/**
  * @author gweinbach on 24/02/2021
  * @since 0.2.0
  */
trait NaturalIdDSL {

  implicit val ModelDSLNaturalId: NaturalId[Model] =
    NaturalId.define(_.name)

  implicit val DomainDSLNaturalId: NaturalId[Domain] =
    NaturalId.define(_.name)

  implicit val ActorDSLNaturalId: NaturalId[Actor] =
    NaturalId.define(_.name)

  implicit val GoalNaturalId: NaturalId[Goal] =
    NaturalId.define { goal =>
      (goal.action.verb, goal.actionObject.map { actionObject =>
        (actionObject.nameGroup.determinant, actionObject.nameGroup.name)
      })
    }

  implicit val UseCaseDSLNaturalId: NaturalId[UseCase] =
    NaturalId.define(useCase => (ActorDSLNaturalId(useCase.actor), GoalNaturalId(useCase.goal)))

  implicit val EntityDSLNaturalId: NaturalId[Entity] =
    NaturalId.define(_.name)

  implicit val StateNameDSLNaturalId: NaturalId[StateName] =
    NaturalId.define(_.qualifier)

  implicit def EntityStateDSLNaturalId(implicit
                                       entityId: NaturalId[Entity],
                                       stateNameId: NaturalId[StateName]): NaturalId[EntityState] =
    NaturalId.define(entityState => (entityId(entityState.entity), stateNameId(entityState.state)))

  implicit val AttributeDSLNaturalId: NaturalId[Attribute] =
    NaturalId.define(_.name)

  implicit val AggregateDSLNaturalId: NaturalId[Aggregate] =
    NaturalId.define(_.name)

  implicit val ReferenceDSLNaturalId: NaturalId[Reference] =
    NaturalId.define(_.name)
}


trait MergerDSL
  extends NaturalIdDSL {

  implicit val DomainDSLMerger: Merger[Domain] =
    Merger.define( (domain1, domain2) =>
      domain1.copy(
        name = domain2.name,
        useCases = domain1.useCases.mergeMap(domain2.useCases),
        entities = domain1.entities.mergeMap(domain2.entities)
      )
    )

  implicit val UseCaseDSLMerger: Merger[UseCase] =
    Merger.define((t1, t2) =>
      t1.copy(
        actor = t2.actor,
        goal = t2.goal,
        constraints = t2.constraints.foldLeft(t1.constraints) {
          case (map, (constraintType, entityStateMap)) =>
            map + (constraintType -> map.get(constraintType).fold(entityStateMap)(existingEntityStateMap =>
              existingEntityStateMap.mergeMap(entityStateMap)
            ))
        }
      )
    )

  implicit val EntityDSLMerger: Merger[Entity] =
    Merger.define { (t1, t2) =>
      t1.copy(
        name = t2.name,
        attributes = t1.attributes.mergeMap(t2.attributes),
        aggregated = t1.aggregated.mergeMap(t2.aggregated),
        referenced = t1.referenced.mergeMap(t2.referenced)
      )
    }

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


}
