package com.ezoky.ezmodel.core

import com.ezoky.ezmodel.core.Atoms.{Model, Name}
import com.ezoky.ezmodel.core.Domains.Domain
import com.ezoky.ezmodel.core.EzModel._
import com.ezoky.ezmodel.core.Structures._
import com.ezoky.ezmodel.storage.EventStore
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class EzModelTest extends FunSuite {

  test("Entity Repository") {
    EzModel.reset() // mandatory to init storage
    val initialEventStoreSize = EventStore(Model).size

    val t1 = Entity("test")
    assert(EventStore(Model).size === initialEventStoreSize + 1)
    entityRepository.populate
    assert(entityRepository.queryAllVersionsCount === 1)
    assert(entityRepository.queryEntitiesCount === 1)
    assert(entityRepository.queryEntities.head === t1)

    val t2 = Entity("test").attribute("att1", single, false)
    assert(EventStore(Model).size === initialEventStoreSize + 3)
    entityRepository.populate
    assert(entityRepository.queryAllVersionsCount === 3)
    assert(entityRepository.queryEntitiesCount === 1)
    assert(entityRepository.queryEntities.head === t1)

    val r = Entity("ref").attribute("att2", single, false)
    assert(EventStore(Model).size === initialEventStoreSize + 5)
    entityRepository.populate
    assert(entityRepository.queryAllVersionsCount === 5)
    assert(entityRepository.queryEntitiesCount === 2)
    assert(entityRepository.query("ref").head === r)

    t2.reference("referenced1", r, multiple, true)
    assert(EventStore(Model).size === initialEventStoreSize + 7)
    entityRepository.populate
    assert(entityRepository.queryAllVersionsCount === 7)
    assert(entityRepository.queryEntitiesCount === 2)
    assert(entityRepository.query("ref").head === r)
    assert(entityRepository.query("test").head.references.head.referenced === r)
  }

  test("Domain Repository") {

    EzModel.reset() // mandatory to init storage
    val initialEventStoreSize = EventStore(Model).size

    val d1 = Domain("test")
    assert(EventStore(Model).size === initialEventStoreSize + 1)
    domainRepository.populate
    assert(domainRepository.queryAllVersionsCount === 1)
    assert(domainRepository.queryEntitiesCount === 1)
    assert(domainRepository.queryEntities.head === d1)
  }
}