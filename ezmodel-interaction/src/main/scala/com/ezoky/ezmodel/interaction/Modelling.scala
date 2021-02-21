package com.ezoky.ezmodel.interaction

import com.ezoky.ezmodel.interaction.interpreter.{Interpreting, Parsing, Saying}

/**
  * @author gweinbach on 20/02/2021
  * @since 0.2.0
  */
object Modelling
  extends Interpreting
  with Parsing
  with Saying
