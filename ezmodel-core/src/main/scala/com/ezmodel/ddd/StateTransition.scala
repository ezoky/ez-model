package com.ezmodel.ddd

import com.ezmodel.ddd.StateTransition.StateTransition

import scala.reflect.ClassTag

/**
 * @author gweinbach
 */
object StateTransition {

  type StateTransition[V, S] = (S => (V, S))

  def map[A, B, S](transition: StateTransition[A, S])(f: A => B): StateTransition[B, S] =
    stateValue => {
      val (a, targetStateValue) = transition(stateValue)
      (f(a), targetStateValue)
    }

}

case class ImmutableTransition[V, S](value: V)(implicit classTag: ClassTag[S]) extends StateTransition[V, S] {
  override def apply(state: S): (V, S) = (value, state)
}