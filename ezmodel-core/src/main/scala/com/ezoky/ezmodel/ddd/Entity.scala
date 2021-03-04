package com.ezoky.ezmodel.ddd

import cats.implicits._
import com.ezoky.ezmodel.ddd.State.State

/**
  * @author gweinbach
  */
object Entity {

  type Entity[+S, +I] = Either[InvalidEntity[S, I], StatefulEntity[S, I]]

  def apply[S, I](state: State[S])(identify: Identify[S, I]): Entity[S, I] = {
    Right(StatefulEntity(state)(identify))
  }
}

sealed abstract class AbstractEntity[+S, +I](val state: State[S])(identify: => Identify[S, I]) {

  import State._

  lazy val id: Either[InvalidState[S], I] = state.map(s => identify(s.value.get))

  lazy val isStateInitial: Either[InvalidState[S], Boolean] = state.map(_.isInitial)

  lazy val isStateFinal: Either[InvalidState[S], Boolean] = state.map(_.isFinal)

  /**
    * Strange behaviour as it will return a left invalid state when invelid (and a right "false" if valid)
    */
  lazy val isStateInvalid: Either[InvalidState[S], Boolean] = state.map(_.isLeft)


  import Entity.Entity

  def +[T >: S](nextState: State[T]): Entity[T, I] = changeState(nextState)

  def changeState[T >: S](nextState: State[T]): Entity[T, I] = {

    val targetState = (state: State[T]) |+| nextState

    if (targetState.map(s => identify(s.value.get.asInstanceOf[S])) != id) {
      // Very ugly !!
      Left(InvalidEntity(IdentityHasMutated, targetState)(identify.asInstanceOf[T => I]))
    }
    else {
      Right(StatefulEntity[T, I](targetState)(identify.asInstanceOf[T => I])) // Very ugly !!
    }
  }

  def hasSameIdentity(other: AbstractEntity[_, _]): Boolean = id.equals(other.id)

  def hasSameState(other: AbstractEntity[_, _]): Boolean = state.equals(other.state)

  def isIdentical(other: AbstractEntity[_, _]): Boolean = hasSameIdentity(other) && hasSameState(other)

  override def equals(other: Any) = other match {
    case otherEntity: AbstractEntity[_, _] => hasSameIdentity(otherEntity)
    case _ => false
  }

  override def hashCode() = id.hashCode()
}


abstract class ErrorCause(reason: String)

case class StatefulEntity[+S, +I](override val state: State[S])(identify: Identify[S, I]) extends AbstractEntity[S, I](
  state)(identify)

case class InvalidEntity[+S, +I](val cause: ErrorCause, override val state: State[S])
                                (identify: Identify[S, I]) extends AbstractEntity[S, I](state)(identify)

case object IdentityHasMutated extends ErrorCause("Identity has mutated")
