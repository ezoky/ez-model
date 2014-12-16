package com.ezmodel.ddd

import scalaz.\/

/**
 * @author gweinbach
 */
sealed case class Entity[+S, +I](state: State[S])(identify: Identify[S, I]) {

  type Entity[+S,+I] = \/[InvalidEntity,Entity[S,I]]

  lazy val id = identify(state.value)

  lazy val isStateInitial = state.isInitial

  lazy val isStateFinal = state.isFinal

  lazy val isStateInvalid = state.isInvalid

  def +[T >: S](nextState: State[T]): Entity[T, I] = changeState(nextState)

  def changeState[T >: S](nextState: State[T]): Entity[T, I] = {
    val targetState = state + nextState
     if (identify(targetState.value) != id) {
        None // throw new EntityIdentityMustNotMutate()
      }
      else {
        Some(Entity(valuedState)(identify))
      }
    }
    else {
      case _ => None // throw new TargetStateHasNoValue()
    }
  }

  def hasSameIdentity(other: Entity[_, _]) = id.equals(other.id)

  def hasSameState(other: Entity[_, _]) = state.equals(other.state)

  def isIdentical(other: Entity[_, _]) = hasSameIdentity(other) && hasSameState(other)

  override def equals(other: Any) = other match {
    case otherEntity: Entity[_, _] => hasSameIdentity(otherEntity)
    case _ => false
  }

  override def hashCode() = id.hashCode()
}

case class InvalidEntity(cause: String) extends Entity[Nothing,Nothing](null)(defaultIdentify)
