package com.ezoky.architecture.monolithic

import cats._
import cats.implicits._
import com.ezoky.architecture.API

/**
  * @author gweinbach on 22/04/2021
  * @since 0.2.0
  */
object MonolithicAPI
  extends API {

  override type QueryProducing[+T] = T

  override type CommandConsuming[T] = T => Unit
  override type CommandConsumingNothing = CommandConsuming[Nothing]
  override type PublisherOf[+T] = T

  override val queryMonad: Monad[Id] = Monad[Id]

  override def validate[T, A <: Id[T], Collection[+Element] <: Iterable[Element]](in: Collection[A]): Iterable[T] =
    in
}
