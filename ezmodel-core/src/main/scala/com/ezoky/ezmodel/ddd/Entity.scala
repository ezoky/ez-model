package com.ezoky.ezmodel.ddd


/**
 * @author gweinbach
 */
sealed case class Entity[I >: AbstractIdentity[_],S >: AbstractState[_]](val identity: I, val state: S = InitialState) {

  def +(nextState: S): Entity[I,S] = changeState(nextState)

  def changeState(nextState: S): Entity[I,S] = state match {
    case FinalState => throw new CannotChangeFromFinalState
    case _ => copy (state = nextState)
  }

  def hasSameIdentity(other: Entity[_,_]) = identity.equals(other.identity)

  def hasSameState(other: Entity[_,_]) = state.equals(other.state)

  def isIdentical(other: Entity[_,_]) = hasSameIdentity(other) && hasSameState(other)

  override def equals(other: Any) = other match {
    case otherEntity: Entity[_,_] => hasSameIdentity(otherEntity)
    case _ => false
  }

  override def hashCode() = identity.hashCode()
}

class CannotChangeFromFinalState extends RuntimeException
