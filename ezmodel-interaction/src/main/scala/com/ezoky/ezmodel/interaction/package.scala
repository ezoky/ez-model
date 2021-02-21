package com.ezoky.ezmodel

import com.ezoky.ezmodel
import com.ezoky.ezmodel.core.Models._

/**
  * @author gweinbach on 04/02/2021
  * @since 0.2.0 */
package object interaction {



  abstract class InteractiveShell[S]() {
    var currentState: S
  }




  case object CurrentModel {
    def contains[T](something: T): Boolean =
      false
  }

}
