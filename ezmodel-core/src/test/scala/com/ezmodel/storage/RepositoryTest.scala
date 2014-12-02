package com.ezmodel.storage

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class RepositorySuite extends FunSuite {

  case class ImmutableEntity(i: Int)

  case class MutableEntity(i: Int) {
    var j: Int = 0
  }

  test("entity creation and direct repository storage") {

    val r = Repository[MutableEntity]("Entity")
    val entityId = 10

    val e1 = MutableEntity(entityId)
    assert(r.queryEntitiesCount === 0)
    assert(r.queryVersionCount(e1) === 0)

    r.store(e1)
    assert(r.queryEntitiesCount === 1)
    assert(r.queryVersionCount(e1) === 1)

    val qe1_0 = r.query(e1)
    assert(qe1_0 === Some(e1))

    e1.j = 1
    r.store(e1)
    assert(r.queryEntitiesCount === 1)
    assert(r.queryVersionCount(e1) === 2)
    assert(r.query(e1).get.j === 1)

    val e2 = MutableEntity(entityId)
    e2.j = 2
    r.store(e2)
    assert(r.queryEntitiesCount === 1)
    assert(r.queryVersionCount(e1) === 3)
    assert(r.query(e1).get.j === 2)

    val e3 = MutableEntity(entityId + 1)
    e3.j = 3
    r.store(e3)
    assert(r.queryEntitiesCount === 2)
    assert(r.queryVersionCount(e3) === 1)
    assert(r.query(e3).get.j === 3)
  }

  test("CQRS: event storage then repository querying") {

    val EntityEventStore = EventStore("Entity")

    EntityEventStore.reset()
    val r = Repository[MutableEntity]("Entity")
    val entityId = 10

    val e1 = MutableEntity(entityId)
    assert(r.queryEntitiesCount === 0)
    assert(r.queryVersionCount(e1) === 0)

    EntityEventStore.store(e1)
    assert(EntityEventStore.size === 1)
    r.populate
    assert(r.queryEntitiesCount === 1)
    assert(r.queryVersionCount(e1) === 1)

    val qe1_0 = r.query(e1)
    assert(qe1_0 === Some(e1))

    e1.j = 1
    EntityEventStore.store(e1)
    assert(EntityEventStore.size === 2)
    r.populate
    assert(r.queryEntitiesCount === 1)
    assert(r.queryVersionCount(e1) === 2)
    assert(r.query(e1).get.j === 1)

    val e2 = MutableEntity(entityId)
    e2.j = 2
    EntityEventStore.store(e2)
    assert(EntityEventStore.size === 3)
    r.populate
    assert(r.queryEntitiesCount === 1)
    assert(r.queryVersionCount(e1) === 3)
    assert(r.query(e1).get.j === 2)

    val e3 = MutableEntity(entityId + 1)
    e3.j = 3
    EntityEventStore.store(e3)
    assert(EntityEventStore.size === 4)
    r.populate
    assert(r.queryEntitiesCount === 2)
    assert(r.queryVersionCount(e3) === 1)
    assert(r.query(e3).get.j === 3)

    val e4 = MutableEntity(entityId + 2)
    EntityEventStore.store(e4)
    assert(EntityEventStore.size === 5)
    val e5 = MutableEntity(entityId + 2)
    EntityEventStore.store(e5)
    assert(EntityEventStore.size === 6)
    r.populate
    assert(r.queryEntitiesCount === 3)
    assert(r.queryVersionCount(e4) === 2)
  }

  test("CQRS: multiple types = multiple repositories") {

    val EntityEventStore = EventStore("Entity")

    EntityEventStore.reset()
    assert(EntityEventStore.size === 0)

    val rm = Repository[MutableEntity]("Entity")
    val ri = Repository[ImmutableEntity]("Entity")

    val entityId = 10

    val m1 = MutableEntity(entityId)
    val i1 = ImmutableEntity(entityId)

    assert(rm.queryEntitiesCount === 0)
    assert(rm.queryVersionCount(m1) === 0)
    assert(ri.queryEntitiesCount === 0)
    assert(ri.queryVersionCount(i1) === 0)

    EntityEventStore.store(m1)
    assert(EntityEventStore.size === 1)
    ri.populate
    rm.populate
    assert(ri.queryEntitiesCount === 0)
    assert(ri.queryVersionCount(i1) === 0)
    assert(rm.queryEntitiesCount === 1)
    assert(rm.queryVersionCount(m1) === 1)

    EntityEventStore.store(i1)
    assert(EntityEventStore.size === 2)
    ri.populate
    rm.populate
    assert(ri.queryEntitiesCount === 1)
    assert(ri.queryVersionCount(i1) === 1)
    assert(rm.queryEntitiesCount === 1)
    assert(rm.queryVersionCount(m1) === 1)
  }
}