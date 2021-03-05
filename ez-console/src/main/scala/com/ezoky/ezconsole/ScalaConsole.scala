package com.ezoky.ezconsole

import scala.reflect.ClassTag
import scala.reflect.runtime.universe.TypeTag
import scala.tools.nsc.GenericRunnerSettings
import scala.tools.nsc.interpreter.Results
import scala.tools.nsc.interpreter.Results.{Error, Success}
import scala.tools.nsc.interpreter.shell.{ILoop, ShellConfig}
import scala.util.Properties.versionString

/**
  * @author gweinbach on 03/03/2021
  * @since 0.2.0
  */
class ScalaConsole[S: TypeTag : ClassTag](module: ConsoleModule[S]) {

  def start: Boolean = {

    val settings = new GenericRunnerSettings(Console.err.println) {
      usejavacp.value = true
      deprecation.value = true
      //      debug.value = true
    }
    settings.embeddedDefaults[ScalaConsole[S]]
    val config = ShellConfig(settings)

    val initialModuleState = module.moduleState
    val moduleInitializationCommands = module.startupScript.scriptsToRun
    val moduleBanner = module.banner
    val modulePrompt = module.prompt

    new ILoop(config) {

      override lazy val prompt: String = config.encolor(modulePrompt)

      override def welcome: String = moduleBanner

      private def initModuleState(): Results.Result = {
        // This creates a val with initial value
        intp.quietBind(intp.namedParam[S](ScalaConsole.InitialModuleStateVariableName, initialModuleState))
        // This creates and assigns a mutable var used to keep current module state
        intp.quietRun(s"var ${ScalaConsole.ModuleStateVariableName} = ${ScalaConsole.InitialModuleStateVariableName}")
      }

      private def initModule(): Unit = {
        if (!intp.reporter.hasErrors) {
          // `savingReplayStack` removes the commands from session history.
          savingReplayStack {
            moduleInitializationCommands.foreach(intp quietRun _)
            initModuleState()
          }
        }
        else {
          throw new RuntimeException(
            s"Scala $versionString interpreter encountered " +
            "errors during initialization"
          )
        }
      }

      override protected def internalReplAutorunCode(): Seq[String] =
        moduleInitializationCommands

      override def resetCommand(line: String): Unit = {
        super.resetCommand(line)
        initModule()
        echo(
          "Module State has been reset"
        )
      }

      override def replay(): Unit = {
        initModule()
        super.replay()
      }

      private def tryInterpret(code: String,
                               quietly: Boolean): Option[String] = {
        val trial: String => Results.Result =
          if (quietly) {
            code =>
              intp.reporter.suppressOutput(
                intp.quietRun(code)
              )
          }
          else {
            intp.interpret(_)
          }
        trial(code) match {
          case Error => None
          case Success => Some(code)
          case _ =>
            intp.reporter.printMessage("Should never happen since this is called only with a full line")
            None
        }
      }

      var stateIsInitialized: Boolean = false

      def initState(): Boolean = {
        if (!stateIsInitialized) {
          val result = initModuleState()
          stateIsInitialized = (result == Results.Success)
        }
        stateIsInitialized
      }

      override def command(line: String): Result = {
        if (!initState()) {
          intp.reporter.printMessage("[error] Failed to initialize state")
        }
        if (line startsWith ":") colonCommand(line)
        else if (!intp.initializeCompiler()) Result(keepRunning = false, None)
        else {
          val result = interpretStartingWith(line).fold[Option[String]](None) {
            fullCode =>
              for {
                _ <- tryInterpret(s"Parser(Say($fullCode))", quietly = true).orElse {
                  intp.reporter.trace("Not parsed")
                  None
                }
                _ <- tryInterpret(s"Interpreter(${ScalaConsole.ModuleStateVariableName}, Parser(Say($fullCode)))",
                  quietly = true).orElse {
                  intp.reporter.printMessage("[error] Command has been parsed but there is no available Interpreter")
                  None
                }
                processed <- tryInterpret(
                  s"""{
                     |  ${ScalaConsole.ModuleStateVariableName} = Processor(${ScalaConsole.ModuleStateVariableName}).process(Say($fullCode)).state
                     |  println()
                     |  println(${ScalaConsole.ModuleStateVariableName})
                     |}
                     |  """.stripMargin, quietly = false).orElse {
                  intp.reporter.printMessage("[error] Failed to process command")
                  None
                }
              } yield processed
          }
          Result(keepRunning = true, result)
        }
      }
    }.run(settings)
  }
}

object ScalaConsole {
  val InitialModuleStateVariableName: String = "initialModuleState"
  val ModuleStateVariableName: String = "moduleState"
}