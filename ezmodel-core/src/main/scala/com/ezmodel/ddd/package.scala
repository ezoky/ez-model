package com.ezmodel

/**
 * @author gweinbach
 */
package object ddd {

  import scala.language.implicitConversions

  implicit def implicitState[S](stateValue: S): ValuedState[S] = ValuedState[S](stateValue)


  type Identify[S, I] = (S => I)

  implicit def implicitIdentify[S, I](idVal: I): Identify[S, I] = (_ => idVal)

  def defaultIdentify[S]: Identify[S, S] = ((s: S) => s)


  /**
   * This implicit to be able to use partial Entity as if it was a complete one.
   */
  implicit def implicitCurriedEntity[S](f: Identify[S, S] => Entity[S, S]): Entity[S, S] = f(defaultIdentify[S])

  implicit def implicitOptionEntity[S,I](option:Option[Entity[S,I]]): Entity[S,I] = option.getOrElse(InvalidEntity)
}
