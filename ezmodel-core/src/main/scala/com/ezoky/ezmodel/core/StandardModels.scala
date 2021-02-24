package com.ezoky.ezmodel.core

/**
  * Can be used for standard usages or for testing
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
  extends Models {
  implicit val ModelNaturalId: NaturalId[Model] =
    NaturalId.define(_.name)
}

trait StandardDomain
  extends Domains {
  implicit val DomainNaturalId: NaturalId[Domain] =
    NaturalId.define(_.name)
}

trait StandardEntity
  extends Entities {

  implicit val EntityNaturalId: NaturalId[Entity] =
    NaturalId.define(_.name)

  implicit def EntityStateNaturalId(implicit
                                    entityId: NaturalId[Entity]): NaturalId[EntityState] =
    NaturalId.define(entityState => (entityId(entityState.entity), entityState.state.qualifier))
}

trait StandardUseCase
  extends UseCases {

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
}
