package com.ezoky.ezmodel.core.requirements

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


  case class Determinant(determinant: String)

  object Determinant {

    val a = Determinant("a")

    val an = Determinant("an")

    val the = Determinant("the")

    val some = Determinant("some")

    val any = Determinant("any")

    val every = Determinant("every")

    val all = Determinant("all")

    val few = Determinant("few")

  }

  case class NameGroup(determinant: Determinant,
                       name: Name)

}