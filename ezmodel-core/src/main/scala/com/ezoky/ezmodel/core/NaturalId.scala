package com.ezoky.ezmodel.core

/**
  * Type class.
  *
  * The `NaturalId` of an item provides a way to identify (name) an item.
  * This Id should be unique in the context of the aggregate it is related to since this Id might be used to used as
  * a unique key in Maps.
  *
  * Some common type classes (see [[com.ezoky.ezmodel.core.NaturalId.StandardTypeClasses]]) are provided for default
  * usage.
  * Import them in the context of use:
  * {{{
  * object MyTypeClasses extends com.ezoky.ezmodel.core.NaturalId.StandardTypeClasses
  * import MyTypeClasses._
  * }}}
  *
  * @author gweinbach on 20/02/2021
  * @since 0.2.0
  */
trait NaturalId[T] {

  type IdType

  def apply(t: T): IdType
}

object NaturalId {

  type Aux[T, I] = NaturalId[T] { type IdType = I }

  def define[I, T](naturalId: T => I): NaturalId[T] =
    new NaturalId[T] {

      override type IdType = I

      override def apply(t: T): I =
        naturalId(t)
    }

  type NaturalMap[I <: NaturalId[T], T] = Map[I#IdType, T]

  object NaturalMap {

    def empty[I <: NaturalId[T], T]: NaturalMap[I, T] =
      Map.empty[I#IdType, T]

    def apply[I <: NaturalId[T], T](ts: T*)
                                   (implicit
                                    id: I): NaturalMap[I, T] =
      ts.foldLeft(empty[I, T])((map, t) => map + (id(t) -> t))
  }

  implicit class NaturalMapHelper[I <: NaturalId[T], T](naturalMap: NaturalMap[I, T])
                                                       (implicit
                                                        id: I) {
    def add(t: T): NaturalMap[I, T] =
      naturalMap + (id(t) -> t)

    def remove(t: T): NaturalMap[I, T] =
      naturalMap - id(t)
  }
}