package com.ezoky.ezmodel.ddd


/**
 * @author gweinbach
 */
sealed case class Entity[I, S](val identity: AbstractIdentity[I], val state: AbstractState[S] = InitialState) {

  def +(nextState: AbstractState[S]): Entity[I, S] = changeState(nextState)

  def changeState(nextState: AbstractState[S]): Entity[I, S] = copy(state = state + nextState)

  def hasSameIdentity(other: Entity[_, _]) = identity.equals(other.identity)

  def hasSameState(other: Entity[_, _]) = state.equals(other.state)

  def isIdentical(other: Entity[_, _]) = hasSameIdentity(other) && hasSameState(other)

  override def equals(other: Any) = other match {
    case otherEntity: Entity[_, _] => hasSameIdentity(otherEntity)
    case _ => false
  }

  override def hashCode() = identity.hashCode()
}

