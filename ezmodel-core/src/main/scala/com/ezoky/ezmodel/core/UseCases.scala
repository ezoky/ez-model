package com.ezoky.ezmodel.core

object UseCases {

  import scala.language.implicitConversions

  import com.ezoky.ezmodel.storage.EventStore

  import Atoms._
  import Structures._
  import Constrains._

  case class UseCase(actor: Actor, goal: Goal) {
    EventStore(Model).store(this)

    def this(useCase: UseCase) {
      this(useCase.actor, useCase.goal)
    }
    def preCondition(state: EntityState) = {
      PreCondition(this, state)
    }
    def postCondition(state: EntityState) = {
      PostCondition(this, state)
    }
  }

  class PreCondition(useCase: UseCase, state: EntityState) extends UseCase(useCase) with Constrained {
    override val constraints = constrain(useCase, Pre, state)
  }
  object PreCondition {
    def apply(useCase: UseCase, state: EntityState) = { new PreCondition(useCase, state) }
  }
  class PostCondition(useCase: UseCase, state: EntityState) extends UseCase(useCase) with Constrained {
    override val constraints = constrain(useCase, Post, state)
  }
  object PostCondition {
    def apply(useCase: UseCase, state: EntityState) = { new PostCondition(useCase, state) }
  }

  object UseCase {
    //  implicit def implicitUseCase1(useCase: (Actor, Goal) = UseCase(useCase._1, useCase._2)
    implicit def implicitUseCase2(useCase: (String, String)) = UseCase(Actor(useCase._1), Goal(Action(useCase._2)))
    implicit def implicitUseCase3(useCase: (String, String, String)) = UseCase(Actor(useCase._1), Goal(Action(useCase._2), ActionObject(the, useCase._3)))
  }

  case class Actor(name: Name) {
    def iWantTo(action: Action, actionObject: ActionObject) = UseCase(this, Goal(action, actionObject))
  }
  implicit def stringActor(name: String) = Actor(Name(name))
  def asA(name: Name) = Actor(name)
  def asAn(name: Name) = Actor(name)

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
    implicit def implicitActionObject2(nameGroup: (Determinant, Name)) = ActionObject(NameGroup(nameGroup._1, nameGroup._2))
    implicit def implicitActionObject3(nameGroup: (Determinant, String)) = ActionObject(NameGroup(nameGroup._1, Name(nameGroup._2)))
    implicit def implicitActionObject1(nameGroup: String) = ActionObject(the, nameGroup)
  }
}
