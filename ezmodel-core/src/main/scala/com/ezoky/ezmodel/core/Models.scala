package com.ezoky.ezmodel.core

/**
  * @author gweinbach on 08/02/2021
  * @since 0.2.0
  */
private[core] trait Models
  extends Domains {

  case class Model(name: Name,
                   domains: DomainMap = DomainMap.empty)
                  (implicit
                   domainId: DomainId) {

    def withDomain(domain: Domain): Model =
      copy(domains = domains + (domainId(domain) -> domain))
  }

}

object Models extends Models