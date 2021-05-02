package com.ezoky.architecture

import cats.Monad

/**
  * @author gweinbach on 22/04/2021
  * @since 0.2.0
  */
trait API {

  type QueryProducing[+T]
  type CommandConsuming[T]
  type CommandConsumingNothing <: CommandConsuming[Nothing]
  type PublisherOf[+T]

  implicit val queryMonad: Monad[QueryProducing]

  def validate[T, A <: QueryProducing[T], Collection[+Element] <: Iterable[Element]](in: Collection[A]): QueryProducing[Iterable[T]]
}
