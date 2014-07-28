package com.ezoky.ezmodel.core

import org.scalatest.FunSuite
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import com.ezoky.ezmodel.core.Atoms.Model
import com.ezoky.ezmodel.core.Structures._
import com.ezoky.ezmodel.core.EzModel._
import com.ezoky.ezmodel.storage.EventStore

@RunWith(classOf[JUnitRunner])
class EntityTest extends FunSuite {

  test("Entity creation") {
    EzModel // mandatory to init storage

    val t1 = Entity("test")
    assert(EventStore(Model).size === 1)
    entityRepository.populate
    assert(entityRepository.queryAllVersionsCount === 1)
    assert(entityRepository.queryEntitiesCount === 1)
    assert(entityRepository.queryEntities.head === t1)

    val t2 = Entity("test").attribute("att1", single, false)
    assert(EventStore(Model).size === 3)
    entityRepository.populate
    assert(entityRepository.queryAllVersionsCount === 3)
    assert(entityRepository.queryEntitiesCount === 1)
    assert(entityRepository.queryEntities.head === t1)

    val r = Entity("ref").attribute("att2", single, false)
    assert(EventStore(Model).size === 5)
    entityRepository.populate
    assert(entityRepository.queryAllVersionsCount === 5)
    assert(entityRepository.queryEntitiesCount === 2)
    assert(entityRepository.query("ref").head === r)

    t2.reference("referenced1", r, multiple, true)
    assert(EventStore(Model).size === 7)
    entityRepository.populate
    assert(entityRepository.queryAllVersionsCount === 7)
    assert(entityRepository.queryEntitiesCount === 2)
    assert(entityRepository.query("ref").head === r)
    assert(entityRepository.query("test").head.references.head.referenced === r)
  }
}