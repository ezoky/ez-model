package com.ezoky.ezmodel.ddd

/**
 * @author gweinbach
 */
trait AbstractState[S] {
}

object State {

  import scala.language.implicitConversions

  implicit def implicitState[S](stateValue: S) = State(stateValue)

  def +(state1: State, state2: State): State = state2 match {
    case NeutralState => state1
    case _ => state1

}

case class State[S](stateValue: S) extends AbstractState[S]

object NeutralState extends AbstractState[Any]

object InitialState extends AbstractState[Any]

object FinalState extends AbstractState[Any]
