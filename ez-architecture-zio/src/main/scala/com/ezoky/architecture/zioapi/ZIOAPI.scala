package com.ezoky.architecture.zioapi

import cats._
import com.ezoky.architecture.API
import zio._

/**
  * @author gweinbach on 29/04/2021
  * @since 0.2.0
  */

class ZIOAPI[EnvType]
  extends API[EnvType, Throwable] {

  override type EffectType[-R, +E, +T] = ZIO[R, E, T]

  override def fail(e: Throwable): ZIO[Any, Throwable, Nothing] =
    ZIO.fail(e)

  override implicit def effectMonad: Monad[Effect] = new Monad[Effect] {

    override def pure[A](x: A): Effect[A] =
      ZIO.succeed(x)

    override def flatMap[A, B](fa: Effect[A])(f: A => Effect[B]): Effect[B] =
      fa.flatMap(f)

    override def tailRecM[A, B](a: A)(f: A => Effect[Either[A, B]]): Effect[B] =
      ZIO.effectSuspend(f(a)).flatMap {
        case Left(l) => tailRecM(l)(f)
        case Right(r) => ZIO.succeed(r)
      }
  }
}
