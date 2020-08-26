package com.ezoky.ezmodel.ddd

import State._
import StateTransition._

/**
 * @author gweinbach
 */
class Command[S](action: => StateTransition[S]) extends (State[S] => State[S]) {

  override def apply(state:State[S]): State[S] = {
    lazy val result = lift(action)(state)
    result
  }
}
