package pt.ipt.dam2023.neei_ipt.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import pt.ipt.dam2023.neei_ipt.R
import pt.ipt.dam2023.neei_ipt.model.ChangePasswordRequest
import pt.ipt.dam2023.neei_ipt.model.RecoverPasswordRequest
import pt.ipt.dam2023.neei_ipt.model.TransactionRequest
import pt.ipt.dam2023.neei_ipt.retrofit.RetrofitInitializer
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChangePasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_pass)

        val username = intent.getStringExtra("username")
        val pwdEdit = findViewById<EditText>(R.id.pwdEdit)
        val pwdrEdit = findViewById<EditText>(R.id.pwdREdit)
        val alterarBtn = findViewById<Button>(R.id.alterarBtn)

        alterarBtn.setOnClickListener {
            if (pwdEdit.text.toString()!= "" && pwdrEdit.text.toString()!= ""){
                if (pwdEdit.text.toString() == pwdrEdit.text.toString()){
                    val req = ChangePasswordRequest(
                        username= username,
                        password = pwdrEdit.text.toString()
                    )
                    // Chamada da função que comunica com a API, para registar uma transação
                    changePassword(req){statusCode ->
                        if (statusCode == 200) {
                            // Registo bem sucedido
                            Toast.makeText(this, "Password alterada com sucesso", Toast.LENGTH_LONG).show()
                            val intent = Intent(this, LoginActivity::class.java)
                            startActivity(intent)
                        }else if(statusCode==404){
                            Toast.makeText(this, "Erro. Contacte o Administrador", Toast.LENGTH_SHORT).show()
                        }else{
                            // Erro não identificado / Falha no servidor
                            Toast.makeText(this, "Erro. Contacte o Administrador", Toast.LENGTH_SHORT).show()
                        }
                    }
                }else{
                    Toast.makeText(this, "As password's não coicidem", Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(this, "Insira as password's", Toast.LENGTH_SHORT).show()
            }


        }
    }

    // Função para alterar passsword
    private fun changePassword(request: ChangePasswordRequest, onResult: (Int) -> Unit) {
        // Faz a chamada a API
        val call = RetrofitInitializer().APIService().changePassword(request)

        call.enqueue(object : Callback<Void> {
            override fun onFailure(call: Call<Void>, t: Throwable) {
                t?.message?.let { Log.e("onFailure error", it) }
                onResult(501)
            }
            // Retorna o StatusCode da resposta
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                onResult(response.code())
            }
        })
    }
}