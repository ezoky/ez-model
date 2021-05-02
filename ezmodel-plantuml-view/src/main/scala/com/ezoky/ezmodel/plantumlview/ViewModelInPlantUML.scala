package com.ezoky.ezmodel.plantumlview

import com.ezoky.architecture.zioapi.ZIOAPI
import com.ezoky.ezlogging.EzLoggableClass
import com.ezoky.ezmodel.core.Models.Model
import com.ezoky.ezmodel.plantuml.{ModellingDiagram, RenderModelInPlantUML}
import com.ezoky.ezplantuml.PlantUMLService
import zio.{Task, ZIO}

/**
  * @author gweinbach on 21/04/2021
  * @since 0.2.0
  */
trait ViewModelInPlantUML {

  def viewPlantUmlModel(model: Model): Unit

}


import sttp.client3._
import sttp.client3.asynchttpclient.zio._
import com.ezoky.architecture.zioapi.ZIOAPI

import ZIOAPI._

object ZIOPlantUMLService
  extends PlantUMLService[ZIOAPI.QueryProducing]

object ZIORenderModelInPlantUML
  extends RenderModelInPlantUML[ZIOAPI.QueryProducing](ZIOPlantUMLService)

object ZIOViewModelInPlantUML
  extends ViewModelInPlantUML
    with EzLoggableClass {

  val renderModelInPlantUML: RenderModelInPlantUML[ZIOAPI.QueryProducing] = ZIORenderModelInPlantUML

  override def viewPlantUmlModel(model: Model): Unit =
    zioViewPlantUmlModel(model)

  def zioViewPlantUmlModel(model: Model) = {
    AsyncHttpClientZioBackend.managed().use { backend =>
      for {
        diagrams <- renderModelInPlantUML.generateSVG(model)
        responses <-
          ZIO.validate(
            diagrams.toList
            .map(buildSVGDiagramRequest)
            .map(_.fold[ZIO[Any, Throwable, Response[Either[String, String]]]](Task.fail(new RuntimeException("no SVG")))(_.send(backend)))
          )(identity)
      } yield responses
    }
  }


  private def buildSVGDiagramRequest(diagram: ModellingDiagram) =
    for {
      svgContent <- diagram.svg
    } yield {
      trace(s"SVG content = ${svgContent.svgString}")
      basicRequest.body(svgContent.svgString).post(uri"http://localhost:7071/api/sendSVG")
    }
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

}

//object SimpleViewModelInPlantUML extends SimpleViewModelInPlantUML$ {
//  override val plantUMLRenderingService: PlantUMLRenderingService = SimplePlantUMLRenderingService
//}