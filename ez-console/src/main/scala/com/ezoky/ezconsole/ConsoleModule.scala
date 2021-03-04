package com.ezoky.ezconsole

/**
  * @author gweinbach on 26/02/2021
  * @since 0.2.0
  */
case class ConsoleModule[S](prompt: String,
                            banner: String,
                            startupScript: ScalaScript,
                            moduleState: S)

object ConsoleModule {

  def apply[S](banner: ConsoleBanner,
               startupScript: ScalaScript,
               moduleState: S): ConsoleModule[S] =
    new ConsoleModule(
      prompt = banner.prompt,
      banner = banner.welcome,
      startupScript,
      moduleState
    )
}