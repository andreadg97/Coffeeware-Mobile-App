package don.coffee.coffeeware

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.BaseAdapter
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_confirm_order.list_orden
import kotlinx.android.synthetic.main.activity_payment.*
import kotlinx.android.synthetic.main.activity_personalizar_producto.*
import kotlinx.android.synthetic.main.producto_orden.*
import kotlinx.android.synthetic.main.producto_orden.view.*

class paymentActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        val orden = intent.getParcelableExtra<Orden>("orden")

        var adaptador = paymentAdaptor(this, orden.productos!!)
        list_orden.adapter = adaptador

        var intentMenu = Intent(this, MainActivity::class.java)

        precioTotalPago.text = orden.preciofinal.toString()

        txtview_cambio.text = "$ 0.0"

        btnCancelar.setOnClickListener{
            startActivity(intentMenu)
        }

        btnConfirm.setOnClickListener{
            mostrarCambio()
        }
    }

    fun mostrarCambio(){

        var novalido = false

        for (x in txtview_pago.text.toString()){
            if(!x.isDigit()){
                Toast.makeText(this,"Ingrese numeros solamente",Toast.LENGTH_SHORT).show()
                novalido = true
                break
            }
        }

        if(novalido == false) {

            var total = precioTotalPago.text.toString().toDouble()
            var pago = txtview_pago.text.toString().toDouble()
            var cambio = pago - total

            if (cambio < 0) {
                txtview_pago.hint = ""
                Toast.makeText(
                    this,
                    "No puede pagar con esa cantidad, es menor al total",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                txtview_cambio.text = cambio.toString()
            }

        }
    }
    
    private class paymentAdaptor:BaseAdapter{
        var context: Context? = null
        var productosPersonalizado = ArrayList<ProductoPersonalizado>()

        constructor(context: Context, productosPersonalizado: ArrayList<ProductoPersonalizado>) {
            this.context = context
            this.productosPersonalizado = productosPersonalizado
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            var productoPersonalizado = productosPersonalizado[position]
            var inflator = LayoutInflater.from(context)
            var vista = inflator.inflate(R.layout.producto_orden, null)
            vista.product_name.text = productoPersonalizado.nombrePersonalizado
            vista.precio.text = (productoPersonalizado.precioExtra + productoPersonalizado.preciobase).toString()

            return vista
        }

        override fun getItem(position: Int): Any {
            return productosPersonalizado[position]
        }

        override fun getItemId(position: Int): Long {
            return 1
        }

        override fun getCount(): Int {
            return productosPersonalizado.size
        }


    }

}
