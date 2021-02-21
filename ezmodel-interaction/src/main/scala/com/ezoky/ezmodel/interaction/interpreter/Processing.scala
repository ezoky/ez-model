package com.ezoky.ezmodel.interaction.interpreter

/**
  * @author gweinbach on 21/02/2021
  * @since 0.2.0
  */
private[interaction] trait Processing
  extends Interpreting {

  case class Processor[S](state: S) {

    def process[W, T](whatISay: Say[W])
                     (implicit
                      parser: Parser[W, T],
                      interpreter: Interpreter[S, T]): Processor[S] =
      Processor(
        Interpreter(
          state,
          Parser(whatISay)
        )
      )
  }
}
