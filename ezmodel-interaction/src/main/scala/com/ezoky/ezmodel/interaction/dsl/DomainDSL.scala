package com.ezoky.ezmodel.interaction.dsl

import com.ezoky.ezmodel.core.Models._


/**
  * @author gweinbach on 08/02/2021
  * @since 0.2.0
  */
trait DomainDSL {

  def inDomain(domainName: String): Domain =
    Domain(Name(domainName))

  implicit class DomainHelper(domain: Domain) {

    def asA(name: Name): Actor = Actor(name)

    def asAn(name: Name): Actor = asA(name)
  }
}
