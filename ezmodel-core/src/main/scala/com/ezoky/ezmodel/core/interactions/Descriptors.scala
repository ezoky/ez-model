package com.ezoky.ezmodel.core.interactions

import com.ezoky.commons.NaturalIds

/**
  * @author gweinbach on 22/03/2021
  * @since 0.2.0
  */
trait Descriptors
  extends Controllers
    with NaturalIds {

  // Rule to describe the detailed behaviour of Controllers and Viewers
  trait InteractionRule


  trait InteractionDescriptor[+T, +ControllerType <: InteractionController[T]]
    extends InteractionRuleHolder {

    val name: InteractionName

    def withRule(rule: InteractionRule): InteractionDescriptor[T, ControllerType]
  }

  type InteractionDescriptorId[T, ControllerType <: InteractionController[T]] =
    NaturalId[InteractionDescriptor[T, ControllerType]]
  type InteractionDescriptorMap[T, ControllerType <: InteractionController[T], I <: InteractionDescriptorId[T, ControllerType]] =
    NaturalMap[InteractionDescriptorId[T, ControllerType], InteractionDescriptor[T, ControllerType]]

  type AnyInteractionDescriptor = InteractionDescriptor[Any, InteractionController[Any]]
  type AnyInteractionDescriptorId = NaturalId[AnyInteractionDescriptor]
  type AnyInteractionDescriptorMap = NaturalMap[AnyInteractionDescriptorId, AnyInteractionDescriptor]

  object AnyInteractionDescriptorMap extends NaturalMapCompanion[AnyInteractionDescriptorId, AnyInteractionDescriptor]


  case class FormOf[T](name: InteractionName,
                       rules: InteractionRulesBag = InteractionRulesBag.Empty)
    extends InteractionDescriptor[T, SingleInstanceController[T]] {
    override def withRule(rule: InteractionRule): FormOf[T] =
      copy(rules = rules + rule)
  }

  case class ListOf[T](name: InteractionName,
                       rules: InteractionRulesBag = InteractionRulesBag.Empty)
    extends InteractionDescriptor[T, MultipleInstanceController[T]] {
    override def withRule(rule: InteractionRule): ListOf[T] =
      copy(rules = rules + rule)
  }

  case class SelectorOf[T](name: InteractionName,
                           rules: InteractionRulesBag = InteractionRulesBag.Empty)
    extends InteractionDescriptor[T, InstanceSelectionController[T]] {
    override def withRule(rule: InteractionRule): SelectorOf[T] =
      copy(rules = rules + rule)
  }


  /**
    * TODO This will require further refinement of course
    */
  type InteractionRulesBag = Set[InteractionRule]

  object InteractionRulesBag {

    val Empty: InteractionRulesBag = Set.empty
  }

  trait InteractionRuleHolder {

    val rules: InteractionRulesBag

    def withRule(rule: InteractionRule): InteractionRuleHolder
  }

}
