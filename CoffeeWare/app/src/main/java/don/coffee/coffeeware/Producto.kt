package don.coffee.coffeeware

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
open class Producto(
    var ID:Int,
    var producto_type:String,
    var nombre: String,
    var preciobase: Double,
    var categoria:Categoria,
    var image: Int,
    var descripcion: String,
    var ingredientesBase: ArrayList<PorcionIngredienteBase>,
    var ingredientesExtra: ArrayList<IngredienteExtra>
): Parcelable{
    constructor(ID: Int, producto_type: String, nombre: String, preciobase: Double, categoria: Categoria): this(ID, producto_type, nombre, preciobase, categoria, 0, "", ArrayList<PorcionIngredienteBase>(), ArrayList<IngredienteExtra>())
}