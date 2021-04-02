package com.ezoky.ezmodel.control

/**
  * @author gweinbach on 31/03/2021
  * @since 0.2.0
  */
trait ActionnableController[-InputType, +OutputType] {

  def action(input: InputType): OutputType

  def apply(input: InputType): OutputType =
    action(input)

}


object ActionnableController {

  def noAction[T]: ActionnableController[T, T] =
    input => input

  def apply[InputType, OutputType](f: InputType => OutputType): ActionnableController[InputType, OutputType] =
    (input: InputType) => f(input)
  
  def map[InputType, OutputType, OutputType2](controller: ActionnableController[InputType, OutputType],
                                              f: OutputType => OutputType2): ActionnableController[InputType, OutputType2] =
    controller | ActionnableController(f)

  def combine[InputType1, OutputType1, InputType2, OutputType2](action1: ActionnableController[InputType1, OutputType1],
                                                                action2: ActionnableController[InputType2, OutputType2])
                                                               (adapter: OutputType1 => InputType2): ActionnableController[InputType1, OutputType2] =
    input => action2.action(adapter(action1.action(input)))

  implicit class ActionnableControllerHelper[InputType, OutputType](action: ActionnableController[InputType, OutputType]) {

    def andThen[InputType2, OutputType2](action2: ActionnableController[InputType2, OutputType2])
                                        (adapter: OutputType => InputType2): ActionnableController[InputType, OutputType2] =
      ActionnableController.combine(action, action2)(adapter)

    def |[OutputType2](action2: ActionnableController[OutputType, OutputType2]): ActionnableController[InputType, OutputType2] =
      action.andThen(action2)(identity)

    def map[OutputType2](f: OutputType => OutputType2): ActionnableController[InputType, OutputType2] =
      ActionnableController.map(action, f)
  }

}
