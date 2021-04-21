package com.ezoky.ezmodel.plantumlview

import com.ezoky.ezlogging.EzLoggableClass
import com.ezoky.ezmodel.core.Models.Model
import com.ezoky.ezmodel.plantuml.{PlantUMLRenderingService, SimplePlantUMLRenderingService}
import sttp.client3._

/**
  * @author gweinbach on 21/04/2021
  * @since 0.2.0
  */
trait PlantUmlViewService {

  def viewPlantUmlModel(model: Model): Unit

}


trait SimplePlantUmlViewService
  extends PlantUmlViewService
    with EzLoggableClass {

  val plantUMLRenderingService: PlantUMLRenderingService

  override def viewPlantUmlModel(model: Model): Unit = {
    val backend = HttpURLConnectionBackend()
    val result = for {
      diagram <- plantUMLRenderingService.generateSVG(model)
      svgContent <- diagram.svg
      response = {
        warn(svgContent.svgString)
        basicRequest
          .body(svgContent.svgString)
          .post(uri"http://localhost:7071/api/sendSVG")
          .send(backend)
      }
    } yield response
    warn(result.toString())
  }

}

object SimplePlantUmlViewService extends SimplePlantUmlViewService {
  override val plantUMLRenderingService: PlantUMLRenderingService = SimplePlantUMLRenderingService
}