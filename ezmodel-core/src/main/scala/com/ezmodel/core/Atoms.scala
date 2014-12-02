package com.ezmodel.core

object Atoms {

  import scala.language.implicitConversions
  
  object Model
  
  case class Name(name: String)
  object DefaultName extends Name("<default>")
  implicit def stringName(name: String) = Name(name)

  case class Qualifier(qualifier: String)
  implicit def stringQualifier(qualifier: String) = Qualifier(qualifier)

  case class Verb(verb: String)
  implicit def stringVerb(verb: String) = Verb(verb)

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
  implicit def implicitNameGroup1(nameGroup: (Determinant, Name)) = NameGroup(nameGroup._1, nameGroup._2)
  implicit def implicitNameGroup2(nameGroup: (Determinant, String)) = NameGroup(nameGroup._1, Name(nameGroup._2))
}