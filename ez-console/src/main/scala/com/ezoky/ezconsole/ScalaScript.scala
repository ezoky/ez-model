package com.ezoky.ezconsole

/**
  * @author gweinbach on 26/02/2021
  * @since 0.2.0
  */
case class ScalaScript(scriptsToRun: Seq[String]) {

  def +(that: ScalaScript): ScalaScript =
    ScalaScript(this.scriptsToRun ++ that.scriptsToRun)

  def block: String =
    scriptsToRun.mkString("\n")
}

object ScalaScript {

  val NothingToRun = ScalaScript(Seq.empty)

  implicit def stringToScalaScript(scriptToRun: String): ScalaScript =
    ScalaScript(Seq(scriptToRun))

}