package com.ezoky.commons

/**
  * @author gweinbach on 25/02/2021
  * @since 0.2.0
  */
trait Mergers {

  /**
    * Type class
    */
  trait Merger[T] {

    def merge(t1: T, t2: T): T
  }

  object Merger {
    
    def define[T](merge: (T, T) => T): Merger[T] =
      (t1: T, t2: T) => merge(t1, t2)

    def apply[T: Merger]: Merger[T] =
      implicitly[Merger[T]]
  }

  implicit class MergeableHelper[T: Merger](t: T) {

    def mergeWith(other: T): T =
      Merger[T].merge(t, other)
  }
}
