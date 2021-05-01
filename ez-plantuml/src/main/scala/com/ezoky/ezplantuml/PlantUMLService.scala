package com.ezoky.ezplantuml

import cats.implicits._
import com.ezoky.architecture._
import com.ezoky.ezlogging.Safe
import com.ezoky.ezplantuml.PlantUMLRenderers._
import net.sourceforge.plantuml.{FileFormat, FileFormatOption, SourceStringReader}

import java.io.ByteArrayOutputStream
import java.nio.charset.Charset

/**
  * @author gweinbach on 06/04/2021
  * @since 0.2.0
  */
trait PlantUMLServiceAPI
  extends API {

  @Query
  def diagramSource(diagram: PlantUMLDiagram): QueryProducing[Option[String]]

  @Query
  def diagramSVG(diagram: PlantUMLDiagram): QueryProducing[Option[SVGString]]

}

case class SVGString(val svgString: String) extends AnyVal {

  def map(f: String => String): SVGString =
    SVGString(f(svgString))
}

trait PlantUMLService
  extends PlantUMLServiceAPI {

  override def diagramSource(diagram: PlantUMLDiagram): QueryProducing[Option[String]] =
    queryMonad.pure(Safe(diagram.render()))

  override def diagramSVG(diagram: PlantUMLDiagram): QueryProducing[Option[SVGString]] =
    for {
      source <- diagramSource(diagram)
      svg <- queryMonad.pure(sourceToSVG(source))
    } yield svg

  private def sourceToSVG(optDiagramSource: Option[String]): Option[SVGString] = {
    for {
      diagramSource <- optDiagramSource
      reader <- Safe(new SourceStringReader(diagramSource))
      outputStream <- Safe(new ByteArrayOutputStream)
      // Write the first image to "outputStream"
      desc <- Safe(reader.generateImage(outputStream, new FileFormatOption(FileFormat.SVG))).orElse {
        outputStream.close()
        None
      }
      // The XML is stored into svg
      svg <- Safe(new String(outputStream.toByteArray, Charset.forName("UTF-8"))).orElse {
        outputStream.close()
        None
      }
    } yield {
      outputStream.close()
      SVGString(svg)
    }
  }
}
