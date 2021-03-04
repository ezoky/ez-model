package com.ezoky.ezmodel.core

import com.ezoky.commons.NaturalIds


private[core] trait UseCases
  extends Atoms
    with Entities
    with Constraints
    with NaturalIds {

  case class UseCase(actor: Actor,
                     goal: Goal,
                     constraints: Constraints = Constraints.empty)
                    (implicit
                     entityStateId: EntityStateId)
    extends Constrained[UseCase] {

    def withPreCondition(state: EntityState): UseCase =
      copy(constraints = constrain(Pre, state))

    def withPostCondition(state: EntityState): UseCase =
      copy(constraints = constrain(Post, state))
  }


  type UseCaseId = NaturalId[UseCase]
  type UseCaseMap = NaturalMap[UseCaseId, UseCase]

  object UseCaseMap extends NaturalMapCompanion[UseCaseId, UseCase]


  case class Actor(name: Name)

  case class Goal(action: Action,
                  actionObject: Option[ActionObject] = None)

  case class Action(verb: Verb)

  case class ActionObject(nameGroup: NameGroup)

}