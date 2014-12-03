package com.ezmodel.ddd

import com.ezmodel.ddd.Behaviour.Behaviour

import scala.reflect.ClassTag

/**
 * @author gweinbach
 */
object Behaviour {

  type Behaviour[S] = (S => S)

}

case class ImmutableBehaviour[S](implicit classTag: ClassTag[S]) extends Behaviour[S] {
  override def apply(v1: S): S = v1
}