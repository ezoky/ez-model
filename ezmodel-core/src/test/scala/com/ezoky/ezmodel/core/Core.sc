
import com.ezoky.ezmodel.core.Atoms._
import com.ezoky.ezmodel.core.Domains._
import com.ezoky.ezmodel.core.Entities._
import com.ezoky.ezmodel.core.UseCases._


val dom = Domain("Driving").useCase("Driver", "change", "Gear").useCase("Driver", "brake").entity("Gear")

dom.useCases
dom.entities
println(dom)

val monthlyInvoicing = asAn("Accountant") iWantTo("invoice", (a, "Month")) preCondition EntityState("Production", "Done") postCondition EntityState("Current Month", "Invoiced")
monthlyInvoicing.constraints

Domain("Accounting") useCase (monthlyInvoicing)

val offre = Entity("Offre") aggregate(multiple, Entity("Gamme") aggregate(multiple, Entity("Sous-Gamme") aggregate(multiple, "Prestation"))) attribute ("nom")
offre.name
offre.attributes
offre.aggregates
offre.references
