package com.ezoky.ezmodel.core


private[core] trait Atoms {

  /**
    * Examples:
    * {{{
    * Name("Joe")
    * Name("Invoice")`
    * }}}
    */
  case class Name(name: String)

  object DefaultName extends Name("<default>")


  /**
    * Examples:
    * {{{
    * Qualifier("Done")
    * Qualifier("Invoiced")`
    * }}}
    */
  case class Qualifier(qualifier: String)

  /**
    * Examples:
    * {{{
    * Verb("end") // intransitive verb
    * Verb("invoice")` // transitive verb expecting an object
    * }}}
    */
  case class Verb(verb: String)


  abstract class Determinant(determinant: String)

  object Determinant {

    case object a extends Determinant("a")

    case object an extends Determinant("an")

    case object the extends Determinant("the")

    case object some extends Determinant("some")

    case object any extends Determinant("any")

    case object every extends Determinant("every")

    case object all extends Determinant("all")

    case object few extends Determinant("few")

  }
  case class NameGroup(determinant: Determinant,
                       name: Name)

}