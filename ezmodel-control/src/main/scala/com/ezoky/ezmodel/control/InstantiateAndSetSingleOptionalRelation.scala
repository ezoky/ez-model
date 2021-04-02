package com.ezoky.ezmodel.control

import com.ezoky.ezmodel.control.ActionnableController._
import com.softwaremill.quicklens.PathLazyModify

import scala.reflect.runtime.universe.TypeTag

/**
  * @author gweinbach on 31/03/2021
  * @since 0.2.0
  */
case class InstantiateAndSetSingleOptionalRelation[T, R: TypeTag] private[control](pathModify: PathLazyModify[T, Option[R]])
  extends ActionnableController[(T, ArgsHolder), SingleValueHolder[T]] {

  private def combinedControllers(mainObject: T): ActionnableController[ArgsHolder, SingleValueHolder[T]] =
    combine(
      InstantiateObject[R](),
      SetSingleRelation.build[T, Option[R]](pathModify)
    )(relationValue => (mainObject, relationValue))

  override def action(input: (T, ArgsHolder)): SingleValueHolder[T] =
    combinedControllers(input._1)(input._2)

  def apply(mainObject: T,
            constructorArgs: ArgsHolder): SingleValueHolder[T] =
    apply((mainObject, constructorArgs))
}


object InstantiateAndSetSingleOptionalRelation {

  import com.softwaremill.quicklens.QuicklensMacros

  import scala.annotation.nowarn
  import scala.language.experimental.macros
  import scala.reflect.macros.blackbox

  /**
    * @param relation must be of the form `_.path.to.value`
    */
  def apply[T, R](relation: T => Option[R]): InstantiateAndSetSingleOptionalRelation[T, R] =
  macro singleOptionalRelationInstantiator[T, R]

  /**
    * Unfortunately, this cannot be named `apply()` (cf. "privatization" of default `apply()`)
    */
  def build[T, R: TypeTag](pathModify: PathLazyModify[T, Option[R]]): InstantiateAndSetSingleOptionalRelation[T, R] =
    new InstantiateAndSetSingleOptionalRelation(pathModify)

  /**
    * This "privatization" of apply is here to override default `apply()` and avoid Type problems with the
    * "macroified" `apply()`
    */
  @nowarn
  private def apply[T, R: TypeTag](pathModify: PathLazyModify[T, Option[R]]): InstantiateAndSetSingleOptionalRelation[T, R] =
    new InstantiateAndSetSingleOptionalRelation(pathModify)


  def singleOptionalRelationInstantiator[T: c.WeakTypeTag, R: c.WeakTypeTag](c: blackbox.Context)(
    relation: c.Expr[T => Option[R]]
  ): c.Tree = {
    val pathModify = QuicklensMacros.modifyLazy_impl[T, Option[R]](c)(relation)
    //    val self = c.prefix
    val tType = c.weakTypeTag[T]
    val rType = c.weakTypeTag[R]
    import c.universe._
    q"new _root_.com.ezoky.ezmodel.control.InstantiateAndSetSingleOptionalRelation[$tType,$rType]($pathModify)"
  }
}