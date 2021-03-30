package com.ezoky.ezmodel.interaction.processing

import com.ezoky.ezinterpreter.Parsing
import com.ezoky.ezmodel.core.Models._

/**
  * @author gweinbach on 07/03/2021
  * @since 0.2.0
  */
trait ModellingSyntax
  extends ModellingCommands
    with ModellingParsers

/**
  * Syntax
  */
trait ModellingCommands {

  case class DefineAModel(model: Model)

  case class DefineADomain(domain: Domain)

  case class DefineAUseCase(useCase: UseCase)
  
  case class DefineAnInteraction(interaction: Interaction)

  case class DefineAnEntity(entity: Entity)

  case class DescribeAnInteraction(interactionDescriptor: AnyInteractionDescriptor)

}

trait ModellingParsers
  extends Parsing
    with ModellingCommands {

  implicit def modelParser[T](implicit cvtToModel: T => Model): Parser[T, DefineAModel] =
    Parser.define(modelLike => Statement(DefineAModel(cvtToModel(modelLike))))

  implicit def domainParser[T](implicit cvtToDomain: T => Domain): Parser[T, DefineADomain] =
    Parser.define(domainLike => Statement(DefineADomain(cvtToDomain(domainLike))))

  implicit def useCaseParser[T](implicit cvtToUseCase: T => UseCase): Parser[T, DefineAUseCase] =
    Parser.define(useCaseLike => Statement(DefineAUseCase(cvtToUseCase(useCaseLike))))
    
  implicit def interactionParser[T](implicit cvtToInteraction: T => Interaction): Parser[T, DefineAnInteraction] =
    Parser.define(interactionLike => Statement(DefineAnInteraction(cvtToInteraction(interactionLike))))

  implicit def entityParser[T](implicit cvtToEntity: T => Entity): Parser[T, DefineAnEntity] =
    Parser.define(entityLike => Statement(DefineAnEntity(cvtToEntity(entityLike))))

  implicit def interactionDescriptorParser[T, ControlType <: InteractionController[T]]: Parser[InteractionDescriptor[T, ControlType], DescribeAnInteraction] =
    Parser.define(interactionDescriptorLike => Statement(DescribeAnInteraction(interactionDescriptorLike)))

}
