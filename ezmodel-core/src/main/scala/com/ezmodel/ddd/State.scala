package com.ezmodel.ddd

import scalaz.Monoid

/**
 * @author gweinbach
 */
sealed trait State[-S] {

  def +[T <: S](s:State[T]):State[T] = implicitly[Monoid[State[T]]].append(this,s)
}

object State {

  import scala.language.implicitConversions

  implicit def StateMonoid[S]: Monoid[State[S]] = new Monoid[State[S]] {

    override def zero: State[S] = IdentityState

    @throws(classOf[CannotChangeToInitialState])
    @throws(classOf[CannotChangeFromFinalState])
    override def append(f1: State[S], f2: => State[S]): State[S] = f2 match {
      case IdentityState => f1
      case InitialState(_) => throw new CannotChangeToInitialState()
      case _ => f1 match {
        case FinalState(_) => throw new CannotChangeFromFinalState()
        case _ => f2
      }
    }
  }
}

final class CannotChangeFromFinalState extends RuntimeException

final class CannotChangeToInitialState extends RuntimeException

object ValuedState {

  import scala.language.implicitConversions

  implicit def implicitState[S](stateValue: S): ValuedState[S] = ValuedState[S](stateValue)

  def apply[S](stateValue:S): ValuedState[S] = new ValuedState(stateValue)

  def unapply[S](stateValue:S):Option[ValuedState[S]] = Option(ValuedState(stateValue))
}

class ValuedState[S](val stateValue: S) extends State[S] {

  def canEqual(other: Any): Boolean = other.isInstanceOf[ValuedState[S]]

  override def equals(other: Any): Boolean = other match {
    case that: ValuedState[S] =>
      (that canEqual this) &&
        stateValue == that.stateValue
    case _ => false
  }

  override def hashCode(): Int = {
    val state = Seq(stateValue)
    state.map(_.hashCode()).foldLeft(0)((a, b) => 31 * a + b)
  }
}

case class InitialState[S](override val stateValue: S) extends ValuedState[S](stateValue)

case class FinalState[S](override val stateValue: S) extends ValuedState[S](stateValue)

case object IdentityState extends State[Any]

