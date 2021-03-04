package com.ezoky.ezmodel.core

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
trait StandardModels
  extends Models
    with StandardTypeClasses

trait StandardTypeClasses
  extends StandardModel
    with StandardDomain
    with StandardUseCase
    with StandardEntity


trait StandardModel
  extends Models
    with StandardDomain {
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

trait StandardDomain
  extends Domains
    with StandardEntity
    with StandardUseCase {

  implicit val DomainNaturalId: NaturalId[Domain] =
    NaturalId.define(_.name)

  implicit val DomainMerger: Merger[Domain] =
    Merger.define( (domain1, domain2) =>
      domain1.copy(
        name = domain2.name,
        useCases = domain1.useCases.mergeWith(domain2.useCases),
        entities = domain1.entities.mergeWith(domain2.entities)
      )
    )
}

trait StandardEntity
  extends Entities {

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

trait StandardUseCase
  extends UseCases
  with StandardEntity {

  implicit val ActorNaturalId: NaturalId[Actor] =
    NaturalId.define(_.name)

  implicit val GoalNaturalId: NaturalId[Goal] =
    NaturalId.define { goal =>
      (goal.action.verb, goal.actionObject.map { actionObject =>
        (actionObject.nameGroup.determinant, actionObject.nameGroup.name)
      })
    }

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
        constraints = t2.constraints.foldLeft(t1.constraints) {
          case (map, (constraintType, entityStateMap)) =>
            map + (constraintType -> map.get(constraintType).fold(entityStateMap)(existingEntityStateMap =>
              existingEntityStateMap.mergeWith(entityStateMap)
            ))
        }
      )
    )
}
