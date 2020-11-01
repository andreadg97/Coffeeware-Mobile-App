package don.coffee.coffeeware

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_lista_ordenes.*
import kotlinx.android.synthetic.main.viewlistaordenes.view.*

class listaOrdenes : AppCompatActivity() {

    var ordenes = SessionData.ordenes
    var adaptador: adaptadorOrdenes? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_ordenes)

        adaptador = adaptadorOrdenes(this, ordenes)
        listview_ordenes.adapter = adaptador
    }

    fun editarOrden(orden: Orden) {
        SessionData.ordenActual = orden.productos!!
        var intent = Intent(applicationContext, EditOrder::class.java)
        intent.putExtra("orden", orden)
        startActivity(intent)
    }

    fun validacionEliminarOrden(orden: Orden) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Importante")
        builder.setMessage("Se eliminará la orden. ¿Desea continuar?")
        builder.setPositiveButton("OK") { dialog, which ->
            eliminarOrden(orden)
        }
        builder.setNegativeButton("Cancelar") { dialog, which ->
            Toast.makeText(
                applicationContext,
                "Se canceló la eliminación. Volviendo a la pantalla principal.",
                Toast.LENGTH_SHORT
            ).show()
        }
        builder.create()
        builder.show()
    }

    fun eliminarOrden(orden: Orden) {
        var id = orden.ID
        var urlEliminar = "http://192.168.0.13:80/coffeeware/wsJSONEliminarOrden.php?ID=" + id

        val eliminar = StringRequest(
            Request.Method.GET,
            urlEliminar,
            Response.Listener<String> {
                Toast.makeText(this, "Orden Eliminada", Toast.LENGTH_LONG).show()
                ordenes.remove(orden)
                adaptador!!.notifyDataSetChanged()
            },
            Response.ErrorListener { error ->
                Toast.makeText(this, "Error al acceder a la base de datos", Toast.LENGTH_LONG)
                    .show()

            })
        var requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(eliminar)
    }

    fun pagarOrden(orden: Orden) {
        var intent = Intent(applicationContext, paymentActivity::class.java)
        intent.putExtra("orden", orden)
        startActivity(intent)
    }

    inner class adaptadorOrdenes : BaseAdapter {
        var ordenes = ArrayList<Orden>()
        var contexto: Context? = null

        constructor(contexto: Context, ordenes: ArrayList<Orden>) {
            this.ordenes = ordenes
            this.contexto = contexto
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            var orden = ordenes[position]
            var inflater =
                contexto!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            var vista = inflater.inflate(R.layout.viewlistaordenes, null)

            vista.textview_nombreCliente.text = orden.cliente
            vista.textview_costoOrden.text = orden.preciofinal.toString()
            vista.textview_estadoOrden.text = orden.ESTADO
            vista.textview_idOrden.text = orden.ID.toString()

            if (!orden.ESTADO.equals("en cola", true)) vista.btn_eliminarOrden.isGone = true

            vista.btn_eliminarOrden.setOnClickListener {
                validacionEliminarOrden(orden)
            }

            vista.btn_editarOrden.setOnClickListener {
                editarOrden(orden)
            }

            vista.btn_pagarOrden.setOnClickListener {
                pagarOrden(orden)
            }

            return vista
        }

        override fun getItem(position: Int): Any {
            return ordenes[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getCount(): Int {
            return ordenes.size
        }

    }

}