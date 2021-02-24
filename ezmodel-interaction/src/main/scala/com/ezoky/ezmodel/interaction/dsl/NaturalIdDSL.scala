package com.ezoky.ezmodel.interaction.dsl

import com.ezoky.ezmodel.core.Models._
import com.ezoky.ezmodel.core.NaturalId

/**
  * @author gweinbach on 24/02/2021
  * @since 0.2.0
  */
trait NaturalIdDSL {

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

  implicit val EntityStateDSLNaturalId: NaturalId[EntityState] =
    NaturalId.define(entityState => (EntityDSLNaturalId(entityState.entity), StateNameDSLNaturalId(entityState.state)))
}
