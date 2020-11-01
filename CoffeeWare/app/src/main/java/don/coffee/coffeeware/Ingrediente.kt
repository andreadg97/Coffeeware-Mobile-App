package don.coffee.coffeeware

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
open class Ingrediente constructor (var nombre: String) : Parcelable