package com.ezoky.ezmodel.interaction.dsl

import com.ezoky.ezmodel.core.Models._
import com.ezoky.ezmodel.core.interactions.InteractionName

/**
  * @author gweinbach on 22/03/2021
  * @since 0.2.0
  */
trait InteractionDSL {

  def theInteraction(interaction: Interaction): Interaction =
    interaction

  // non transitive Action
  def theInteraction(action: Action): Interaction =
    Interaction(action, None)

  // transitive Action
  def theInteraction(action: Action,
                     actionObject: ActionObject): Interaction =
    Interaction(action, Some(actionObject))

  // transitive Action made simple
  def theInteraction(action: Action,
                     determinant: Determinant,
                     name: Name): Interaction =
    Interaction(action, Some(ActionObject(NameGroup(determinant, name))))

  implicit class InteractionHelper(interaction: Interaction)
                                  (implicit
                                   val interactionId: InteractionId) {

    def uses(controllerRef: InteractionName): InteractionRule =
    // This does not actually take descriptor type into account... but it doesn't matter!
      AssignControllerToInteraction(interactionId(interaction), controllerRef)
  }

}
