package don.coffee.coffeeware

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PorcionIngredienteExtra(var cantidad:Int,var ingrediente:Ingrediente): Parcelable