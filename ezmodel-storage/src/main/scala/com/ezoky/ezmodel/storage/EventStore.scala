package com.ezoky.ezmodel.storage

import com.github.nscala_time.time.Imports.richReadableInstant
import org.joda.time.DateTime

import scala.collection.immutable.Queue
import scala.reflect.ClassTag

case class Event[T](entityVersion: T, date: DateTime = DateTime.now)

object EventStore {
  private var eventStores: Map[Any, EventStore] = Map()

  def apply(key: Any): EventStore = {
    eventStores.getOrElse(key, {
      val newEventStore: EventStore = new EventStore()
      eventStores += (key -> newEventStore)
      newEventStore
    })
  }
}

class EventStore extends Publisher {

  var events: List[Event[_]] = Nil

  def reset() = {
    events = Nil
  }

  def store(entityVersion: Any): Unit = {
    val event = Event(entityVersion)
    events = event :: events
    enqueue(event)
  }

  def queryBetween[T](from: DateTime, to: DateTime)(implicit t: ClassTag[T]): List[Event[_]] = {
    val entityClass = t.runtimeClass
    events.filter(event => event.date > from && event.date <= to && event.entityVersion.getClass.isAssignableFrom(entityClass))
  }

  def queryTo[T](to: DateTime)(implicit t: ClassTag[T]): List[Event[_]] = {
    val entityClass = t.runtimeClass
    events.filter(event => event.date <= to && event.entityVersion.getClass.isAssignableFrom(entityClass))
  }

  def queryFrom[T](from: DateTime)(implicit t: ClassTag[T]): List[Event[_]] = {
    val entityClass = t.runtimeClass
    events.filter(event => event.date > from && event.entityVersion.getClass.isAssignableFrom(entityClass))
  }

  def queryAll[T](implicit t: ClassTag[T]): List[Event[_]] = {
    val entityClass = t.runtimeClass
    events.filter(_.entityVersion.getClass.isAssignableFrom(entityClass))
  }

  def size = events.size
}

protected case class EventQueue(filter: Event[_] => Boolean) {

  private var eventQueue: Queue[Event[_]] = Queue()

  def enqueue(queued: Event[_]) = {
    if (filter(queued)) {
      eventQueue = eventQueue.enqueue(queued)
    }
  }

  def dequeue[T]: Option[Event[T]] =
    if (eventQueue.isEmpty) None
    else {
      eventQueue.dequeue match {
        case (event, queuedEventQueue) => {
          eventQueue = queuedEventQueue
          Some(event.asInstanceOf[Event[T]])
        }
      }
    }

  def size = eventQueue.size
}

protected trait Publisher {

  var subscribers: Map[Any, EventQueue] = Map()

  def subscribeTo[T](subscriber: Any)(implicit t: ClassTag[T]) = {
    subscribers += (subscriber -> EventQueue(event => t.runtimeClass.isAssignableFrom(event.entityVersion.getClass)))
  }

  def enqueue(queued: Event[_]) = {
    for (eventQueue <- subscribers.values) eventQueue.enqueue(queued)
  }

  def dequeue[T](subscriber: Any): Option[Event[T]] = {
    subscribers.get(subscriber) match {
      case None => None
      case Some(eventQueue) => eventQueue.dequeue[T]
    }
  }

  def queueSize(subscriber: Any): Int = {
    subscribers.get(subscriber) match {
      case None => 0
      case Some(eventQueue) => eventQueue.size
    }
  }
}
