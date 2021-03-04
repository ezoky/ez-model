package com.ezoky.ezmodel

/**
 * @author gweinbach
 */
package object ddd {

  type Identify[-S, +I] = (S => I)

  def stateIdentify[S]: Identify[S, S] = ((s: S) => s)
  def constantIdentify[S,I](constant: I): Identify[S,I] = ((s: S) => constant)

  implicit def implicitIdentify[S, I](idVal: I): Identify[S, I] = constantIdentify[S,I](idVal)



  import State.State

  implicit def implicitState[S](validState: ValidState[S]): State[S] = Right(validState)
  implicit def implicitState[S](invalidState: InvalidState[S]): State[S] = Left(invalidState)
  implicit def implicitState[S](stateValue: S): State[S] = Right(CommonState[S](stateValue))



  import Entity.Entity

  /**
   * This implicit to be able to use partial Entity as if it was a complete one.
   */
  implicit def implicitCurriedEntity[S](f: Identify[S, S] => Entity[S, S]): Entity[S, S] = f(stateIdentify[S])

  /**
   * This is only possible because InvalidEntity and StatefulEntity have a common ancestor (AbstractEntity)
   */
  implicit def entityProjection[S, I](entity: Entity[S, I]): AbstractEntity[S, I] = entity.fold[AbstractEntity[S, I]](l => l, r => r)

}
