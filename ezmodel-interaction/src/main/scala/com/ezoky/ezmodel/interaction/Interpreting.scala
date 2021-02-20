package com.ezoky.ezmodel.interaction

import shapeless._

/**
  * @author gweinbach on 20/02/2021
  * @since 0.2.0
  */
/**
  * What the user says...
  */
private[interaction] trait Saying {

  case class Say[T](something: T)

  object Say {

    def apply[T1, T2](s1: T1, s2: T2): Say[T1 :: T2 :: HNil] =
      Say[T1 :: T2 :: HNil](s1 :: s2 :: HNil)

    def apply[T1, T2, T3](s1: T1, s2: T2, s3: T3): Say[T1 :: T2 :: T3 :: HNil] =
      Say[T1 :: T2 :: T3 :: HNil](s1 :: s2 :: s3 :: HNil)

    def apply[T1, T2, T3, T4](s1: T1, s2: T2, s3: T3, s4: T4): Say[T1 :: T2 :: T3 :: T4 :: HNil] =
      Say[T1 :: T2 :: T3 :: T4 :: HNil](s1 :: s2 :: s3 :: s4 :: HNil)
  }

}

/**
  * ...will be parsed into statements
  */
private[interaction] trait Parsing
  extends Saying {

  sealed trait Statement {
    type StatementType
    val stated: StatementType

    final def combine(other: Statement): Statement =
      other.stated match {
        case hlist: HList =>
          Statement(stated :: hlist)
        case s =>
          Statement(stated :: s :: HNil)
      }
  }

  object Statement {

    type Aux[S] = Statement { type StatementType = S }

    lazy val Empty: Statement.Aux[HNil] =
      Statement(HNil)

    def apply[S](statement: S): Statement.Aux[S] =
      new Statement {
        override type StatementType = S
        override val stated: S = statement
      }
  }


  implicit def parseHNil(hNil: HNil): Statement = Statement(HNil)

  implicit def parseHList[H, T <: HList](hlist: H :: T)
                                        (implicit
                                         parseH: H => Statement,
                                         parseT: T => Statement): Statement =
    parseH(hlist.head).combine(parseT(hlist.tail))

  case object Parser {
    def apply[T](said: Say[T])(implicit parse: T => Statement): Statement = parse(said.something)
  }

  def parse[T](said: Say[T])(implicit parse: T => Statement): Statement =
    Parser(said)

}

// And finally interpreted in terms of state change
private[interaction] trait Interpreting
  extends Parsing {

  trait Interpreter[S, T] {

    def interpret(state: S,
                  statement: T): S

    def apply(state: S,
              statement: Statement)
             (implicit
              eqv: statement.StatementType =:= T): S =
      interpret(state, statement.stated)
  }

  implicit def hNilInterpreter[S]: Interpreter[S, HNil] =
    Interpreter.noop

  implicit def programInterpreter[S, H, T <: HList](implicit
                                                    interpreterH: Interpreter[S, H],
                                                    interpreterT: Interpreter[S, T]): Interpreter[S, H :: T] =
    new Interpreter[S, H :: T] {
      override def interpret(state: S,
                             statement: H :: T): S =
        interpreterT.interpret(interpreterH.interpret(state, statement.head), statement.tail)
    }

  object Interpreter {

    def noop[S, T]: Interpreter[S, T] =
      new Interpreter[S, T] {
        override def interpret(state: S,
                               statement: T): S = state
      }

    def define[S, T](interpretation: S => T => S): Interpreter[S, T] =
      new Interpreter[S, T] {
        override def interpret(state: S,
                               statement: T): S =
          interpretation(state)(statement)
      }

    def apply[S, T](state: S,
                    statement: Statement)
                   (implicit
                    eqv: statement.StatementType =:= T,
                    interpreter: Interpreter[S, T]): S =
      interpreter(state, statement)
  }


  def interpret[S, T](state: S,
                      statement: Statement)
                     (implicit
                      eqv: statement.StatementType =:= T,
                      interpreter: Interpreter[S, T]): S =
    Interpreter(state, statement)

}
