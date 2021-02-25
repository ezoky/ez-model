package com.ezoky.ezmodel.interaction.interpreter

import shapeless._

/**
  * @author gweinbach on 20/02/2021
  * @since 0.2.0
  */
/**
  * ..and then interpreted in terms of state change
  */
private[interaction] trait Interpreting
  extends Parsing {

  trait Interpreter[S, T] {

    def interpret(state: S,
                  statement: T): S

    def apply(state: S,
              statement: Statement[T]): S =
      interpret(state, statement.stated)
  }

  implicit def hNilInterpreter[S]: Interpreter[S, HNil] =
    Interpreter.noop

  implicit def hListInterpreter[S, H, T <: HList](implicit
                                                  interpreterH: Interpreter[S, H],
                                                  interpreterT: Interpreter[S, T]): Interpreter[S, H :: T] =
    new Interpreter[S, H :: T] {
      override def interpret(state: S,
                             statement: H :: T): S =
        interpreterT.interpret(interpreterH.interpret(state, statement.head), statement.tail)
    }

  object Interpreter {

    def noop[S, T]: Interpreter[S, T] =
      new Interpreter[S, T] {
        override def interpret(state: S,
                               statement: T): S = state
      }

    def define[S, T](interpretation: S => T => S): Interpreter[S, T] =
      (state: S, statement: T) => interpretation(state)(statement)

    def apply[S, T](state: S,
                    statement: Statement[T])
                   (implicit
                    interpreter: Interpreter[S, T]): S =
      interpreter(state, statement)
  }


  def interpret[S, T](state: S,
                      statement: Statement[T])
                     (implicit
                      interpreter: Interpreter[S, T]): S =
    Interpreter(state, statement)

}