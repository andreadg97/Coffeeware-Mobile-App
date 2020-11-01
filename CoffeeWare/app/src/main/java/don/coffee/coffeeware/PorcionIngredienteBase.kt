package don.coffee.coffeeware

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class PorcionIngredienteBase(var cantidad:Int, var ingrediente:Ingrediente):Parcelable