package com.ezmodel.ddd

/**
 * @author gweinbach
 */
object Identify {

  import scala.language.implicitConversions

  type Identify[S, I] = (ValuedState[S] => I)

  implicit def implicitIdentify[S, I](idVal: I): Identify[S, I] = (_ => idVal)
}

