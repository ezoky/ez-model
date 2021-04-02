package com.ezoky.ezmodel.control

import shapeless.ops.hlist.ToTraversable
import shapeless.{::, HList, HNil}


/**
  * @author gweinbach on 31/03/2021
  * @since 0.2.0
  */
sealed trait ArgsHolder {

  type ValueType

  val asArgs: Seq[Any]
}

case object NoValue extends ArgsHolder {
  override val asArgs: Seq[Any] = Seq.empty[Any]
}

sealed trait ValueHolder[+V]
  extends ArgsHolder {

  override type ValueType <: V

  val value: V
}

object ValueHolder {

  def apply[V](v: V): ValueHolder[V] =
    SingleValueHolder(v)

  def map[V1, V2](v1: ValueHolder[V1])(f: V1 => V2): ValueHolder[V2] =
    ValueHolder(f(v1.value))

  def flatMap[V1, V2](v1: ValueHolder[V1])(f: V1 => ValueHolder[V2]): ValueHolder[V2] =
    f(v1.value)

  def zip[V1, V2](v1: ValueHolder[V1],
                  v2: ValueHolder[V2]): MultipleValueHolder[V1 :: V2 :: HNil] =
    MultipleValueHolder(v1.value, v2.value)

  implicit class ValueHolderHelper[V](v: ValueHolder[V]) {

    def map[V2](f: V => V2): ValueHolder[V2] =
      ValueHolder.map(v)(f)

    def flatMap[V2](f: V => ValueHolder[V2]): ValueHolder[V2] =
      ValueHolder.flatMap(v)(f)

    def zip[V2](value2: ValueHolder[V2]): MultipleValueHolder[V :: V2 :: HNil] =
      ValueHolder.zip(v, value2)
  }

}


final case class SingleValueHolder[+V](value: V)
  extends ValueHolder[V] {

  override val asArgs: Seq[Any] = Seq(value)
}

final case class MultipleValueHolder[V <: HList](val value: V)
                                                (implicit toTraversableAux: ToTraversable.Aux[V, List, Any])
  extends ValueHolder[V] {

  override lazy val asArgs: Seq[Any] = value.toList[Any]
}

object MultipleValueHolder {

  val Empty: MultipleValueHolder[HNil] = MultipleValueHolder()

  implicit class MultipleValueHolderHelper[VH, VT <: HList](value: MultipleValueHolder[VH :: VT]) {

    def head: SingleValueHolder[VH] =
      SingleValueHolder(value.value.head)

    def tail(implicit toTraversableAux: ToTraversable.Aux[VT, List, Any]): MultipleValueHolder[VT] =
      MultipleValueHolder(value.value.tail)
  }

  def apply(): MultipleValueHolder[HNil] =
    MultipleValueHolder(HNil)

  def apply[V1](v1: V1): MultipleValueHolder[V1 :: HNil] =
    MultipleValueHolder(v1 :: HNil)

  def apply[V1, V2](v1: V1, v2: V2): MultipleValueHolder[V1 :: V2 :: HNil] =
    MultipleValueHolder(v1 :: v2 :: HNil)

  def apply[V1, V2, V3](v1: V1, v2: V2, v3: V3): MultipleValueHolder[V1 :: V2 :: V3 :: HNil] =
    MultipleValueHolder(v1 :: v2 :: v3 :: HNil)

  def apply[V1, V2, V3, V4](v1: V1, v2: V2, v3: V3, v4: V4): MultipleValueHolder[V1 :: V2 :: V3 :: V4 :: HNil] =
    MultipleValueHolder(v1 :: v2 :: v3 :: v4 :: HNil)

  def apply[V1, V2, V3, V4, V5](v1: V1, v2: V2, v3: V3, v4: V4, v5: V5): MultipleValueHolder[V1 :: V2 :: V3 :: V4 :: V5 :: HNil] =
    MultipleValueHolder(v1 :: v2 :: v3 :: v4 :: v5 :: HNil)

  def apply[V1, V2, V3, V4, V5, V6](v1: V1, v2: V2, v3: V3, v4: V4, v5: V5, v6: V6): MultipleValueHolder[V1 :: V2 :: V3 :: V4 :: V5 :: V6 :: HNil] =
    MultipleValueHolder(v1 :: v2 :: v3 :: v4 :: v5 :: v6 :: HNil)

}

