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
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_lista_ordenes.*
import kotlinx.android.synthetic.main.activity_manejar_categoria.*
import kotlinx.android.synthetic.main.viewcategoria.*
import kotlinx.android.synthetic.main.viewcategoria.view.*

class manejarCategoria : AppCompatActivity() {
    var adaptador:adaptadorCategoria?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manejar_categoria)
        adaptador = adaptadorCategoria(this, SessionData.listaCategoria)
        listview_categorias.adapter = adaptador
        adaptador!!.notifyDataSetChanged()

        btn_agregarCategoria.setOnClickListener{
            val intent = Intent(this, agregarCategoria::class.java)
            startActivity(intent)
        }
    }

    fun eliminarCategoria(categoria: Categoria){

        val url = "http://192.168.1.74:80/coffeeware/wsJSONEliminarCategoria.php?ID="+categoria.ID.toString()

        var stringRequest = StringRequest(url, Response.Listener<String> { response ->
            if (response.trim().equals("elimina", true)){
                Toast.makeText(applicationContext, "ELIMINADO CON EXITO", Toast.LENGTH_SHORT).show()
                SessionData.listaCategoria.remove(categoria)
                adaptador!!.notifyDataSetChanged()
            }else{
                if (response.trim().equals("noExiste", true)){
                    Toast.makeText(applicationContext, "No se encuentra a la persona", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(applicationContext, "No se ha eliminado", Toast.LENGTH_SHORT).show()
                }
            }
        },
            Response.ErrorListener {
                Toast.makeText(applicationContext, toString(), Toast.LENGTH_SHORT).show()
            }
        )
        var requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(stringRequest)

    }

    fun actualizarCategoria(categoria: Categoria){
        val intent = Intent(this, agregarCategoria::class.java)
        intent.putExtra("categoria", categoria)
        startActivity(intent)
    }

    inner class adaptadorCategoria:BaseAdapter{

        var categorias = ArrayList<Categoria>()
        var context: Context? = null

        constructor(context:Context,categorias:ArrayList<Categoria>){
            this.context = context
            this.categorias = categorias
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            var categoria = categorias[position]
            var inflater = context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            var vista = inflater.inflate(R.layout.viewcategoria,null)

            vista.textview_nombrecategoria.setText(categoria.nombre)
            vista.textview_categoriaid.setText(categoria.ID.toString())

            vista.btn_editarCategoria.setOnClickListener {
                actualizarCategoria(categoria)
            }

            vista.btn_eliminarCategoria.setOnClickListener {
                eliminarCategoria(categoria)
            }

            return vista
        }

        override fun getItem(position: Int): Any {
            return categorias[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getCount(): Int {
           return categorias.size
        }

    }



}
