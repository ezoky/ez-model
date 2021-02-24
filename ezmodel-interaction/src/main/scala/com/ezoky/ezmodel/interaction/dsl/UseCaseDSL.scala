package com.ezoky.ezmodel.interaction.dsl

import com.ezoky.ezmodel.core.Models._

/**
  * @author gweinbach on 07/02/2021
  * @since 0.2.0
  */
trait UseCaseDSL
  extends NaturalIdDSL {

  implicit def implicitUseCase2(useCase: (String, String)): UseCase =
    UseCase(
      Actor(Name(useCase._1)),
      Goal(Action(Verb(useCase._2)))
    )

  implicit def implicitUseCase3(useCase: (String, Goal)): UseCase =
    UseCase(
      Actor(Name(useCase._1)),
      useCase._2
//      Goal(Action(Verb(useCase._2)),
//        Some(ActionObject(NameGroup(Determinant.the, Name(useCase._3)))))
    )

  implicit class ActorHelper(actor: Actor) {

    def iWantTo(goal: Goal): UseCase =
      UseCase(actor, goal)

    // non transitive Action
    def iWantTo(action: Action): UseCase =
      UseCase(actor, Goal(action, None))

    // transitive Action
    def iWantTo(action: Action,
                actionObject: ActionObject): UseCase =
      UseCase(actor, Goal(action, Some(actionObject)))

    // transitive Action made simple
    def iWantTo(action: Action,
                determinant: Determinant,
                name: Name): UseCase =
      UseCase(actor, Goal(action, Some(ActionObject(NameGroup(determinant, name)))))
  }

  // UseCases
  implicit def stringActor(name: String): Actor =
    Actor(Name(name))

  def asA(name: Name): Actor = Actor(name)

  def asAn(name: Name): Actor = asA(name)

  // Action
  implicit def stringToAction(verb: String): Action =
    Action(Verb(verb))


  // Action Object
  implicit def nameGroupToActionObject(nameGroup: NameGroup): ActionObject =
    ActionObject(nameGroup)

  implicit def determinantAndNameToActionObject(nameGroup: (Determinant, Name)): ActionObject =
    ActionObject(NameGroup(nameGroup._1, nameGroup._2))

  implicit def determinantAndStringToActionObject(nameGroup: (Determinant, String)): ActionObject =
    ActionObject(NameGroup(nameGroup._1, Name(nameGroup._2)))

  implicit def stringToActionObject(nameGroup: String): ActionObject =
    ActionObject(NameGroup(Determinant.the, Name(nameGroup)))

  /**
    * Alows Goals constructs like `("invoice" a "customer")`
    */
  implicit class GoalStringHelper(verb: String) {

    def a(name: Name): Goal = Goal(Action(Verb(verb)), Some(ActionObject(NameGroup(Determinant.a, name))))

    def an(name: Name): Goal = Goal(Action(Verb(verb)), Some(ActionObject(NameGroup(Determinant.an, name))))

    def the(name: Name): Goal = Goal(Action(Verb(verb)), Some(ActionObject(NameGroup(Determinant.the, name))))

    def some(name: Name): Goal = Goal(Action(Verb(verb)), Some(ActionObject(NameGroup(Determinant.some, name))))

    def any(name: Name): Goal = Goal(Action(Verb(verb)), Some(ActionObject(NameGroup(Determinant.any, name))))

    def every(name: Name): Goal = Goal(Action(Verb(verb)), Some(ActionObject(NameGroup(Determinant.every, name))))

    def all(name: Name): Goal = Goal(Action(Verb(verb)), Some(ActionObject(NameGroup(Determinant.all, name))))

    def few(name: Name): Goal = Goal(Action(Verb(verb)), Some(ActionObject(NameGroup(Determinant.few, name))))
  }

  implicit class UseCaseHelper(useCase: UseCase) {

    def provided(preCondition: EntityState): UseCase =
      useCase.withPreCondition(preCondition)

    def resultingIn(postCondition: EntityState): UseCase =
      useCase.withPostCondition(postCondition)
  }
}
