package com.ezoky.ezmodel.core

import com.ezoky.ezmodel.core.NaturalId.define

/**
  * Can be used for standard usages
  *
  * @author gweinbach on 24/02/2021
  * @since 0.2.0
  */
trait StandardModels
  extends Models
    with StandardModel
    with StandardDomain
    with StandardUseCase
    with StandardEntity


trait StandardModel
  extends Models {
  implicit val ModelNaturalId: NaturalId[Model] =
    define(_.name)
}

trait StandardDomain
  extends Domains {
  implicit val DomainNaturalId: NaturalId[Domain] =
    define(_.name)
}

trait StandardEntity
  extends Entities {

  implicit val EntityNaturalId: NaturalId[Entity] =
    define(_.name)

  implicit def EntityStateNaturalId(implicit
                                    entityId: NaturalId[Entity]): NaturalId[EntityState] =
    define(entityState => (entityId(entityState.entity), entityState.state.qualifier))
}

trait StandardUseCase
  extends UseCases {

  implicit val ActorNaturalId: NaturalId[Actor] =
    define(_.name)

  implicit val GoalNaturalId: NaturalId[Goal] =
    define { goal =>
      (goal.action.verb, goal.actionObject.map { actionObject =>
        (actionObject.nameGroup.determinant, actionObject.nameGroup.name)
      })
    }

  implicit def UseCaseNaturalId(implicit
                                actorId: NaturalId[Actor],
                                goalId: NaturalId[Goal]): NaturalId[UseCase] =
    define { useCase =>
      (actorId.apply(useCase.actor), goalId.apply(useCase.goal))
    }
}
