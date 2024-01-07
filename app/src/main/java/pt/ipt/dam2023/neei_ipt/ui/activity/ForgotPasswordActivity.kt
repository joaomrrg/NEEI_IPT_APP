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
import pt.ipt.dam2023.neei_ipt.model.RecoverPasswordRequest
import pt.ipt.dam2023.neei_ipt.model.TransactionRequest
import pt.ipt.dam2023.neei_ipt.retrofit.RetrofitInitializer
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ForgotPasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recup_pass)

        val emailEdit = findViewById<EditText>(R.id.emailEdit)
        val sendEmailbtn = findViewById<Button>(R.id.sendEmailBtn)

        sendEmailbtn.setOnClickListener {
            val req = RecoverPasswordRequest(
                email= emailEdit.text.toString()
            )
            // Chamada da função que comunica com a API, para registar uma transação
            recoverPassword(req){statusCode ->
                if (statusCode == 200) {
                    // Registo bem sucedido
                    Toast.makeText(this, "Password de recuperação enviada para o seu email.", Toast.LENGTH_LONG).show()
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                }else if(statusCode==404){
                    Toast.makeText(this, "O email não está associado a nenhuma conta.", Toast.LENGTH_LONG).show()
                }else{
                    // Erro não identificado / Falha no servidor
                    Toast.makeText(this, "Erro. Contacte o Administrador", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // Função para recuperar passsword
    private fun recoverPassword(request: RecoverPasswordRequest, onResult: (Int) -> Unit) {
        // Faz a chamada a API
        val call = RetrofitInitializer().APIService().recoverPassword(request)

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