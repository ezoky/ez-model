package com.ezoky.ezmodel.core

import com.ezoky.commons.NaturalIds

/**
  * @author gweinbach on 08/02/2021
  * @since 0.2.0
  */
private[core] trait Models
  extends Domains
    with interactions.Models
    with NaturalIds {

  case class Model(name: Name,
                   domains: DomainMap = DomainMap.empty)
                  (implicit
                   domainId: DomainId,
                   domainMerger: Merger[Domain]) {

    def ownsDomain(domain: Domain): Boolean =
      domains.owns(domain)

    def withDomain(domain: Domain): Model =
      copy(domains = domains + (domainId(domain) -> domain))

    def mergeDomain(domain: Domain): Model =
      copy(domains = domains.merge(domain))
  }


  type ModelId = NaturalId[Model]
  type ModelMap = NaturalMap[ModelId, Model]

  object ModelMap extends NaturalMapCompanion[ModelId, Model]

}

object Models extends Models