package don.coffee.coffeeware

import kotlinx.android.parcel.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
class IngredienteExtra(var nombreExtra: String, var precio: Double): Ingrediente(nombreExtra)