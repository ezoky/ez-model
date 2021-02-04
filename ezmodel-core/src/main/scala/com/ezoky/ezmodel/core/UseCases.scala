package com.ezoky.ezmodel.core

object UseCases {

  import Atoms._
  import Constraints._
  import Entities._

  case class UseCase(actor: Actor,
                     goal: Goal,
                     constraints: Constraints = Constraints.Empty)
    extends Constrained[UseCase] {

    def this(useCase: UseCase) = {
      this(useCase.actor, useCase.goal)
    }

    def preCondition(state: EntityState): UseCase =
      copy(constraints = constrain(Pre, state))

    def postCondition(state: EntityState) =
      copy(constraints = constrain(Post, state))
  }

  object UseCase {

    implicit def implicitUseCase2(useCase: (String, String)): UseCase =
      UseCase(Actor(useCase._1), Goal(Action(useCase._2)))

    implicit def implicitUseCase3(useCase: (String, String, String)): UseCase =
      UseCase(Actor(useCase._1), Goal(Action(useCase._2), ActionObject(the, useCase._3)))
  }

  def PreCondition(useCase: UseCase, state: EntityState): UseCase =
    useCase.preCondition(state)

  def PostCondition(useCase: UseCase, state: EntityState): UseCase =
    useCase.postCondition(state)

    case class Actor(name: Name) {
    def iWantTo(action: Action, actionObject: ActionObject) = UseCase(this, Goal(action, actionObject))
  }

  implicit def stringActor(name: String): Actor = Actor(Name(name))

  def asA(name: Name) = Actor(name)

  def asAn(name: Name) = asA(name)

  case class Goal(action: Action, actionObject: ActionObject = null)

  object Goal {
    def iWantTo(action: Action, actionObject: ActionObject) = Goal(action, actionObject)
  }

  case class Action(verb: Verb)

  object Action {
    implicit def stringAction(verb: String) = Action(Verb(verb))
  }

  case class ActionObject(nameGroup: NameGroup)

  object ActionObject {
    implicit def implicitActionObject1(nameGroup: NameGroup) = ActionObject(nameGroup)

    implicit def implicitActionObject2(nameGroup: (Determinant, Name)) = ActionObject(NameGroup(nameGroup._1,
      nameGroup._2))

    implicit def implicitActionObject3(nameGroup: (Determinant, String)) = ActionObject(NameGroup(nameGroup._1,
      Name(nameGroup._2)))

    implicit def implicitActionObject4(nameGroup: String) = ActionObject(the, nameGroup)
  }

}
