package com.ezmodel.ddd

import scalaz.{-\/, Monoid, \/, \/-}


/**
 * @author gweinbach
 */
trait State[+S] {

  val value: S

  val isValued: Boolean

  val isInitial: Boolean

  val isFinal: Boolean

  val isInvalid: Boolean

  type State[+S] = InvalidState[S] \/ ValidState[S]

  import scala.language.implicitConversions

  implicit def StateMonoid[S]: Monoid[State[S]] = new Monoid[State[S]] {

    override def zero: State[S] = \/-(IdentityState)

    override def append(f1: State[S], f2: => State[S]): State[S] = (f1, f2) match {

      case (-\/(_), _) => f1 // no change of state after an InvalidState is reached
      case (_, \/-(IdentityState)) => f1
      case (_, to: InitialState[S]) => -\/(CannotChangeToInitialState(to.value))
      case (from: FinalState[S], _) => -\/(CannotChangeFromFinalState[S](from.value))
      case _ => f2
    }
  }

}

/*
object ValuedState {

  def apply[S](stateValue: S): ValuedState[S] = new CommonState(stateValue)

  //def unapply[S](initialState: InitialState[S]): Option[InitialState[S]] = Option(new InitialState(initialState.get))

  //def unapply[S](valuedState: ValuedState[S]): Option[ValuedState[S]] = Option(ValuedState(valuedState.get))
}
*/


abstract class ValuedState[+S](protected val stateValue: S) extends State[S] {

  override val isValued = true

  override val value = stateValue

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

trait ValidState[+S] extends State[S] {
  override val isInvalid = false

  def +[T >: S](s: State[T]): State[T] = implicitly[Monoid[State[T]]].append(\/-(this), s)
}

case object IdentityState extends ValidState[Nothing] {

  override val isValued = false

  override def value = throw new NoSuchElementException("IdentityState.get")

  override val isInitial = false
  override val isFinal = false
}

class CommonState[+S](override val stateValue: S) extends ValuedState {
  override val isInitial = false
  override val isFinal = false
  override val isInvalid = false
}

trait InitialState[+S] extends ValidState[S] {
  override val isInitial = true
}

trait FinalState[+S] extends ValidState[S] {
  override val isFinal = true
}

trait InvalidState[+S] extends State[S] {
  override val isInvalid = true
  override val isFinal = true
  override val isInitial = false
}

class ErrorMessageInvalidState[+S](val cause: String, override val stateValue: S) extends ValuedState[S](stateValue) with InvalidState[S]

class ExceptionInvalidState[+S](val cause: Exception, override val stateValue: S) extends ValuedState[S](stateValue) with InvalidState[S]

final case class CannotChangeToInitialState[+S](override val stateValue: S) extends ErrorMessageInvalidState[S]("Cannot change to InitialState", stateValue)

final case class CannotChangeFromFinalState[+S](override val stateValue: S) extends ErrorMessageInvalidState[S]("Cannot change from FinalState", stateValue)
