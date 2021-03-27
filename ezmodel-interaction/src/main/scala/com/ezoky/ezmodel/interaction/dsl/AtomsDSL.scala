package com.ezoky.ezmodel.interaction.dsl

import com.ezoky.ezmodel.core.Models._

/**
  * @author gweinbach on 04/02/2021
  * @since 0.2.0
  */
trait AtomsDSL {

  implicit def convertStringToName(name: String): Name =
    Name(name)

  implicit def convertStringToVerb(verb: String): Verb =
    Verb(verb)

  implicit def convertStringToDeterminant(determinant: String): Determinant =
    Determinant(determinant)

  implicit def convertStringToQualifier(qualifier: String): Qualifier =
    Qualifier(qualifier)


  def a(name: Name): NameGroup = NameGroup(Determinant.a, name)

  def an(name: Name): NameGroup = NameGroup(Determinant.an, name)

  def the(name: Name): NameGroup = NameGroup(Determinant.the, name)

  def some(name: Name): NameGroup = NameGroup(Determinant.some, name)

  def any(name: Name): NameGroup = NameGroup(Determinant.any, name)

  def every(name: Name): NameGroup = NameGroup(Determinant.every, name)

  def all(name: Name): NameGroup = NameGroup(Determinant.all, name)

  def few(name: Name): NameGroup = NameGroup(Determinant.few, name)
}
