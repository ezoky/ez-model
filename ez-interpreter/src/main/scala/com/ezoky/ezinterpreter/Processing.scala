package com.ezoky.ezinterpreter

/**
  * @author gweinbach on 21/02/2021
  * @since 0.2.0
  */
trait Processing
  extends Interpreting {

  trait Processor[S, U, F[_]] {

    val state: S

    def process[W, T](whatISay: Say[W])
                     (implicit
                      parser: Parser[W, T],
                      interpreter: Interpreter[S, T, U]): F[U]
  }

  case class StateProcessor[S](state: S)
    extends Processor[S, S, StateProcessor] {

    def process[W, T](whatISay: Say[W])
                     (implicit
                      parser: Parser[W, T],
                      interpreter: Interpreter[S, T, S]): StateProcessor[S] =
      StateProcessor(
        Interpreter(
          state,
          Parser(whatISay)
        )
      )
  }

}
