package com.ezoky.ezmodel.interaction.dsl

import com.ezoky.ezmodel.core.Models._

/**
  * @author gweinbach on 26/03/2021
  * @since 0.2.0
  */
trait DescriptorDSL {

  implicit class DescriptorHelper[T, ControlType <: InteractionController[T]](descriptor: InteractionDescriptor[T, ControlType])
                                                                             (implicit
                                                                              val interactionId: InteractionId) {

    def withTitle(title: (ControlType) => String): InteractionDescriptor[T, ControlType] =
      descriptor.withRule(ControllerTitle(descriptor.name, title))
  }

}
