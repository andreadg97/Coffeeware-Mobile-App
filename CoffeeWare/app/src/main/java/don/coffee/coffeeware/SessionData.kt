package don.coffee.coffeeware

class SessionData {
    companion object{
        var ordenActual = ArrayList<ProductoPersonalizado>()
        var ordenes = ArrayList<Orden>()
        var listaProductos = ArrayList<Producto>()
        var listaCategoria = ArrayList<Categoria>()
        var ordenEdit = Orden()
    }
}