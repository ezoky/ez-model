package com.ezoky.ezmodel.interaction.interpreter

import shapeless.{::, HList, HNil}

/**
  * @author gweinbach on 21/02/2021
  * @since 0.2.0
  */
/**
  * ...will be parsed into statements...
  */
private[interaction] trait Parsing
  extends Saying {

  case class Statement[+S](stated: S) {

    def combine[H <: HList](other: Statement[H]): Statement[S :: H] =
      Statement(stated :: other.stated)
  }

  object Statement {

    lazy val Empty: Statement[HNil] =
      Statement(HNil)
  }


  trait Parser[-T, +S] {

    def parse(something: T): Statement[S]

    final def apply(said: Say[T]): Statement[S] =
      parse(said.something)
  }

  case object Parser {

    def define[T, S](parsing: T => Statement[S]): Parser[T, S] =
      (something: T) => parsing(something)

    def apply[T, S](said: Say[T])(implicit parser: Parser[T, S]): Statement[S] =
      parser(said)
  }

  def parse[T, S](said: Say[T])(implicit parser: Parser[T, S]): Statement[S] =
    parser(said)


  implicit val hNilParser: Parser[HNil, HNil] =
    Parser.define(_ => Statement(HNil))

  implicit def hListParser[H, T <: HList, SH, ST <: HList](implicit
                                                           parserH: Parser[H, SH],
                                                           parserT: Parser[T, ST]): Parser[H :: T, SH :: ST] =
    Parser.define(hList =>  parserH.parse(hList.head).combine(parserT.parse(hList.tail)))
}
