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

}

object Models extends Models