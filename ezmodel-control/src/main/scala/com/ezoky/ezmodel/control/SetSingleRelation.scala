package com.ezoky.ezmodel.control

import com.softwaremill.quicklens.PathLazyModify


/**
  * @author gweinbach on 31/03/2021
  * @since 0.2.0
  */
case class SetSingleRelation[T, R] private[control](pathModify: PathLazyModify[T, R])
  extends ActionnableController[(T, SingleValueHolder[R]), SingleValueHolder[T]] {

  override def action(input: (T, SingleValueHolder[R])): SingleValueHolder[T] =
    SingleValueHolder(pathModify.setTo(input._2.value)(input._1))

  def apply(mainObject: T,
            values: SingleValueHolder[R]): SingleValueHolder[T] =
    apply((mainObject, values))
}

object SetSingleRelation {

  import com.softwaremill.quicklens.QuicklensMacros

  import scala.annotation.nowarn
  import scala.language.experimental.macros
  import scala.reflect.macros.blackbox

  /**
    * @param relation  must be of the form `_.path.to.value`
    */
  def apply[T, R](relation: T => R): SetSingleRelation[T, R] =
  macro singleRelationSetter[T, R]

  /**
    * Unfortunately, this cannot be named `apply` (cf. "privatization" of default `apply`)
    */
  def build[T, R](pathModify: PathLazyModify[T, R]): SetSingleRelation[T, R] =
    new SetSingleRelation(pathModify)

  /**
    * This "privatization" of apply is here to override default apply and avoid Type problems with the
    * "macroified" apply()
    */
  @nowarn
  private def apply[T, R](pathModify: PathLazyModify[T, R]): SetSingleRelation[T, R] =
    new SetSingleRelation(pathModify)


  def singleRelationSetter[T: c.WeakTypeTag, R: c.WeakTypeTag](c: blackbox.Context)(
    relation: c.Expr[T => R]
  ): c.Tree = {
    val pathModify = QuicklensMacros.modifyLazy_impl[T, R](c)(relation)
    //    val self = c.prefix
    val tType = c.weakTypeTag[T]
    val rType = c.weakTypeTag[R]
    import c.universe._
    q"new _root_.com.ezoky.ezmodel.control.SetSingleRelation[$tType,$rType]($pathModify)"
  }

}