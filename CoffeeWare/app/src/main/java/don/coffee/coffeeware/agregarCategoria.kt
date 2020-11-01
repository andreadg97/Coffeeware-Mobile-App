package don.coffee.coffeeware

import android.app.DownloadManager
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_agregar_categoria.*
import kotlinx.android.synthetic.main.activity_agregar_producto.*
import kotlinx.android.synthetic.main.activity_manejar_categoria.*

class agregarCategoria : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agregar_categoria)

        var categoria = intent.getParcelableExtra<Categoria>("categoria")
        if (categoria != null){
            edtIdCat.setText(categoria.ID.toString())
            edtIdCat.isEnabled = false
            edtNombreCat.setText(categoria.nombre)
            textview_tituloCategoria.text = "Actualizar categoria"
            btn_categoriaAgregar.text = "Actualizar Categoria"
        }

        btn_Cancelar_categoria.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        btn_categoriaAgregar.setOnClickListener {
            if (textview_tituloCategoria.text.toString().equals("Actualizar categoria", true)){
                if(comprobar()){
                    actualizarCategoria()
                }else{
                    Toast.makeText(this, "Llenar campo vacio", Toast.LENGTH_SHORT).show()
                }

            } else {
                if(comprobar()){
                    agregarCategoria()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                }else{
                    Toast.makeText(this, "Llenar campo vacio", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    fun comprobar():Boolean{
        var id = edtIdCat.text.toString()
        var nombre = edtNombreCat.text.toString()
        if(id.equals("") || nombre.equals("")){
            return false
        }
        return true
    }


    private fun actualizarCategoria() {
        val url = "http://192.168.0.13:80/coffeeware/wsJSONActualizaCategoria.php?"

        var req = object:StringRequest(Request.Method.POST, url, Response.Listener { response ->
            if (response.toString().equals("actualiza", true)) {
                Toast.makeText(this, "ACTUALIZADO CON EXITO", Toast.LENGTH_SHORT).show()
                for (cat in SessionData.listaCategoria){
                    if (cat.ID == edtIdCat.text.toString().toInt()){
                        cat.nombre = edtNombreCat.text.toString()
                    }
                }
                edtIdCat.setText("")
                edtNombreCat.setText("")
            } else {
                Toast.makeText(this, "NO SE HA PODIDO ACTUALIZAR", Toast.LENGTH_SHORT).show()
            }

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)

        }, Response.ErrorListener {
            Toast.makeText(this, "No se ha podido conectar a la Base de datos",Toast.LENGTH_SHORT).show()
        }){
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["ID"] = edtIdCat.text.toString()
                params["nombre"] = edtNombreCat.text.toString()
                return params
            }
        }

        var requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(req)
    }

    fun agregarCategoria(){
        val url = "http://192.168.0.13:80/coffeeware/wsJSONRegistroCategorias.php?ID="+edtIdCat.text.toString()+"&nombre="+edtNombreCat.text.toString()

        val agg = JsonObjectRequest(Request.Method.POST,url,null, Response.Listener { response ->
            Toast.makeText(applicationContext, "OPERACIÓN EXITOSA", Toast.LENGTH_SHORT).show()
            val categoria = Categoria(edtNombreCat.text.toString(), edtIdCat.text.toString().toInt())
            SessionData.listaCategoria.add(categoria)
            edtIdCat.setText("")
            edtNombreCat.setText("")
        },Response.ErrorListener { error ->
            Toast.makeText(applicationContext, error.toString(), Toast.LENGTH_SHORT).show()
        })

        var requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(agg)
    }
}
