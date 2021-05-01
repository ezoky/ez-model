package com.ezoky.architecture

import cats.Monad

/**
  * @author gweinbach on 22/04/2021
  * @since 0.2.0
  */
trait API {

  type QueryProducing[+T]
  type CommandConsuming[-T]
  type CommandConsumingNothing <: CommandConsuming[Nothing]
  type PublisherOf[+T]

  implicit val queryMonad: Monad[QueryProducing]

  def validate[T, A <: QueryProducing[T], Collection[+Element] <: Iterable[Element]](in: Collection[A]): QueryProducing[Iterable[T]]
}

//class QueryProducing[Q[_]: Monad, T]

abstract class APIImpl[Q[+_] : Monad, C[-_], P[+_]]
  extends API {

  override type QueryProducing[+T] = Q[T]
  override type CommandConsuming[-T] = C[T]
  override type PublisherOf[+T] = P[T]

  override val queryMonad: Monad[QueryProducing] = Monad[Q]
}
