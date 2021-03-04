package com.ezoky.ezmodel.console

import com.ezoky.ezconsole.{ConsoleModule, EzokyAsciiBanner, ScalaScript}
import com.ezoky.ezmodel.interaction.ModellingState

/**
  * @author gweinbach on 01/03/2021
  * @since 0.2.0
  */
object EzModeller {

  private val EzModellerBanner =
    new EzokyAsciiBanner {
      override val prompt: String = "ez> "
      override val applicationName: String = "EZModeller"
    }

  private val EzModelStartup: ScalaScript =
    """
      |import com.ezoky.ezmodel.core.Models._
      |import com.ezoky.ezmodel.interaction.dsl.DSL._
      |import com.ezoky.ezmodel.interaction.Modelling._
      |import com.ezoky.ezmodel.interaction._
      |""".stripMargin


  val defaultConsoleModule: ConsoleModule[ModellingState] =
    ConsoleModule(
      EzModellerBanner,
      EzModelStartup,
      ModellingState.Empty
    )
}
