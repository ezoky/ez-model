package com.ezoky.ezmodel.core

/**
  * @author gweinbach on 08/02/2021
  * @since 0.2.0
  */
private[core] trait Models
  extends Domains {

  case class Model(name: Name,
                   domains: Set[Domain] = Set.empty[Domain]) {

    def addDomain(domain: Domain): Model =
      copy(domains = domains + domain)
  }

//  case class Model[DomainId](name: Name,
//                             domains: Map[DomainId, Domain] =
//                             Map.empty[DomainId, Domain])
//                            (implicit
//                             domainId: NaturalId.Aux[Domain, DomainId]) {
//
//    def addDomain(domain: Domain): Model[DomainId] =
//      copy(domains = domains + (domainId(domain), domain))
//  }

}

object Models extends Models