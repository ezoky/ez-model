package com.ezoky.ezmodel.ddd

/**
 * @author gweinbach
 */
trait AbstractState[S] {
}

object State {
  import scala.language.implicitConversions

  implicit def implicitState[S](stateValue:S) = State(stateValue)
}
case class State[S](stateValue: S) extends AbstractState[S]

object InitialState extends AbstractState[Any]

object FinalState extends AbstractState[Any]
