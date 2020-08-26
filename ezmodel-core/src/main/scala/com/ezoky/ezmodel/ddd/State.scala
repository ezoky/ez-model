package com.ezoky.ezmodel.ddd

import cats._
import cats.implicits._

/**
 * @author gweinbach
 */

object State {

  type State[+S] = Either[InvalidState[S], ValidState[S]]

  def apply[S](stateOption: Option[S]): State[S] =
    stateOption match {
      case Some(stateValue) => apply[S](stateValue)
      case None => Left(emptyState[S])
    }

  def apply[S](stateValue: S): State[S] = Right(CommonState(stateValue))

  def emptyState[S]:InvalidState[S] = EmptyState

  import scala.language.implicitConversions

  implicit def StateMonoid[S]: Monoid[State[S]] = new Monoid[State[S]] {

    override def empty: State[S] = Right(IdentityState)

    override def combine(f1: State[S], f2: State[S]): State[S] = (f1, f2) match {

      case (Left(_), _) => f1 // no change of state after an InvalidState is reached
      case (_, Right(IdentityState)) => f1
      case (_, Right(to: InitialState[S])) => Left(CannotChangeToInitialState(to.value.get))
      case (Right(from: FinalState[S]), _) => Left(CannotChangeFromFinalState(from.value.get))
      case _ => f2
    }
  }

  implicit def disjunctionAsState[S](d: Either[InvalidState[S], ValidState[S]]): State[S] = d
}

trait AbstractState[+S] {

  val value: Option[S]

  val isValued: Boolean

  val isInitial: Boolean

  val isFinal: Boolean

  val isInvalid: Boolean
}

trait ValidState[+S] extends AbstractState[S] {

  override val isInvalid = false

  import com.ezoky.ezmodel.ddd.State._

  def +[T >: S](s: State[T]): State[T] = (Right(this): State[T]) |+| s
}

case object IdentityState extends ValidState[Nothing] {

  override val isValued = false
  override val value = None

  override val isInitial = false
  override val isFinal = false
}

trait InitialState[+S] extends ValidState[S] {
  override val isInitial = true
}

trait FinalState[+S] extends ValidState[S] {
  override val isFinal = true
}

trait InvalidState[+S] extends AbstractState[S] {
  override val isInvalid = true
  override val isFinal = true
  override val isInitial = false

  import com.ezoky.ezmodel.ddd.State._

  def +[T >: S](s: State[T]): State[T] = (Left(this): State[T]) |+| s
}


// Valid States

abstract class ValuedState[+S](protected val stateValue: S) extends AbstractState[S] {

  override val isValued = true
  override val value = Some(stateValue)


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


case class CommonState[+S](override val stateValue: S) extends ValuedState[S](stateValue) with ValidState[S] {
  override val isInitial = false
  override val isFinal = false
  override val isInvalid = false
}

case class CommonInitialState[+S](override val stateValue: S) extends ValuedState[S](stateValue) with InitialState[S] {
  override val isFinal = false
}

case class CommonFinalState[+S](override val stateValue: S) extends ValuedState[S](stateValue) with FinalState[S] {
  override val isInitial = false
}


// Invalid states

case object EmptyState extends InvalidState[Nothing] {
  override val value = None
  override val isValued: Boolean = false
}

class ErrorMessageInvalidState[+S](val cause: String, override val stateValue: S) extends ValuedState[S](stateValue) with InvalidState[S]

class ExceptionInvalidState[+S](val cause: Exception, override val stateValue: S) extends ValuedState[S](stateValue) with InvalidState[S]

final case class CannotChangeToInitialState[+S](override val stateValue: S) extends ErrorMessageInvalidState[S]("Cannot change to InitialState", stateValue)

final case class CannotChangeFromFinalState[+S](override val stateValue: S) extends ErrorMessageInvalidState[S]("Cannot change from FinalState", stateValue)
