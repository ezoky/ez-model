package com.ezoky.ezmodel.interaction.processing.simple

import com.ezoky.ezmodel.interaction.ModellingState
import com.ezoky.ezmodel.interaction.interpreter.Interpreting
import com.ezoky.ezmodel.interaction.processing.ModellingCommands

/**
  * @author gweinbach on 07/03/2021
  * @since 0.2.0
  */
trait ModellingInterpreter
  extends StateTransitionInterpreters

trait StateTransitionInterpreters
  extends Interpreting
    with ModellingCommands {

  implicit val defineModelInterpreter: StateTransitionInterpreter[ModellingState, DefineAModel] =
    StateTransitionInterpreter.define(state => command => state.setCurrentModel(command.model))

  implicit val defineDomainInterpreter: StateTransitionInterpreter[ModellingState, DefineADomain] =
    StateTransitionInterpreter.define(state => command => state.setCurrentDomain(command.domain))

  implicit val defineUseCaseInterpreter: StateTransitionInterpreter[ModellingState, DefineAUseCase] =
    StateTransitionInterpreter.define(state => command => state.setCurrentUseCase(command.useCase))

  implicit val defineEntityInterpreter: StateTransitionInterpreter[ModellingState, DefineAnEntity] =
    StateTransitionInterpreter.define(state => command => state.setCurrentEntity(command.entity))
}
