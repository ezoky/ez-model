package com.ezoky.ezmodel.storage

import scala.reflect.ClassTag
import com.ezoky.ezmodel.storage.EventStore._

object Repository {
  def apply[T](eventStoreKey:Any)(implicit t: ClassTag[T]):Repository[T] = {
    new Repository[T](eventStoreKey)(t)
  }
}
class Repository[T](eventStoreKey:Any)(implicit t: ClassTag[T]) {

  EventStore(eventStoreKey).subscribeTo[T](this)

  def populate():Unit = {
	EventStore(eventStoreKey).dequeue[T](this) match {
	  case None => {}
      case Some(Event(entityVersion, _)) => {
        store(entityVersion)
        populate()
      }
    }
  }

  private def resetStorage() = {
    storage = Map[T, List[T]]()
  }

  def store(entityVersion: T): Unit = {
    assume(entityVersion != null)
    storage.get(entityVersion) match {
      case None => storage += (entityVersion -> List(entityVersion))
      case Some(entitiesList) => storage += (entityVersion -> (entityVersion :: entitiesList))
    }
  }

  def query(entity: T): Option[T] = {
    assume(entity != null)
    storage.get(entity) match {
      case None => None
      case Some(Nil) => throw new RuntimeException(s"Invalid Storage with empty event list for value $entity")
      case Some(current :: _) => Some(current)
    }
  }

  def queryVersionCount(entity: T): Int = {
    assume(entity != null)
    storage.get(entity) match {
      case None => 0
      case Some(Nil) => throw new RuntimeException(s"Invalid Storage with empty event list for value $entity")
      case Some(list) => list.size
    }
  }
  
  def queryAllVersionsCount: Int = {
    storage.values.foldLeft(0)((i,l)=>i + l.size)
  }

  def queryEntitiesCount: Int = {
    storage.keys.size
  }

  def queryEntities: Iterable[T] = {
    storage.keys
  }

  private var storage = Map[T, List[T]]()
}