package com.ezoky.ezmodel.plantumlview

import cats.Monad
import cats.implicits._
import com.ezoky.architecture.Command
import com.ezoky.ezmodel.core.Models
import com.ezoky.ezmodel.core.Models.Model
import com.ezoky.ezmodel.plantuml.{ModellingDiagram, RenderModelInPlantUML}
import sttp.client3._
import sttp.model.Uri

/**
  * @author gweinbach on 21/04/2021
  * @since 0.2.0
  */
trait ViewModelInPlantUMLAPI[F[_]] {

  type CommandConsumingNothing = () => F[Unit]

  @Command
  def viewDiagramInPlantUML(diagram: ModellingDiagram): CommandConsumingNothing

  @Command
  def viewModelInPlantUML(model: Model): CommandConsumingNothing

}

trait ViewModelInPlantUMLConfig {
  val sendSVGEndPoint: Uri
}

case class ViewModelInPlantUMLServerConfig(serverHost: String)
  extends ViewModelInPlantUMLConfig {
  lazy val sendSVGEndPoint = uri"${serverHost}/api/sendSVG"
}

abstract class ViewModelInPlantUML[F[_] : Monad](renderModelInPlantUML: RenderModelInPlantUML[F])
  extends ViewModelInPlantUMLAPI[F] {

  def configReader[A](configure: ViewModelInPlantUMLConfig => A): F[A]

  def useBackend(action: SttpBackend[F, Any] => F[Response[Either[String, String]]]): F[Unit]

  def sendRequest(request: Request[Either[String, String], Any]): F[Unit] =
    useBackend(backend => request.send(backend))

  override def viewDiagramInPlantUML(diagram: ModellingDiagram): CommandConsumingNothing = { () =>
    diagram.svg.fold(Monad[F].unit) {
      svgContent =>
        configReader {
          config =>
            basicRequest.body(svgContent.svgString).post(config.sendSVGEndPoint)
        }.flatMap(sendRequest)
    }
  }

  override def viewModelInPlantUML(model: Models.Model): CommandConsumingNothing = { () =>
    for {
      diagramSet <- renderModelInPlantUML.generateSVG(model)
      _ <- diagramSet.map(viewDiagramInPlantUML(_)()).toList.traverse(identity)
    } yield ()
  }
}
