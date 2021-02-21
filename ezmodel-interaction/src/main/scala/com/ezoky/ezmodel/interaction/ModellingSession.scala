package com.ezoky.ezmodel.interaction

import com.ezoky.ezmodel.interaction.Modelling._

/**
  * @author gweinbach on 21/02/2021
  * @since 0.2.0
  */
case class ModellingSession(state: ModellingState) {

  def process[W, T](whatISay: Say[W])
                   (implicit
                    parser: Parser[W, T],
                    interpreter: Interpreter[ModellingState, T]): ModellingSession = {
    ModellingSession(
      Interpreter(
        state,
        Parser(whatISay)
      )
    )
  }
}
