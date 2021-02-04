package com.ezoky.ezmodel

/**
 * @author gweinbach on 04/02/2021
 * @since 0.2.0 */
package object interaction {

  case class Say[T](something: T)

  case object CurrentModel {
    def contains[T](something: T): Boolean =
      false
  }

}
