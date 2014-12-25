package com.ezmodel.ddd

import com.ezmodel.ddd.State._
import com.ezmodel.ddd.StateTransition._

/**
 * @author gweinbach
 */
class Command[S](action: => StateTransition[S]) extends (State[S] => State[S]) {

  override def apply(state:State[S]): State[S] = {
    lazy val result = lift(action)(state)
    result
  }
}
