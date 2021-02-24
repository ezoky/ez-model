package com.ezoky.ezmodel.core

import com.ezoky.ezmodel.core.NaturalId.Dictionary
import org.scalatest.funsuite.AnyFunSuite

/**
  * @author gweinbach on 24/02/2021
  * @since 0.2.0
  */
class NaturalIdTest
  extends AnyFunSuite {

  test("Natural Id is contextual") {

    case class Point(x: Int, y: Int)

    {
      // On the plane
      implicit object PlanePos extends NaturalId[Point] {
        override type IdType = (Int, Int)

        override def apply(t: Point): (Int, Int) = (t.x, t.y)
      }

      val points = Dictionary(Point(1, 2), Point(2, 3), Point(1, 4), Point(2, 4), Point(1, 2))

      assert(points.size === 4)
    }

    {
      // On the abscissa
      implicit object AbscissaPos extends NaturalId[Point] {
        override type IdType = Int

        override def apply(t: Point): Int = t.x
      }

      val points = Dictionary(Point(1, 2), Point(2, 3), Point(1, 4), Point(2, 4), Point(1, 2))

      assert(points.size === 2)

    }

    {
      // On the ordinate
      implicit object OrdinatePos extends NaturalId[Point] {
        override type IdType = Int

        override def apply(t: Point): Int = t.y
      }

      val points = Dictionary(Point(1, 2), Point(2, 3), Point(1, 4), Point(2, 4), Point(1, 2))

      assert(points.size === 3)

    }
  }

}
