package com.ezoky.ezmodel.interaction

import com.ezoky.ezmodel.core.Models._
import com.ezoky.ezmodel.interaction.interpreter.{Interpreting, Parsing}

/**
  * @author gweinbach on 21/02/2021
  * @since 0.2.0
  */
trait ModellingInterpreter
  extends ModelSyntax
    with DomainSyntax
    with UseCaseSyntax
    with EntitySyntax

trait ModelSyntax
  extends Parsing
    with Interpreting {

  // Syntax
  case class DefineAModel(model: Model)


  // Parsers
  implicit val modelParser: Parser[Model, DefineAModel] =
    Parser.define(model => Statement(DefineAModel(model)))


  // Interpreters
  implicit val defineModelInterpreter: Interpreter[ModellingState, DefineAModel] =
    Interpreter.define(state => command => state.setCurrentModel(command.model))
}

trait DomainSyntax
  extends Parsing
    with Interpreting {

  // Syntax
  case class DefineADomain(domain: Domain)


  // Parsers
  implicit val domainParser: Parser[Domain, DefineADomain] =
    Parser.define(domain => Statement(DefineADomain(domain)))


  // Interpreters
  implicit val defineDomainInterpreter: Interpreter[ModellingState, DefineADomain] =
    Interpreter.define(state => command => state.setCurrentDomain(command.domain))
}

trait UseCaseSyntax
  extends Parsing
    with Interpreting {

  // Syntax
  case class DefineAUseCase(useCase: UseCase)


  // Parsers
  implicit val useCaseParser: Parser[UseCase, DefineAUseCase] =
    Parser.define(useCase => Statement(DefineAUseCase(useCase)))


  // Interpreters
  implicit val defineUseCaseInterpreter: Interpreter[ModellingState, DefineAUseCase] =
    Interpreter.define(state => command => state.setCurrentUseCase(command.useCase))
}

trait EntitySyntax
  extends Parsing
    with Interpreting {

  // Syntax
  case class DefineAnEntity(entity: Entity)


  // Parsers
  implicit def entityParser[T](implicit cvtToEntity: T => Entity): Parser[T, DefineAnEntity] =
    Parser.define(entity => Statement(DefineAnEntity(cvtToEntity(entity))))


  // Interpreters
  implicit val defineEntityInterpreter: Interpreter[ModellingState, DefineAnEntity] =
    Interpreter.define(state => command => state.setCurrentEntity(command.entity))
}
