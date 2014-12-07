package com.ezmodel.ddd

/**
 * @author gweinbach
 */
sealed case class Entity[S, I](state: ValuedState[S])(identify: Identify[S, I]) {

  lazy val id = identify(state.stateValue)

  lazy val isInitial = state match {
    case InitialState(_) => true
    case _ => false
  }

  lazy val isFinal = state match {
    case FinalState(_) => true
    case _ => false
  }

  def +(nextState: State[S]): Entity[S, I] = changeState(nextState)

  @throws(classOf[EntityIdentityMustNotMutate])
  @throws(classOf[TargetStateHasNoValue])
  def changeState(nextState: State[S]): Entity[S, I] = {
    val targetState: State[S] = state + nextState
    targetState match {
      case ValuedState(_) =>
        val valuedState = targetState.asInstanceOf[ValuedState[S]]
        if (identify(valuedState.stateValue) != id) {
          throw new EntityIdentityMustNotMutate()
        }
        Entity(valuedState)(identify)

      case _ => throw new TargetStateHasNoValue()
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

final class EntityIdentityMustNotMutate() extends RuntimeException

final class TargetStateHasNoValue() extends RuntimeException
