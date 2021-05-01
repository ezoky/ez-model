package com.ezoky

import scala.annotation.StaticAnnotation

/**
  * Annotations intenede to be used for documentation purposes.
  *
  * @author gweinbach on 22/04/2021
  * @since 0.2.0
  */
package object architecture {

  class Services extends StaticAnnotation

  class Command extends StaticAnnotation

  class Query extends StaticAnnotation

  class EventPublisher extends StaticAnnotation

  class PureSideEffect extends StaticAnnotation

  class ValueObject extends StaticAnnotation

  class Entity extends StaticAnnotation

  class Identifier extends StaticAnnotation


  trait eventInterface

  case class producedBy[+ProducerType]() extends eventInterface

  case class consumedBy[+ConsumerType]() extends eventInterface

  class BusinessEvent(eventInterface: eventInterface*) extends StaticAnnotation

  class BusinessState extends StaticAnnotation

  /**
    * Raw data read from the outside as published by other data owners
    */
  class Content extends StaticAnnotation
}
