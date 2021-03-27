package com.ezoky.ezmodel.core.interactions

import com.ezoky.ezmodel.core.requirements.UseCases

/**
  * @author gweinbach on 22/03/2021
  * @since 0.2.0
  */
trait InteractionRules
  extends Descriptors
    with UseCases {

  self =>

  trait AssignControllerToInteractionRule[InteractionRefType] extends InteractionRule {
    val interactionRef: InteractionRefType
  }

  trait ControllerTitleRule[ControllerType] extends InteractionRule {
    val title: ControllerType => String
  }


  case class AssignControllerToInteraction[InteractionRefType](interactionRef: InteractionRefType,
                                                               controllerRef: InteractionName)
    extends AssignControllerToInteractionRule[InteractionRefType]

  case class ControllerTitle[T, ControllerType <: InteractionController[T]](controllerRef: InteractionName,
                                                                            title: ControllerType => String)
    extends ControllerTitleRule[ControllerType]


  class InteractionRulesDefinitionContext[T, ControllerType <: InteractionController[T], InteractionRefType] {

    def defaultInteractionRules(interactionRef: InteractionRefType,
                                descriptorRef: InteractionName): InteractionRulesBag =
      InteractionRulesBag.Empty +
      AssignControllerToInteraction[InteractionRefType](interactionRef, descriptorRef) +
      ControllerTitle[T, ControllerType](descriptorRef, _.name.toString)
  }

}
