package com.ezoky.ezmodel.ddd

import scalaz.Monoid

/**
 * @author gweinbach
 */
trait AbstractState[-S] {

  import State._

  def +[T <: S](s:AbstractState[T]):AbstractState[T] = StateMonoid[T].append(this,s)
}

object State {

  import scala.language.implicitConversions

  implicit def implicitState[S](stateValue: S):AbstractState[S] = State(stateValue)

  implicit def StateMonoid[S]: Monoid[AbstractState[S]] = new Monoid[AbstractState[S]] {

    override def zero: AbstractState[S] = IdentityState

    override def append(f1: AbstractState[S], f2: => AbstractState[S]): AbstractState[S] = f2 match {
      case IdentityState => f1
      case _ => f1 match {
        case FinalState => throw new CannotChangeFromFinalState
        case _ => f2
      }
    }
  }
}

case class State[S](stateValue: S) extends AbstractState[S]

object IdentityState extends AbstractState[Any]

object InitialState extends AbstractState[Any]

object FinalState extends AbstractState[Any]

class CannotChangeFromFinalState extends RuntimeException
