package don.coffee.coffeeware

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_agregar_producto.*
import org.json.JSONObject

class agregarProducto : AppCompatActivity() {

    var producto:Producto? =  null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agregar_producto)

         producto = intent.getParcelableExtra<Producto>("producto")
        if(producto!=null){
            edtId.setText(producto!!.ID.toString())
            edtId.isEnabled = false
            edtTipo.setText(producto!!.producto_type)
            edtNombre.setText(producto!!.nombre)
            edtPrecioBase.setText(producto!!.preciobase.toString())
            edtCategoria.setText(producto!!.categoria.ID.toString())
            btn_aceptaragregar.setText("Actualizar producto")
            textview_titulo.setText("Actualizar producto")
        }

        btn_Cancelar_producto.setOnClickListener {
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
        }

        btn_aceptaragregar.setOnClickListener {
            if(textview_titulo.text.toString()=="Actualizar producto"){
                if(comprobación()){
                    actualizarProducto()
                }else{
                    Toast.makeText(applicationContext, "Llenar campo vacio", Toast.LENGTH_SHORT).show()
                }

            }else{
                if(comprobación()){
                    agregarProducto()
                    var intent = Intent(this,listaProductos::class.java)
                    startActivity(intent)
                }else{
                    Toast.makeText(applicationContext, "Llenar campo vacio", Toast.LENGTH_SHORT).show()
                }

            }
        }
    }

    fun comprobación():Boolean{

        var id = edtId.text.toString()
        var nombre = edtNombre.text.toString()
        var tipo = edtTipo.text.toString()
        var idCat = edtCategoria.text.toString()
        var precio = edtPrecioBase.text.toString()

        if(id.equals("") || nombre.equals("") || tipo.equals("") || idCat.equals("")||precio.equals("")){
            return false
        }
        return true
    }

    fun agregarProducto(){
        var url: String="http://192.168.0.13:80/coffeeware/wsJSONRegistroProducto.php?ID="+edtId.text.toString()+"&producto_type="+edtTipo.text.toString()+"&nombre="+edtNombre.text.toString()+"&preciobase="+edtPrecioBase.text.toString()+"&id_categoria="+edtCategoria.text.toString()
        url=url.replace(" ","%20")
        val jsonobject= JsonObjectRequest(
            Request.Method.POST,url,null,
            Response.Listener<JSONObject?> {
                Toast.makeText(applicationContext, "OPERACIÓN EXITOSA", Toast.LENGTH_SHORT).show()
                val producto = Producto(edtId.text.toString().toInt(), edtTipo.text.toString(),edtNombre.text.toString(),edtPrecioBase.toString().toDouble(),SessionData.listaCategoria.first {
                    it.ID == edtCategoria.text.toString().toInt()
                })
                SessionData.listaProductos.add(producto)
                edtId.setText("")
                edtTipo.setText("")
                edtNombre.setText("")
                edtPrecioBase.setText("")
                edtCategoria.setText("")
            },
            Response.ErrorListener {
                Toast.makeText(applicationContext, it.toString(), Toast.LENGTH_SHORT).show()
            }
        )
        var requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(jsonobject)
    }

    fun actualizarProducto(){

        var url: String="http://192.168.0.13:80/coffeeware/wsJSONActualizarProducto.php"
        val req = object:StringRequest(Request.Method.POST, url, Response.Listener { response ->
            if (response.toString().trim().equals("actualiza", true)){
                Toast.makeText(applicationContext, "ACTUALIZADO CON EXITO", Toast.LENGTH_SHORT).show()
                for (prod in SessionData.listaProductos){
                    if (prod.ID == edtId.text.toString().toInt()){
                        prod.producto_type = edtTipo.text.toString()
                        prod.nombre = edtNombre.text.toString()
                        prod.preciobase = edtPrecioBase.text.toString().toDouble()
                        prod.categoria = SessionData.listaCategoria.first {
                            it.ID == edtCategoria.text.toString().toInt()
                        }
                    }
                }
                edtId.setText("")
                edtTipo.setText("")
                edtNombre.setText("")
                edtPrecioBase.setText("")
                edtCategoria.setText("")
            }else{
                Toast.makeText(applicationContext, "No se ha actualizado", Toast.LENGTH_SHORT).show()
            }
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }, Response.ErrorListener {
            Toast.makeText(applicationContext, "No se ha podido conectar a la Base de datos", Toast.LENGTH_SHORT)
        }){
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["ID"] = edtId.text.toString()
                params["producto_type"] = edtTipo.text.toString()
                params["nombre"] = edtNombre.text.toString()
                params["preciobase"] = edtPrecioBase.text.toString()
                params["id_categoria"] = edtCategoria.text.toString()
                return params
            }
        }

        var requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(req)
    }

}
