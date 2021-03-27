package com.ezoky.ezmodel.interaction.dsl

/**
  * @author gweinbach on 04/02/2021
  * @since 0.2.0
  */
object DSL
  extends AtomsDSL
    with UseCaseDSL
    with InteractionDSL
    with EntityDSL
    with DomainDSL
    with ModelDSL
    with DescriptorDSL
    with NaturalIdDSL
    with MergerDSL
