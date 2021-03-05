package com.ezoky.ezmodel.interaction

import com.ezoky.ezmodel.interaction.interpreter.Processing
import com.ezoky.ezmodel.interaction.processing.{ModellingSyntax, simple}

/**
  * @author gweinbach on 20/02/2021
  * @since 0.2.0
  */
object Modelling
  extends Processing
    with ModellingSyntax
    with simple.ModellingInterpreter