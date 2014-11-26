package com.ezoky.ezmodel.ddd

/**
 * @author gweinbach
 */
trait AbstractIdentity[-I] {
}

object Identity {
  import scala.language.implicitConversions

  implicit def implicitIdentity[I](idValue:I):AbstractIdentity[I] = Identity(idValue)
}
case class Identity[I](idValue: I) extends AbstractIdentity[I]
