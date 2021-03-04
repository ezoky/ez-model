package com.ezoky.ezmodel.console

import com.ezoky.ezconsole.ScalaConsole

/**
  * @author gweinbach on 27/02/2021
  * @since 0.2.0
  */
object EzModellerConsole
  extends ScalaConsole(EzModeller.defaultConsoleModule)
    with App {

  try {
    start
  }
  catch {
    case e: Throwable =>
      Console.err.println(s"Failed to start console: ${e}")
      e.printStackTrace(Console.err)
      sys.exit(1)
  }
  sys.exit(0)
}