
import com.ezoky.ezmodel.core.Models._

val Say = com.ezoky.ezmodel.core.dsl.DSL
import Say._


val dom = Say inDomain "Driving" withUseCase ("Driver", "change" the "Gear") withUseCase ("Driver", "brake") withEntity "Gear"

dom.useCases
dom.entities
println(dom)

val monthlyInvoicing = Say asAn "Accountant" iWantTo ("invoice" a "Month") withPreCondition ("Production" is "Done") withPostCondition ("Current Month" is "Invoiced")
monthlyInvoicing.constraints

Domain("Accounting") withUseCase (monthlyInvoicing)

val offre = Entity("Offre") aggregates many (Entity("Gamme") aggregates many (Entity("Sous-Gamme") aggregates many ("Prestation"))) withAttribute "nom"
offre.name
offre.attributes
offre.aggregated
offre.referenced
