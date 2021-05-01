package com.ezoky.architecture.monolithic

import cats.Monad
import com.ezoky.architecture.API

/**
  * @author gweinbach on 22/04/2021
  * @since 0.2.0
  */
trait MonolithicAPI
  extends API {

  type Identity[T] = T

//  implicit override val queryMonad: Monad[Identity] = Monad[Identity]
}
