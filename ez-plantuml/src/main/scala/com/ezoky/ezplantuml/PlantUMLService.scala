package com.ezoky.ezplantuml

import com.ezoky.ezlogging.Safe
import com.ezoky.ezplantuml.PlantUMLService.SVGString
import net.sourceforge.plantuml.{FileFormat, FileFormatOption, SourceStringReader}

/**
  * @author gweinbach on 06/04/2021
  * @since 0.2.0
  */
trait PlantUMLService {

  def diagramSource(diagram: PlantUMLDiagram): Option[String]

  def diagramSVG(diagram: PlantUMLDiagram): Option[SVGString]

}

object PlantUMLService {

  case class SVGString(val svgString: String) extends AnyVal {

    def map(f: String => String): SVGString =
      SVGString(f(svgString))
  }
}

trait SimplePlantUMLService
  extends PlantUMLService {

  override def diagramSource(diagram: PlantUMLDiagram): Option[String] =
    Safe(diagram.render())
  
  override def diagramSVG(diagram: PlantUMLDiagram): Option[SVGString] =
    for {
      source <- diagramSource(diagram)
      svg <- sourceToSVG(source)
    } yield svg

  private def sourceToSVG(diagramSource: String): Option[SVGString] = {
    import java.io.ByteArrayOutputStream
    import java.nio.charset.Charset
    for {
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

object SimplePlantUMLService extends SimplePlantUMLService
