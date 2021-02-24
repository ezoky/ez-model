package com.ezoky.ezmodel.core

/**
  * @author gweinbach on 08/02/2021
  * @since 0.2.0
  */
private[core] trait Models
  extends Domains
    with NaturalIds {

  case class Model(name: Name,
                   domains: DomainMap = DomainMap.empty)
                  (implicit
                   domainId: DomainId) {

    def withDomain(domain: Domain): Model =
      copy(domains = domains + (domainId(domain) -> domain))
  }


  type ModelId = NaturalId[Model]
  type ModelMap = NaturalMap[ModelId, Model]

  object ModelMap {
    def empty: ModelMap =
      NaturalMap.empty[ModelId, Model]

    def apply(models: Model*)
             (implicit
              id: ModelId): ModelMap =
      NaturalMap(models: _*)
  }

}

object Models extends Models