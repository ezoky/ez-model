package com.ezmodel.ddd

/**
 * @author gweinbach
 */
object Identity {

  import scala.language.implicitConversions

  type Identity[S, I] = (ValuedState[S] => I)

  implicit def implicitIdentity[S, I](idVal: I): Identity[S, I] = (_ => idVal)
}

