package com.ezoky.ezmodel.interaction.dsl

import com.ezoky.ezmodel.core.NaturalId
import com.ezoky.ezmodel.core.Models._

/**
  * @author gweinbach on 24/02/2021
  * @since 0.2.0
  */
trait NaturalIdDSL {

  implicit val DomainDSLNaturalId: NaturalId[Domain] =
    NaturalId.define(_.name)
}
