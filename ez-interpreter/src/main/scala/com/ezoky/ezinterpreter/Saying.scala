package com.ezoky.ezinterpreter

import shapeless.{::, HNil}

/**
  * What the user says...
  */
private[ezinterpreter] trait Saying {

  case class Say[+T](something: T)

  object Say {

    def apply[T1, T2](s1: T1, s2: T2): Say[T1 :: T2 :: HNil] =
      Say(s1 :: s2 :: HNil)

    def apply[T1, T2, T3](s1: T1, s2: T2, s3: T3): Say[T1 :: T2 :: T3 :: HNil] =
      Say(s1 :: s2 :: s3 :: HNil)

    def apply[T1, T2, T3, T4](s1: T1, s2: T2, s3: T3, s4: T4): Say[T1 :: T2 :: T3 :: T4 :: HNil] =
      Say(s1 :: s2 :: s3 :: s4 :: HNil)

    def apply[T1, T2, T3, T4, T5](s1: T1, s2: T2, s3: T3, s4: T4, s5: T5): Say[T1 :: T2 :: T3 :: T4 :: T5 :: HNil] =
      Say(s1 :: s2 :: s3 :: s4 :: s5 :: HNil)

    def apply[T1, T2, T3, T4, T5, T6](s1: T1, s2: T2, s3: T3, s4: T4, s5: T5, s6: T6): Say[T1 :: T2 :: T3 :: T4 :: T5 :: T6 :: HNil] =
      Say(s1 :: s2 :: s3 :: s4 :: s5 :: s6 :: HNil)

    def apply[T1, T2, T3, T4, T5, T6, T7](s1: T1, s2: T2, s3: T3, s4: T4, s5: T5, s6: T6, s7: T7): Say[T1 :: T2 :: T3 :: T4 :: T5 :: T6 :: T7 :: HNil] =
      Say(s1 :: s2 :: s3 :: s4 :: s5 :: s6 :: s7 :: HNil)
  }

}
