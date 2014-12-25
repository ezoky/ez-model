package com.ezmodel.ddd

import com.ezmodel.ddd.State.State
import com.ezmodel.ddd.StateTransition.{StateAction, StateTransition}

import scala.reflect.ClassTag


/**
 * @author gweinbach
 */
object StateTransition {

  type StateTransition[S] = (S => S)

  type StateAction[V, S] = (S => (V, S))

  def map[A, B, S](transition: StateAction[A, S])(f: A => B): StateAction[B, S] =
    stateValue => {
      val (a, targetStateValue) = transition(stateValue)
      (f(a), targetStateValue)
    }

  def lift[S](transition: StateTransition[S]) : (State[S] => State[S]) = state => (state map (x => CommonState(transition(x.value.get))))

  //def lift[V,S](action: StateAction[V,S]) : (State[S] => (V,State[S])) = _ map action
}

case class IdentityTransition[S](implicit classTag: ClassTag[S]) extends StateTransition[S] {
  override def apply(state: S): S = state
}

case class IdentityAction[V, S](value: V)(implicit classTag: ClassTag[S]) extends StateAction[V, S] {
  override def apply(state: S): (V, S) = (value, state)
}