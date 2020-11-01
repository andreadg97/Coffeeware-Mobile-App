package don.coffee.coffeeware

import kotlinx.android.parcel.Parcelize

@Parcelize
open class ProductoPersonalizado (
    var idPersonalizado: Int,
    var tipoPersonalizado:String,
    var nombrePersonalizado: String,
    var precioBasePersonalizado: Double,
    var categoriaPersonalizado: Categoria,
    var imagePersonalizado: Int,
    var descripcionPersonalizado: String,
    var ingredientesBasePersonalizado: ArrayList<PorcionIngredienteBase>,
    var ingredientesExtraPersonalizado: ArrayList<IngredienteExtra>,
    var orden: Orden?,
    var nota: String,
    var precioExtra: Double,
    var cantidad: Int
): Producto(idPersonalizado,tipoPersonalizado,nombrePersonalizado, precioBasePersonalizado,categoriaPersonalizado, imagePersonalizado, descripcionPersonalizado, ingredientesBasePersonalizado, ingredientesExtraPersonalizado){
    constructor(producto: Producto) : this(producto.ID, producto.producto_type, producto.nombre, producto.preciobase, producto.categoria, producto.image, producto.descripcion, producto.ingredientesBase, producto.ingredientesExtra, null, "", 0.0, 1){

    }
}