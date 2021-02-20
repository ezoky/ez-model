package com.ezoky.ezmodel.core

private[core] trait UseCases
  extends Atoms
    with Entities
    with Constraints {

  case class UseCase(actor: Actor,
                     goal: Goal,
                     constraints: Constraints = Constraints.Empty)
    extends Constrained[UseCase] {

    def withPreCondition(state: EntityState): UseCase =
      copy(constraints = constrain(Pre, state))

    def withPostCondition(state: EntityState) =
      copy(constraints = constrain(Post, state))
  }


  case class Actor(name: Name)

  case class Goal(action: Action,
                  actionObject: Option[ActionObject] = None)

  case class Action(verb: Verb)

  case class ActionObject(nameGroup: NameGroup)

}