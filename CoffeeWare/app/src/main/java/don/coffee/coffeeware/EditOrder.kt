package don.coffee.coffeeware
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_confirm_order.*
import kotlinx.android.synthetic.main.activity_editar_ordenes.*
import kotlinx.android.synthetic.main.activity_editar_ordenes.view.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.ingrediente_view.view.*
import kotlinx.android.synthetic.main.ingrediente_view.view.remove
import kotlinx.android.synthetic.main.producto_editar_orden.view.*
import kotlinx.android.synthetic.main.producto_orden.view.*
import kotlinx.android.synthetic.main.producto_orden.view.precio
import kotlinx.android.synthetic.main.producto_orden.view.product_name
import kotlinx.android.synthetic.main.producto_view.view.*
import kotlinx.android.synthetic.main.viewlistaproductos.view.*

class EditOrder : AppCompatActivity() {

    var productosPersonalizados = ArrayList<ProductoPersonalizado>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_ordenes)

        var orden = intent.getParcelableExtra<Orden>("orden")


        if(orden != null){
            productosPersonalizados = orden.productos!!
        }else if (SessionData.ordenEdit.ID != 0){
            SessionData.ordenEdit.productos = SessionData.ordenActual
            SessionData.ordenEdit.preciofinal = obtenerPrecioFinal()
            orden = SessionData.ordenEdit
            productosPersonalizados = SessionData.ordenActual
        }else{
            productosPersonalizados = SessionData.ordenActual
        }




        //Adaptador
        var adaptador = AdaptadorProductosPersonalizados(this, productosPersonalizados)
        list_ordenEdit.adapter = adaptador
        var intentMain = Intent(this, MainActivity::class.java)


        precioTotalEdit.text = obtenerPrecioFinal().toString()
        //Botón para confirmar edición
        //No lo envía a la base de datos
        btn_guardarcambios.setOnClickListener(){
            if (orden != null) {
                actualizarOrden(orden)
                for (ord in SessionData.ordenes){
                    if (ord.ID == orden.ID){
                        ord.preciofinal = orden!!.preciofinal
                        ord.productos = orden.productos
                    }
                }
                SessionData.ordenEdit = Orden()
                startActivity(intentMain)
            } else {
                var intent = Intent(this, ConfirmOrder::class.java)
                startActivity(intent)
            }
        }

        //Botón para añadir más productos
        btn_añadirproducto.setOnClickListener(){
            if(orden != null){
                SessionData.ordenEdit = orden
            }
            startActivity(intentMain)
        }

        //Botón para cancelar orden
        btn_cancelar.setOnClickListener(){
            productosPersonalizados.clear()
            SessionData.ordenEdit = Orden()
            startActivity(intentMain)
            adaptador!!.notifyDataSetChanged()
        }


    }

    fun actualizarOrden(orden: Orden){
        var url = "http://192.168.0.13/coffeeware/wsJSONActualizarOrden.php?"

        val request = object: StringRequest(
            Method.POST, url, Response.Listener { response ->
                Toast.makeText(this, "Orden actualizada", Toast.LENGTH_SHORT).show()
            }, Response.ErrorListener {
                Toast.makeText(this, it.toString(), Toast.LENGTH_SHORT).show()
            }
        ){
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["ID"] = orden.ID.toString()
                params["cliente"] = orden.cliente
                params["ESTADO"] = orden.ESTADO
                params["preciofinal"] = orden.preciofinal.toString()
                return params
            }
        }

        var requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(request)

    }

    fun personalizar(producto: Producto){
        val intent = Intent(this, PersonalizarProductoActivity::class.java)
        val orden = this.intent.getParcelableExtra<Orden>("orden")
        intent.putExtra("orden", orden)
        intent.putExtra("productoEdit", producto)
        startActivity(intent)
    }

    fun obtenerPrecioFinal(): Double{
        var total: Double = 0.0

        for(x in SessionData.ordenActual){
            total += x.preciobase+x.precioExtra
        }
        return total
    }

    inner class AdaptadorProductosPersonalizados : BaseAdapter {
        var productosPersonalizados = ArrayList<ProductoPersonalizado>()
        var contexto: Context? = null

        constructor(contexto: Context, productosPersonailzados: ArrayList<ProductoPersonalizado>) {
            this.contexto = contexto
            this.productosPersonalizados = productosPersonailzados

        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            var productoPersonalizado = productosPersonalizados[position]
            var inflator = LayoutInflater.from(contexto)
            var vista = inflator.inflate(R.layout.producto_editar_orden, null)

            vista.product_name.text = productoPersonalizado.nombrePersonalizado
            vista.precio.text = (productoPersonalizado.precioBasePersonalizado+productoPersonalizado.precioExtra).toString()


            //botonEliminarProductoP
            vista.remove.setOnClickListener {
            productosPersonalizados.remove(productosPersonalizados[position])
                this.notifyDataSetChanged()
                obtenerPrecioFinal()
            }

            //botonEditarProductoP
            vista.edit.setOnClickListener(){
                var productoTemp = SessionData.ordenActual[position]
                SessionData.ordenActual.removeAt(position)
                personalizar(productoTemp)
            }
            return vista
        }

        override fun getItem(position: Int): Any {
            return productosPersonalizados[position]
        }

        override fun getItemId(position: Int): Long {
            return 1
        }

        override fun getCount(): Int {
            return productosPersonalizados.size
        }

    }

}