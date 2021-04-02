package com.ezoky.ezmodel.control

import shapeless.{::, HNil}

/**
  * @author gweinbach on 01/04/2021
  * @since 0.2.0
  */
case class Join[I1, InputType1 >: ValueHolder[I1], O1, OutputType1 <: ValueHolder[O1], I2, InputType2 >: ValueHolder[I2], O2, OutputType2 <: ValueHolder[O2]](action1: ActionnableController[InputType1, OutputType1],
                                                                                                                                                              action2: ActionnableController[InputType2, OutputType2])
  extends ActionnableController[MultipleValueHolder[I1 :: I2 :: HNil], MultipleValueHolder[O1 :: O2 :: HNil]] {

  override def action(input: MultipleValueHolder[I1 :: I2 :: HNil]): MultipleValueHolder[O1 :: O2 :: HNil] =
    action1(input.head).zip(action2(input.tail.head))
}
