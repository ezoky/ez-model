package com.ezoky.architecture.zioapi

import cats.Monad
import com.ezoky.architecture.API
import zio._

/**
  * @author gweinbach on 29/04/2021
  * @since 0.2.0
  */

object ZIOAPI extends API {

  override type QueryProducing[+T] = Task[T]
  override type CommandConsuming[T] = RIO[T, Nothing]
  override type PublisherOf[T] = Task[T]

  override implicit val queryMonad: Monad[Task] =
    new Monad[Task] {
      override def pure[A](x: A): Task[A] =
        Task.succeed(x)

      override def flatMap[A, B](fa: Task[A])(f: A => Task[B]): Task[B] =
        fa.flatMap(f)

      override def tailRecM[A, B](a: A)(f: A => Task[Either[A, B]]): Task[B] =
        ZIO.effectSuspend(f(a)).flatMap {
          case Left(l) => tailRecM(l)(f)
          case Right(r) => ZIO.succeed(r)
        }
    }

  override def validate[T, A <: Task[T], Collection[+Element] <: Iterable[Element]](in: Collection[A]): Task[Iterable[T]] =
    ZIO.partition(in)(identity).flatMap { case (es, bs) =>
      if (es.isEmpty) Task.succeed(bs)
      else Task.fail(es.head)
    }
}
