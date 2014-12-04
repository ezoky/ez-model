package com.ezmodel.ddd

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

/**
 * @author gweinbach
 */
@RunWith(classOf[JUnitRunner])
class BehaviourTest extends FunSuite {

  test("Identify Behaviour does not do anything") {
    val s1 = "A"
    val ib = ImmutableBehaviour[String]
    val s2 = ib(s1)

    assert(s1 === s2)
  }

//  test("a reflexive method is a Behaviour") {
//    case class Test(value: Int) {
//      def behaviour(t: Test) = Test(2 * value)
//
//      def noBehaviour1(t: Test) = 2 * value
//
//      def noBehaviour2(i: Int) = Test(2 * i)
//    }
//
//    import scala.reflect.runtime.{universe => ru}
//
//    import scala.reflect.internal.
//
//    //    val b = ru.typeOf[Test].member(TermName("behaviour")).asMethod
//    val t = Test(1)
//    val m = ru.runtimeMirror(t.getClass.getClassLoader)
//
//    val bm = ru.typeOf[Test].member(TermName("behaviour"))
//    val rbm = m.reflect(t)
//    val behaviourMeth = rbm.reflectMethod(bm.asMethod)
//
//    val tm = ru.typeOf[Behaviour.type].member(ru.newTermName("type"))
//    val rtm = m.reflect(tm)
//    val behaviourType = rtm.symbol.typeSignature
//  }
}
