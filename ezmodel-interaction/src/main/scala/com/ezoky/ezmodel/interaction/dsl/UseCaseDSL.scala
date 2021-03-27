package com.ezoky.ezmodel.interaction.dsl

import com.ezoky.ezmodel.core.Models._

/**
  * @author gweinbach on 07/02/2021
  * @since 0.2.0
  */
trait UseCaseDSL
  extends NaturalIdDSL
    with MergerDSL {

  /**
    * One single implicit with following signature should be needed:
    * {{{def implicitTupleToUseCase(useCase: (Actor, NameGroup)): UseCase}}}
    * But implicit mechanism does not work well ont Tuples.
    */
  implicit def convertTupleToUseCase2(useCase: (String, String)): UseCase =
    UseCase(
      Actor(Name(useCase._1)),
      Goal(Action(Verb(useCase._2)))
    )

  /**
    * required because implicit mechanism does not work well ont Tuples
    */
  implicit def convertTupleToUseCase3(useCase: (String, ActionGroup)): UseCase =
    UseCase(
      Actor(Name(useCase._1)),
      useCase._2
    )

  // Actor
  implicit def convertStringToActor(name: String): Actor =
    Actor(Name(name))

  def asA(name: Name): Actor = Actor(name)

  def asAn(name: Name): Actor = asA(name)

  implicit class ActorHelper(actor: Actor) {

    def inOrderTo(goal: Goal): UseCase =
      UseCase(actor, goal)

    // non transitive Action
    def inOrderTo(action: Action): UseCase =
      UseCase(actor, Goal(action, None))

    // transitive Action
    def inOrderTo(action: Action,
                  actionObject: ActionObject): UseCase =
      UseCase(actor, Goal(action, Some(actionObject)))

    // transitive Action made simple
    def inOrderTo(action: Action,
                  determinant: Determinant,
                  name: Name): UseCase =
      UseCase(actor, Goal(action, Some(ActionObject(NameGroup(determinant, name)))))
  }

  // Action
  implicit def convertStringToAction(verb: String): Action =
    Action(Verb(verb))


  // Action Object
  implicit def convertNameGroupToActionObject(nameGroup: NameGroup): ActionObject =
    ActionObject(nameGroup)

  implicit def convertDeterminantAndNameToActionObject(nameGroup: (Determinant, Name)): ActionObject =
    ActionObject(NameGroup(nameGroup._1, nameGroup._2))

  implicit def convertDeterminantAndStringToActionObject(nameGroup: (Determinant, String)): ActionObject =
    ActionObject(NameGroup(nameGroup._1, Name(nameGroup._2)))

  implicit def convertStringToActionObject(nameGroup: String): ActionObject =
    ActionObject(NameGroup(Determinant.the, Name(nameGroup)))


  case class ActionGroup(action: Action, actionObject: ActionObject)

  implicit def convertActionGroupToGoal(actionGroup: ActionGroup): Goal =
    Goal(actionGroup.action, Some(actionGroup.actionObject))

  implicit def convertActionGroupToInteraction(actionGroup: ActionGroup): Interaction =
    Interaction(actionGroup.action, Some(actionGroup.actionObject))

  /**
    * Allows ActionGroup constructs like `("invoice" a "customer")`
    */
  implicit class ActionGroupStringHelper(verb: String) {

    def a(name: Name): ActionGroup = ActionGroup(Action(Verb(verb)), ActionObject(NameGroup(Determinant.a, name)))

    def an(name: Name): ActionGroup = ActionGroup(Action(Verb(verb)), ActionObject(NameGroup(Determinant.an, name)))

    def the(name: Name): ActionGroup = ActionGroup(Action(Verb(verb)), ActionObject(NameGroup(Determinant.the, name)))

    def some(name: Name): ActionGroup = ActionGroup(Action(Verb(verb)), ActionObject(NameGroup(Determinant.some, name)))

    def any(name: Name): ActionGroup = ActionGroup(Action(Verb(verb)), ActionObject(NameGroup(Determinant.any, name)))

    def every(name: Name): ActionGroup = ActionGroup(Action(Verb(verb)),
      ActionObject(NameGroup(Determinant.every, name)))

    def all(name: Name): ActionGroup = ActionGroup(Action(Verb(verb)), ActionObject(NameGroup(Determinant.all, name)))

    def few(name: Name): ActionGroup = ActionGroup(Action(Verb(verb)), ActionObject(NameGroup(Determinant.few, name)))
  }

  // Use Case

  def theUseCase(actor: Actor, goal: Goal): UseCase =
    UseCase(actor, goal)

  implicit class UseCaseHelper(useCase: UseCase) {

    def provided(preCondition: EntityState): UseCase =
      useCase.withPreCondition(preCondition)

    def resultingIn(postCondition: EntityState): UseCase =
      useCase.withPostCondition(postCondition)

    def resultsIn(postCondition: EntityState): UseCase =
      resultingIn(postCondition)

    def iWantTo(interaction: Interaction): UseCase =
      useCase.withInteraction(interaction)
  }

}
