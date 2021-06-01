package com.ezoky.architecture

import cats._

/**
  * @author gweinbach on 22/04/2021
  * @since 0.2.0
  */
trait API[EnvType, ErrorType] {

  type EffectType[-R, +E, +T]

  type Effect[+T] = EffectType[EnvType, ErrorType, T]

  implicit def effectMonad: Monad[Effect]

  def succeed[T](t: T): Effect[T] =
    effectMonad.pure(t)

  def fail(e: ErrorType): EffectType[Any, ErrorType, Nothing]

  type QueryProducing[T] = Effect[T]

  type CommandConsuming[T] = Effect[T] => Effect[Unit]
  type CommandConsumingNothing = CommandConsuming[Unit]

  type PublisherOf[T] = Unit => Effect[T]

}
