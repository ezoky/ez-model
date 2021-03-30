package com.ezoky.ezinterpreter

import shapeless.{::, HList, HNil}

/**
  * ..and then interpreted in terms of state change
  */
trait Interpreting
  extends Parsing {

  /**
    * @tparam StateType     is the incoming State type
    * @tparam StatementType is the statement (command) type
    * @tparam OutputType    is the output type of the interpreter
    */
  trait Interpreter[-StateType, -StatementType, +OutputType] {

    def interpret(state: StateType,
                  statement: StatementType): OutputType

    def apply(state: StateType,
              statement: Statement[StatementType]): OutputType =
      interpret(state, statement.stated)
  }

  object Interpreter {

    def define[StateType, StatementType, OutputType](interpretation: StateType => StatementType => OutputType): Interpreter[StateType, StatementType, OutputType] =
      (state: StateType, statement: StatementType) => interpretation(state)(statement)

    def apply[StateType, StatementType, OutputType](state: StateType,
                                                    statement: Statement[StatementType])
                                                   (implicit
                                                    interpreter: Interpreter[StateType, StatementType, OutputType]): OutputType =
      interpreter(state, statement)
  }

  def interpret[StateType, StatementType, OutputType](state: StateType,
                                                      statement: Statement[StatementType])
                                                     (implicit
                                                      interpreter: Interpreter[StateType, StatementType, OutputType]): OutputType =
    Interpreter(state, statement)


  /**
    * This is a simple interpreter that operates as a state transition: result of the interpretation
    * is a state update.
    *
    * @tparam StateType     is the incoming and outgoing State type
    * @tparam StatementType is the statement (command) type
    */
  type StateTransitionInterpreter[StateType, StatementType] = Interpreter[StateType, StatementType, StateType]

  object StateTransitionInterpreter {

    def identity[StateType, StatementType]: StateTransitionInterpreter[StateType, StatementType] =
      define(state => _ => state)

    def define[StateType, StatementType](interpretation: StateType => StatementType => StateType): StateTransitionInterpreter[StateType, StatementType] =
      Interpreter.define[StateType, StatementType, StateType](interpretation)
  }

  implicit def hNilStateTransitionInterpreter[StateType]: StateTransitionInterpreter[StateType, HNil] =
    StateTransitionInterpreter.identity

  implicit def hListStateTransitionInterpreter[StateType, H, StatementType <: HList](implicit
                                                                                     interpreterH: StateTransitionInterpreter[StateType, H],
                                                                                     interpreterT: StateTransitionInterpreter[StateType, StatementType]): StateTransitionInterpreter[StateType, H :: StatementType] =
    StateTransitionInterpreter.define(state => statement =>
      interpreterT.interpret(interpreterH.interpret(state, statement.head), statement.tail)
    )
}
