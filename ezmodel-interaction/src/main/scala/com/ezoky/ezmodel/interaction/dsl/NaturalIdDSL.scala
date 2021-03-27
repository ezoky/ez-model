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

  implicit val InteractionDSLNaturalId: NaturalId[Interaction] =
    NaturalId.define { interaction =>
      (interaction.action.verb, interaction.actionObject.map { actionObject =>
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

  implicit val InteractionDescriptorDSLNaturalId: NaturalId[InteractionDescriptor[_, _]] =
    NaturalId.define(_.name)

  implicit val AnyInteractionDescriptorDSLNaturalId: NaturalId[AnyInteractionDescriptor] =
    NaturalId.define(_.name)
}
