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
trait ViewModelInPlantUMLAPI[Effect[_]] {

  @Command
  def viewDiagramInPlantUML(diagram: ModellingDiagram): Effect[Unit]

  @Command
  def viewModelInPlantUML(model: Model): Effect[Unit]

}

trait ViewModelInPlantUMLConfig {
  val sendSVGEndPoint: Uri
}

case class ViewModelInPlantUMLServerConfig(serverHost: String)
  extends ViewModelInPlantUMLConfig {
  lazy val sendSVGEndPoint = uri"${serverHost}/api/sendSVG"
}

abstract class ViewModelInPlantUML[Effect[_] : Monad](renderModelInPlantUML: RenderModelInPlantUML[Effect])
  extends ViewModelInPlantUMLAPI[Effect] {

  def configReader[A](configure: ViewModelInPlantUMLConfig => A): Effect[A]

  def useBackend(action: SttpBackend[Effect, Any] => Effect[Unit]): Effect[Unit]

  def postRequest(postRequest: Request[Either[String, String], Any]): Effect[Unit] =
    useBackend(backend => postRequest.send(backend).map(_ => ()))

  override def viewDiagramInPlantUML(diagram: ModellingDiagram): Effect[Unit] =
    diagram.svg.fold(Monad[Effect].unit) {
      svgContent =>
        configReader {
          config =>
            basicRequest.body(svgContent.svgString).post(config.sendSVGEndPoint)
        }.flatMap(postRequest)
    }


  override def viewModelInPlantUML(model: Models.Model): Effect[Unit] =
    for {
      diagramSet <- renderModelInPlantUML.generateSVG(model)
      _ <- diagramSet.map(viewDiagramInPlantUML(_)).toList.traverse(identity)
    } yield ()
}


//class ZIOViewModelInPlantUML(override val renderModelInPlantUML: RenderModelInPlantUML[Task])
//  extends ViewModelInPlantUML[Task](renderModelInPlantUML)
//    with EzLoggableClass {


//  override def viewDiagramInPlantUML(model: Model): Unit =
//    zioViewPlantUmlModel(model)
//
//  def zioViewPlantUmlModel(model: Model) = {
//     backend =>
//      for {
//        diagrams <- renderModelInPlantUML.generateSVG(model)
//        responses <- diagrams.toList.map(buildSVGDiagramRequest).traverse(identity)
//      } yield responses
//    }
//
//
//  private def buildSVGDiagramRequest(diagram: ModellingDiagram) =
//    for {
//      svgContent <- diagram.svg
//    } yield {
//      trace(s"SVG content = ${svgContent.svgString}")
//      basicRequest.body(svgContent.svgString).post(uri"http://localhost:7071/api/sendSVG")
//    }
//
//  {
//    val backend = HttpURLConnectionBackend()
//    val result = for {
//      diagram <- plantUMLRenderingService.generateSVG(model)
//      svgContent <- diagram.svg
//      response = {
//        warn(svgContent.svgString)
//        basicRequest
//          .body(svgContent.svgString)
//          .post(uri"http://localhost:7071/api/sendSVG")
//          .send(backend)
//      }
//    } yield response
//    warn(result.toString())
//  }

//}

//object SimpleViewModelInPlantUML extends SimpleViewModelInPlantUML$ {
//  override val plantUMLRenderingService: PlantUMLRenderingService = SimplePlantUMLRenderingService
//}