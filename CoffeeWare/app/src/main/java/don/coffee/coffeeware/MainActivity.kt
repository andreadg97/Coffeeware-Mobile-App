package don.coffee.coffeeware

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.producto_view.view.*
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.list
import org.json.JSONArray
import org.json.JSONObject
import java.lang.StringBuilder
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_personalizar_producto.*

class MainActivity : AppCompatActivity() {


    var JSON: JSONObject? = null
    var JSONarray: JsonArray? = null
    var productosCategoriaActual = ArrayList<Producto>()
    var adaptador: AdaptadorProductos? = null
    var categoriaActual = 1

    //Auxiliares
    var ing = IngredienteBase("Pastel")
    var porcionIngre = PorcionIngredienteBase(2, ing)
    var porciones = ArrayList<PorcionIngredienteBase>()
    var extra = IngredienteExtra("Leche", 5.0)
    var extras = ArrayList<IngredienteExtra>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Personalizando la action bar
        var actionBar = supportActionBar

        if (actionBar != null) {
            actionBar.setIcon(R.drawable.logofinal)
        }
        if (actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(true)
        }
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
        }

        var producto = Producto(1, "Hamburguesa", "Suiza", 55.0, categoria = Categoria("Alimento", 1), image = 1, descripcion = "Ta rica", ingredientesBase = ArrayList(), ingredientesExtra = ArrayList())

        adaptador = AdaptadorProductos(this, productosCategoriaActual)
        gridview_productos.adapter = adaptador

        adaptador!!.notifyDataSetChanged()
        cargarAuxiliares()
        if (SessionData.listaCategoria.isEmpty()){
            cargarCategorias("http://192.168.0.13:80/coffeeware/wsJSONConsultarListaCategorias.php")
        }else{
            mostrarCategoriaActual(categoriaActual)
            actualizarNumeroProductos()
            adaptador!!.notifyDataSetChanged()
        }

        adaptador!!.notifyDataSetChanged()


        btn_ordenactual.setOnClickListener {
            if (SessionData.ordenEdit.ID != 0){
                val intent = Intent(this, EditOrder::class.java)
                startActivity(intent)
            }else{
            val intent = Intent(this, ConfirmOrder::class.java)
            startActivity(intent)
            }
        }

        btn_ordenes.setOnClickListener{
            val intent = Intent(this, listaOrdenes::class.java)
            startActivity(intent)
        }

        btn_izquierda.setOnClickListener {

            for(x in SessionData.listaCategoria){
                if (x.ID == categoriaActual){
                    var indexactual = SessionData.listaCategoria.indexOf(x)

                    if(indexactual == 0){
                        categoriaActual = SessionData.listaCategoria[SessionData.listaCategoria.size -1].ID
                    }else{
                        categoriaActual = SessionData.listaCategoria[indexactual-1].ID
                    }

                    break
                }
            }

            mostrarCategoriaActual(categoriaActual)
        }

        btn_derecha.setOnClickListener {

            for(x in SessionData.listaCategoria){
                if (x.ID == categoriaActual){
                    var indexactual = SessionData.listaCategoria.indexOf(x)

                    if(indexactual == (SessionData.listaCategoria.size-1)){
                        categoriaActual = SessionData.listaCategoria[0].ID
                    }else{
                        categoriaActual = SessionData.listaCategoria[indexactual+1].ID
                    }

                    break
                }
            }

            mostrarCategoriaActual(categoriaActual)
        }
        btn_productos.setOnClickListener{

            val intent = Intent(this,listaProductos::class.java)
            startActivity(intent)

        }
        btn_categorias.setOnClickListener{
            val intent = Intent(this,manejarCategoria::class.java)
            startActivity(intent)
        }

        }

    fun personalizar(producto: Producto){
        val intent = Intent(this, PersonalizarProductoActivity::class.java)
        intent.putExtra("producto", producto)
        startActivity(intent)
    }

    fun cargarAuxiliares() {
        porciones.add(porcionIngre)
        porciones.add(porcionIngre)
        porciones.add(porcionIngre)

        extras.add(extra)
        extras.add(extra)
        extras.add(extra)

        println("Ya se crearon todos los extra")

    }

    fun desplegarTitulo() {
        for(x in SessionData.listaCategoria){
            if(x.ID == categoriaActual){
                textview_titulo.text = x.nombre
            }
        }
    }

    fun cargarCategorias(URL:String) {

        val jsonA = JsonObjectRequest(Request.Method.GET,URL,null,Response.Listener { response ->
            var JSON = response.getJSONArray("categoria")
            val gson = Gson()
            for(i in 0..JSON.length()-1){
                    var categoriaJson = JSON[i].toString()
                    var categoriaTemp:Categoria = gson.fromJson(categoriaJson,Categoria::class.java)
                var id:Int = categoriaTemp.ID
                var nombre:String = categoriaTemp.nombre
                SessionData.listaCategoria.add(Categoria(nombre,id))
                }

            if(!SessionData.listaCategoria.isNullOrEmpty()){
                categoriaActual = SessionData.listaCategoria[0].ID
                cargarAlimentos("http://192.168.0.13/coffeeware/wsJSONConsultarListaProductos.php")
            }

            },Response.ErrorListener { error ->
            Toast.makeText(this,error.toString(),Toast.LENGTH_LONG).show()
        })

        var requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(jsonA)
        adaptador!!.notifyDataSetChanged()

    }

    fun obtenerCategoria(ID: Int): Categoria{
        for(x in SessionData.listaCategoria){
            if(x.ID == ID){
                return x
            }
        }

        return Categoria("NULL",-1)
    }

    fun cargarAlimentos(URL:String) {
        val jsonobject = JsonObjectRequest(Request.Method.GET,URL,null,Response.Listener { response ->

            var JSON = response.getJSONArray("producto")
            val gson = Gson()
            var tam = JSON.length()-1

            for( i in 0..tam) {
               var jsonObject = JSON.getJSONObject(i)

                    var categoria: Categoria = obtenerCategoria(jsonObject.optString("id_categoria").toInt())

                    val productoTemp = gson.fromJson(jsonObject.toString(),Producto::class.java)

                    if (categoria.ID != -1){
                        productoTemp.categoria = categoria
                    }else{
                        Toast.makeText(this,"ERROR en categoria de producto",Toast.LENGTH_LONG).show()
                    }

                    productoTemp.image = R.drawable.image_icon
                    productoTemp.descripcion="Descripcion"
                    productoTemp.ingredientesBase = porciones
                    productoTemp.ingredientesExtra=extras

                    SessionData.listaProductos.add(productoTemp)

            }
            mostrarCategoriaActual(categoriaActual)
            actualizarNumeroProductos()
            adaptador!!.notifyDataSetChanged()

        },Response.ErrorListener { error ->
            Toast.makeText(this,error.toString(),Toast.LENGTH_LONG).show()
        })

        var requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(jsonobject)
    }

    fun actualizarNumeroProductos(){
        if(!SessionData.ordenActual.isNullOrEmpty()){
            textview_numeroproductos.text = SessionData.ordenActual.size.toString()
        }
    }

    fun mostrarCategoriaActual(idcategoria: Int){
        desplegarTitulo()
        productosCategoriaActual.clear()

        for(x in SessionData.listaProductos){
            if(x.categoria.ID == idcategoria){
                productosCategoriaActual.add(x)
            }
        }
        adaptador!!.notifyDataSetChanged()
    }

    inner class AdaptadorProductos : BaseAdapter {
        var productos = ArrayList<Producto>()
        var contexto: Context? = null

        constructor(contexto: Context, productos: ArrayList<Producto>) {
            this.contexto = contexto
            this.productos = productos
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            var producto = productos[position]
            var inflater =
                contexto!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            var vista = inflater.inflate(R.layout.producto_view, null)


            if(producto.nombre.length>15){
             var nombre=  producto.nombre.substring(0,14)+"..."
                vista.textview_nombre.text = nombre
            }else{
                vista.textview_nombre.text = producto.nombre
            }
            vista.btn_producto.setImageResource(R.drawable.vasito)
           // vista.textview_descripcion.text = producto.descripcion

            vista.btn_producto.setOnClickListener {
                personalizar(producto)
            }

            return vista
        }

        override fun getItem(position: Int): Any {
            return productos[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getCount(): Int {
            return productos.size
        }

    }
}