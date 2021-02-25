package com.ezoky.ezmodel.interaction.dsl

import com.ezoky.ezmodel.core.Models._

/**
  * @author gweinbach on 19/02/2021
  * @since 0.2.0
  */
trait ModelDSL
  extends NaturalIdDSL
    with MergerDSL {

  def inModel(modelName: String): Model =
    Model(Name(modelName))
}
