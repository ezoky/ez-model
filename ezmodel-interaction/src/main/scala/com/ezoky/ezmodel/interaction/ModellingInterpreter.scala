package com.ezoky.ezmodel.interaction

import com.ezoky.ezmodel.core.Models._
import com.ezoky.ezmodel.interaction.interpreter.{Interpreting, Parsing}

/**
  * @author gweinbach on 21/02/2021
  * @since 0.2.0
  */
trait ModellingInterpreter
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
