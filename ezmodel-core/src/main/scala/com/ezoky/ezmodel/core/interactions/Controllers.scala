package com.ezoky.ezmodel.core.interactions

/**
  * @author gweinbach on 23/03/2021
  * @since 0.2.0
  */
trait Controllers {

  trait InteractionController[+T] {
    val name: InteractionName
  }

  trait SingleInstanceController[+T]
    extends InteractionController[T] {

    def currentObject: Option[T]
  }

  trait MultipleInstanceController[+T]
    extends InteractionController[T] {

    def objects: Seq[T]
  }

  trait InstanceSelectionController[T]
    extends InteractionController[T]
}
