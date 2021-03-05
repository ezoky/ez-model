package com.ezoky.ezmodel.ddd

import com.ezoky.ezmodel.ddd.StateTransition.{StateAction, StateTransition}

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

  def lift[S](transition: StateTransition[S]): (State.State[S] => State.State[S]) =
    state => for {
      s <- state
    } yield CommonState[S]({
      for {v <- s.value} yield transition(v)
    }.get)


  //  def lift[V,S](action: StateAction[V,S]): (State[S] => State[(V,S)]) = ???
  //    (state: State[S]) => {
  //      for {
  //        state: ValidState[S] <- state
  //        value: S <- state.value
  //      } yield action(value)
  //    }

}

case class IdentityTransition[S]() extends StateTransition[S] {
  override def apply(state: S): S = state
}

case class IdentityAction[V, S](value: V) extends StateAction[V, S] {
  override def apply(state: S): (V, S) = (value, state)
}