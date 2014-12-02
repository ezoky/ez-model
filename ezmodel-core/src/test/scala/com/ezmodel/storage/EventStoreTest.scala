package com.ezmodel.storage

import org.junit.runner.RunWith
import org.scalatest._
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class EventStoreModelSuite extends FunSuite {

  case class IntEntity(i: Int)
  case class StringEntity(s: String)

  test("Event Queue queues and dequeues events") {

    val eventQueue = EventQueue(e => e.entityVersion.isInstanceOf[IntEntity])
    assert(eventQueue.size === 0)

    eventQueue.enqueue(Event(IntEntity(1)))
    assert(eventQueue.size === 1)

    eventQueue.enqueue(Event(StringEntity("a")))
    assert(eventQueue.size === 1)

    eventQueue.enqueue(Event(IntEntity(2)))
    assert(eventQueue.size === 2)

    assert(eventQueue.dequeue[IntEntity].get.entityVersion.i === 1)
    assert(eventQueue.dequeue[IntEntity].get.entityVersion.i === 2)
    assert(eventQueue.dequeue[Any] === None)
  }

  test("Publisher") {
    
    val listened = new Publisher {}
    
    listened.subscribeTo[IntEntity](this)
    assert(listened.queueSize(this) === 0)
    assert(listened.queueSize(new Object) === 0)
    
    listened.enqueue(Event(IntEntity(1)))
    assert(listened.queueSize(this) === 1)
    listened.enqueue(Event(StringEntity("a")))
    assert(listened.queueSize(this) === 1)
    listened.enqueue(Event(IntEntity(2)))
    assert(listened.queueSize(this) === 2)
    assert(listened.queueSize(new Object) === 0)
    
    assert(listened.dequeue[IntEntity](this).get.entityVersion.i === 1)
    assert(listened.dequeue[IntEntity](new Object) === None)
    listened.enqueue(Event(IntEntity(3)))
    assert(listened.dequeue[IntEntity](this).get.entityVersion.asInstanceOf[IntEntity].i === 2)
    assert(listened.dequeue[IntEntity](this).get.entityVersion.asInstanceOf[IntEntity].i === 3)
    assert(listened.dequeue[IntEntity](this) === None)
  }
  
  test("Entity store") {

    val EventStoreModel = EventStore("entities")
    EventStoreModel.reset()
    
    val events0 = EventStoreModel.queryAll[IntEntity]
    assert(events0.size === 0)

    val i1 = IntEntity(1)
    EventStoreModel.store(i1)
    val events1 = EventStoreModel.queryAll[IntEntity]
    assert(events1.size === 1)

    val s1 = StringEntity("hello")
    val s2 = StringEntity("world")
    EventStoreModel.store(s1)
    EventStoreModel.store(s2)
    val events2 = EventStoreModel.queryAll[IntEntity]
    assert(events2.size === 1)

    val events3 = EventStoreModel.queryAll[StringEntity]
    assert(events3.size === 2)
    
    val s3 = StringEntity("hello")
    EventStoreModel.store(s3)
    val events4 = EventStoreModel.queryAll[StringEntity]
    assert(events4.size === 3)
  }
}