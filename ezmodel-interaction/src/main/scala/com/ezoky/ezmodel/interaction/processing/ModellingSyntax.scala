package com.ezoky.ezmodel.interaction.processing

import com.ezoky.ezmodel.core.Models._
import com.ezoky.ezmodel.interaction.interpreter.Parsing

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

  case class DefineAnEntity(entity: Entity)

}

trait ModellingParsers
  extends Parsing
    with ModellingCommands {

  implicit val modelParser: Parser[Model, DefineAModel] =
    Parser.define(model => Statement(DefineAModel(model)))

  implicit val domainParser: Parser[Domain, DefineADomain] =
    Parser.define(domain => Statement(DefineADomain(domain)))

  implicit val useCaseParser: Parser[UseCase, DefineAUseCase] =
    Parser.define(useCase => Statement(DefineAUseCase(useCase)))

  implicit def entityParser[T](implicit cvtToEntity: T => Entity): Parser[T, DefineAnEntity] =
    Parser.define(entity => Statement(DefineAnEntity(cvtToEntity(entity))))

}
