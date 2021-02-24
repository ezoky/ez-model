package com.ezoky.ezmodel.core


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

  object UseCaseMap {
    def empty: UseCaseMap =
      NaturalMap.empty[UseCaseId, UseCase]

    def apply(useCases: UseCase*)
             (implicit
              id: UseCaseId): UseCaseMap =
      NaturalMap(useCases: _*)
  }


  case class Actor(name: Name)

  case class Goal(action: Action,
                  actionObject: Option[ActionObject] = None)

  case class Action(verb: Verb)

  case class ActionObject(nameGroup: NameGroup)

}