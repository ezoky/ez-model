package com.ezoky.architecture.zioapi

import cats.Monad
import com.ezoky.architecture.APIImpl
import com.ezoky.architecture.zioapi.ZIOAPI._
import zio._

/**
  * @author gweinbach on 29/04/2021
  * @since 0.2.0
  */

abstract class ZIOAPI
  extends APIImpl[ZIOQueryProducing, ZIOCommandConsuming, ZIOPublisherOf]

object ZIOAPI {

  type ZIOQueryProducing[T] = Task[T]
  type ZIOCommandConsuming[T] = RIO[T, Nothing]
  type ZIOPublisherOf[T] = Task[T]

  implicit val queryMonad: Monad[Task] =
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
}
