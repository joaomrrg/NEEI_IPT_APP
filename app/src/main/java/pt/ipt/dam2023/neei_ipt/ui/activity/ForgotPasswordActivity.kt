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

        // Ponteiros de elementos da View
        val emailEdit = findViewById<EditText>(R.id.emailEdit)
        val sendEmailbtn = findViewById<Button>(R.id.sendEmailBtn)

        // Evento de Mouse Click no botão Recuperar Password
        sendEmailbtn.setOnClickListener {
            // Criação do objeto a enviar para recuperar password
            val req = RecoverPasswordRequest(
                email= emailEdit.text.toString()
            )
            // Verifica se o email inserido não está vazio e se contém '@' e '.'
            if (emailEdit.text.toString()!= "" && emailEdit.text.toString().contains('@') && emailEdit.text.toString().contains('.')){
                // Chamada da função que comunica com a API, para recuperar password
                recoverPassword(req){statusCode ->
                    if (statusCode == 200) {
                        // Recuperção com sucesso. Email enviado para o utilizador
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
            }else{
                Toast.makeText(this, "Email inválido ou não fornecido.", Toast.LENGTH_LONG).show()
            }

        }
    }

    /**
     * Função para recuperar passsword
     */
    private fun recoverPassword(request: RecoverPasswordRequest, onResult: (Int) -> Unit) {
        // Faz a chamada a API
        val call = RetrofitInitializer().APIService().recoverPassword(request)

        call.enqueue(object : Callback<Void> {
            // Tratamento de falha da chamada à API
            override fun onFailure(call: Call<Void>, t: Throwable) {
                t?.message?.let { Log.e("onFailure error", it) }
                onResult(501)
            }
            // Tratamento da resposta bem-sucedida da chamada à API
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                onResult(response.code()) // Chama a função de retorno com o statusCode da resposta da API
            }
        })
    }
}