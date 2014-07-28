package com.ezoky.ezmodel.storage

object Core {

	import com.ezoky.ezmodel.core._
	
	import Atoms._
	import UseCases._
	import Domains._
	import Structures._
	import EzModel._
	
	EzModel                                   //> res0: com.ezoky.ezmodel.core.EzModel.type = com.ezoky.ezmodel.core.EzModel$@
                                                  //| 3ff6929c
	
	val dom = Domain("Driving").useCase("Driver","change","Gear").useCase("Driver","brake").entity("Gear")
                                                  //> dom  : com.ezoky.ezmodel.core.Domains.Domain = Domain(Name(Driving))
	dom.useCases                              //> res1: List[com.ezoky.ezmodel.core.UseCases.UseCase] = List(UseCase(Actor(Nam
                                                  //| e(Driver)),Goal(Action(Verb(brake)),null)), UseCase(Actor(Name(Driver)),Goal
                                                  //| (Action(Verb(change)),ActionObject(NameGroup(Determinant(the),Name(Gear)))))
                                                  //| )
	dom.entities                              //> res2: List[com.ezoky.ezmodel.core.Structures.Entity] = List(Entity(Name(Gear
                                                  //| )))
	val monthlyInvoicing = asAn("Accountant") iWantTo("invoice",(a,"Month")) preCondition EntityState("Production","Done") postCondition	EntityState("Current Month","Invoiced")
                                                  //> monthlyInvoicing  : com.ezoky.ezmodel.core.UseCases.PostCondition = UseCase(
                                                  //| Actor(Name(Accountant)),Goal(Action(Verb(invoice)),ActionObject(NameGroup(De
                                                  //| terminant(a),Name(Month)))))
  monthlyInvoicing.constraints                    //> res3: scala.collection.immutable.Map[com.ezoky.ezmodel.core.Constrains.Const
                                                  //| raintType,List[com.ezoky.ezmodel.core.Structures.EntityState]] = Map(Constra
                                                  //| intType(pre-condition) -> List(EntityState(Name(Production [Done]))), Constr
                                                  //| aintType(post-condition) -> List(EntityState(Name(Current Month [Invoiced]))
                                                  //| ))
  
	Domain("Accounting") useCase(monthlyInvoicing)
                                                  //> res4: com.ezoky.ezmodel.core.Domains.Domain = Domain(Name(Accounting))
	
	val offre = Entity("Offre") aggregate(multiple, Entity("Gamme") aggregate(multiple, Entity("Sous-Gamme") aggregate(multiple, "Prestation"))) attribute("nom")
                                                  //> offre  : com.ezoky.ezmodel.core.Structures.Structure[com.ezoky.ezmodel.core.
                                                  //| Structures.Entity] = anon$2(Name(Offre))
	offre.name                                //> res5: com.ezoky.ezmodel.core.Atoms.Name = Name(Offre)
	offre.attributes                          //> res6: List[com.ezoky.ezmodel.core.Structures.Attribute] = List(Attribute(Nam
                                                  //| e(nom),Multiplicity(single),false))
	offre.aggregates                          //> res7: List[com.ezoky.ezmodel.core.Structures.Aggregate] = List(Aggregate(ano
                                                  //| n$3(Name(Offre)),Name(<default>),anon$3(Name(Gamme)),Multiplicity(single),fa
                                                  //| lse))
	offre.references                          //> res8: List[com.ezoky.ezmodel.core.Structures.Reference] = List()
                                                  
	
  domainRepository.populate
  domainRepository.queryEntitiesCount             //> res9: Int = 2
 domainRepository.queryEntities.foreach(println)  //> Domain(Name(Driving))
                                                  //| Domain(Name(Accounting))
  domainRepository.queryAllVersionsCount          //> res10: Int = 6
 
  entityRepository.populate
  entityRepository.queryEntitiesCount             //> res11: Int = 9
  entityRepository.queryEntities.foreach(println) //> Entity(Name(Production))
                                                  //| Entity(Name(Gear))
                                                  //| EntityState(Name(Production [Done]))
                                                  //| anon$2(Name(Offre))
                                                  //| EntityState(Name(Current Month [Invoiced]))
                                                  //| Entity(Name(Current Month))
                                                  //| anon$3(Name(Sous-Gamme))
                                                  //| anon$3(Name(Gamme))
                                                  //| Entity(Name(Prestation))
	entityRepository.queryAllVersionsCount    //> res12: Int = 13

	useCaseRepository.populate
  useCaseRepository.queryEntitiesCount            //> res13: Int = 3
  useCaseRepository.queryEntities.foreach(println)//> UseCase(Actor(Name(Driver)),Goal(Action(Verb(change)),ActionObject(NameGrou
                                                  //| p(Determinant(the),Name(Gear)))))
                                                  //| UseCase(Actor(Name(Driver)),Goal(Action(Verb(brake)),null))
                                                  //| UseCase(Actor(Name(Accountant)),Goal(Action(Verb(invoice)),ActionObject(Nam
                                                  //| eGroup(Determinant(a),Name(Month)))))
	useCaseRepository.queryAllVersionsCount   //> res14: Int = 5
}