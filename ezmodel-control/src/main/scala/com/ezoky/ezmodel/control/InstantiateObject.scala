package com.ezoky.ezmodel.control

import scala.reflect.runtime.universe._

/**
  * @author gweinbach on 31/03/2021
  * @since 0.2.0
  */
case class InstantiateObject[T: TypeTag]()
  extends ActionnableController[ArgsHolder, SingleValueHolder[Option[T]]] {

  override def action(input: ArgsHolder): SingleValueHolder[Option[T]] =
    SingleValueHolder(
      ReflectionHelper.instantiateWithArgs(input.asArgs)
    )
}
