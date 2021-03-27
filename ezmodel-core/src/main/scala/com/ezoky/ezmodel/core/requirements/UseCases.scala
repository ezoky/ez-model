package com.ezoky.ezmodel.core.requirements

import com.ezoky.commons.NaturalIds
import com.ezoky.ezmodel.core.Entities


private[core] trait UseCases
  extends Atoms
    with Entities
    with Constraints
    with NaturalIds {

  case class UseCase(actor: Actor,
                     goal: Goal,
                     interaction: Option[Interaction] = None,
                     constraints: Constraints = Constraints.empty)
                    (implicit
                     entityStateId: EntityStateId,
                     interactionMerger: Merger[Interaction])
    extends Constrained[UseCase] {

    def mergeInteraction(newInteraction: Interaction): UseCase =
      interaction.fold(withInteraction(newInteraction))(existingInteraction =>
        copy(interaction = Some(existingInteraction.mergeWith(newInteraction)))
      )

    def withInteraction(interaction: Interaction): UseCase =
      copy(interaction = Some(interaction))

    def withPreCondition(state: EntityState): UseCase =
      copy(constraints = constrain(Pre, state))

    def withPostCondition(state: EntityState): UseCase =
      copy(constraints = constrain(Post, state))
  }


  type UseCaseId = NaturalId[UseCase]
  type UseCaseMap = NaturalMap[UseCaseId, UseCase]

  object UseCaseMap extends NaturalMapCompanion[UseCaseId, UseCase]


  case class Actor(name: Name)

  case class Action(verb: Verb)

  case class ActionObject(nameGroup: NameGroup)

  case class Goal(action: Action,
                  actionObject: Option[ActionObject] = None)

  case class Interaction(action: Action,
                         actionObject: Option[ActionObject] = None)

  type InteractionId = NaturalId[Interaction]
  type InteractionMap = NaturalMap[InteractionId, Interaction]

  object InteractionMap extends NaturalMapCompanion[InteractionId, Interaction]


}