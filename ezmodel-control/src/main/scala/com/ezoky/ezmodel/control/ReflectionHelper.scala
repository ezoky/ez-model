package com.ezoky.ezmodel.control

import cats.syntax.option.{none, _}
import com.ezoky.ezlogging.{EzLoggableClass, Safe}

import scala.reflect.runtime.universe._

/**
  * @author gweinbach on 02/04/2021
  * @since 0.2.0
  */
object ReflectionHelper
  extends EzLoggableClass {

  def instantiateWithArgs[T: TypeTag](args: Seq[Any]): Option[T] =
    for {
      objectType <- Safe(typeTag[T].tpe)
      companionType <- Safe(objectType.companion)
      classSymbol <- Safe(objectType.typeSymbol.asClass)
      moduleSymbol <- Safe(classSymbol.companion.asModule)
      constructorSymbol <- Safe(objectType.decl(termNames.CONSTRUCTOR).asMethod)

      rootMirror <- Safe(runtimeMirror(getClass.getClassLoader))
      classMirror <- Safe(rootMirror.reflectClass(classSymbol))
      constructorMirror <- Safe(classMirror.reflectConstructor(constructorSymbol))
      instanceMirror <- Safe(rootMirror.reflect(rootMirror.reflectModule(moduleSymbol).instance))

      methodArgs <- buildArgs(args, constructorSymbol, companionType, instanceMirror)

      instance <- Safe(constructorMirror(methodArgs: _*).asInstanceOf[T])
    } yield instance


  private def buildArgs(inputArgs: Seq[Any],
                        method: MethodSymbol,
                        companionType: Type,
                        instanceMirror: InstanceMirror): Option[Seq[Any]] = {

    trace(s"buildArgs: method.name=${method.name}, method.fullName=${method.fullName}, method.paramLists")
    trace(s"buildArgs: method.paramLists=${method.paramLists.flatten}")

    val nbArgs = method.paramLists.flatten.size
    val truncatedArgs = inputArgs.take(nbArgs)
    if (truncatedArgs.size == nbArgs) {
      Some(truncatedArgs)
    }
    else {
      val defaultArgs = defaultMethodArgs(method, companionType, instanceMirror)
      (truncatedArgs.size to (nbArgs - 1)).foldLeft(truncatedArgs.some) {
        case (None, i) =>
          None
        case (Some(seq), i) =>
          defaultArgs.get(i).fold {
            error(s"Failed to build argument list: no input arg or default arg value for arg #$i of method ${method.fullName}")
            none[Seq[Any]]
          }(defaultArg => Some(seq :+ defaultArg))
      }
    }
  }

  private def defaultMethodArgs(method: MethodSymbol,
                                companionType: Type,
                                instanceMirror: InstanceMirror): Map[Int, Any] = {

    trace(s"defaultMethodArgs: method.name=${method.name}, method.fullName=${method.fullName}")

    method.paramLists.flatten.zipWithIndex.flatMap {
      case (arg, i) =>
        trace(s"defaultMethodArgs: arg.name=${arg.name}, arg.fullName=${arg.fullName}")

        if (arg.asTerm.isParamWithDefault) {
          trace(s"defaultMethodArgs: arg ${arg.name} has default value")

          val defaultArgMethod = companionType.decl(
            TermName("apply$default$" + (i + 1).toString)
          ).asMethod
          val defaultArgValue = instanceMirror.reflectMethod(defaultArgMethod)()

          trace(s"defaultMethodArgs: arg ${arg.name}.defaultValue = $defaultArgValue")

          Some(i -> defaultArgValue)
        }
        else {
          None
        }
    }.toMap[Int, Any]
  }
}