package com.ezoky.architecture

import cats._
import cats.implicits._

/**
  * @author gweinbach on 22/04/2021
  * @since 0.2.0
  */
trait API[EnvType, ErrorType] {

  type EffectType[-R, +E, +T]

  type Effect[+T] = EffectType[EnvType, ErrorType, T]
  implicit def effectMonad: Monad[Effect]

  type QueryProducing[T] = Effect[T]

  type CommandConsuming[T] = Effect[T] => Unit
  type CommandConsumingNothing = CommandConsuming[Nothing]

  type PublisherOf[T] = Unit => Effect[T]

}
