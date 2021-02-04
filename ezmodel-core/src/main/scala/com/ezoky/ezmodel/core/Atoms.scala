package com.ezoky.ezmodel.core

object Atoms {

  object Model

  case class Name(name: String)


  object DefaultName extends Name("<default>")

  implicit def stringName(name: String): Name = Name(name)


  case class Qualifier(qualifier: String)

  implicit def stringQualifier(qualifier: String): Qualifier = Qualifier(qualifier)


  case class Verb(verb: String)

  implicit def stringVerb(verb: String): Verb = Verb(verb)


  abstract case class Determinant(determinant: String)

  object a extends Determinant("a")

  object an extends Determinant("an")

  object the extends Determinant("the")

  object some extends Determinant("some")

  object any extends Determinant("any")

  object every extends Determinant("every")

  object all extends Determinant("all")

  object few extends Determinant("few")


  case class NameGroup(determinant: Determinant, name: Name)

  implicit def implicitNameGroup1(nameGroup: (Determinant, Name)): NameGroup =
    NameGroup(nameGroup._1, nameGroup._2)

  implicit def implicitNameGroup2(nameGroup: (Determinant, String)): NameGroup =
    NameGroup(nameGroup._1, Name(nameGroup._2))

  def a(name: Name): NameGroup = NameGroup(a, name)

  def an(name: Name): NameGroup = NameGroup(an, name)

  def the(name: Name): NameGroup = NameGroup(the, name)

  def some(name: Name): NameGroup = NameGroup(some, name)

  def any(name: Name): NameGroup = NameGroup(any, name)

  def every(name: Name): NameGroup = NameGroup(every, name)

  def all(name: Name): NameGroup = NameGroup(all, name)

  def few(name: Name): NameGroup = NameGroup(few, name)
}