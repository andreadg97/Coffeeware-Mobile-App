package don.coffee.coffeeware

import kotlinx.android.parcel.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
class IngredienteBase(var nombreBase:String): Ingrediente(nombreBase)