package com.ezoky.architecture.monolithic

import cats._
import cats.implicits._
import com.ezoky.architecture.API

/**
  * @author gweinbach on 22/04/2021
  * @since 0.2.0
  */
class MonolithicAPI[EnvType]
  extends API[EnvType, Throwable] {

  override type EffectType[-R, +E, +T] = R => Either[E, T]

  override implicit def effectMonad: Monad[Effect] =
    new Monad[Effect] {

      override def pure[A](x: A): EnvType => Either[Throwable, A] =
        _ => Right(x)

      override def flatMap[A, B](fa: EnvType => Either[Throwable, A])(f: A => EnvType => Either[Throwable, B]): EnvType => Either[Throwable, B] = {
        env =>
          fa(env) match {
            case Left(value) =>
              Left(value)
            case Right(value) =>
              f(value)(env)
          }
      }

      override def tailRecM[A, B](a: A)(f: A => EnvType => Either[Throwable, Either[A, B]]): EnvType => Either[Throwable, B] =
        flatMap(f(a)) {
          case Left(l) => tailRecM(l)(f)
          case Right(r) => pure(r)
        }

    }
}
