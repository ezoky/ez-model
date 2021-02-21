package com.ezoky.ezmodel.interaction

import com.ezoky.ezmodel.interaction.Modelling._

/**
  * @author gweinbach on 21/02/2021
  * @since 0.2.0
  */
case class ModellingSession(state: ModellingState) {

  def process[W, T](whatISay: Say[W])
                   (implicit
                    parser: W => Statement {type StatementType = T},
                    interpreter: Interpreter[ModellingState, T]): ModellingSession = {
//    val statement: Statement.Aux[T] = Parser(whatISay)
//    Interpreter(state, statement)
    this
  }
}
