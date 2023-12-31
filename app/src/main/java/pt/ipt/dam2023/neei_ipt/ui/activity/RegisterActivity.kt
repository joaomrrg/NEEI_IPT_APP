package pt.ipt.dam2023.neei_ipt.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import pt.ipt.dam2023.neei_ipt.R
import pt.ipt.dam2023.neei_ipt.model.ResponseAPI
import pt.ipt.dam2023.neei_ipt.model.RegisterRequest
import pt.ipt.dam2023.neei_ipt.retrofit.RetrofitInitializer
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Ponteiros de elementos da View
        val nomeText = findViewById<EditText>(R.id.nomeText)
        val apelidoText = findViewById<EditText>(R.id.apelidoText)
        val emailText = findViewById<EditText>(R.id.emailText)
        val usernameText = findViewById<EditText>(R.id.usernameText)
        val passwordText = findViewById<EditText>(R.id.passwordText)
        val repetePasswordText = findViewById<EditText>(R.id.repetePasswordText)
        val registarBtn = findViewById<Button>(R.id.registarBtn)


        // Evento de Mouse Click no botão Registar
        registarBtn.setOnClickListener{
            // Guarda os valores em texto dos elementos da View
            val nome = nomeText.text.toString()
            val apelido = apelidoText.text.toString()
            val email = emailText.text.toString()
            val username = usernameText.text.toString()
            val password = passwordText.text.toString()
            val repetePassword = repetePasswordText.text.toString()

            // Verifica se todas as EditText estão preenchidas
            if (nome.isNotEmpty() && apelido.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()
                && repetePassword.isNotEmpty()) {
                // Verifica se o email contém '@' e '.'
                if (email.contains('@') and email.contains('.')){
                    // Verificar se as passwords coicidem
                    if(password.equals(repetePassword)){
                        //Criação do objeto a enviar para Registar um utilizador
                        val registerRequest = RegisterRequest(
                            email = email,
                            username = username,
                            password = password,
                            role = 3, // 3 default para Aluno
                            name = nome,
                            surname = apelido
                        )
                        // Chamada da função que comunica com a API, para registar um utilizador
                        registerUser(registerRequest){response ->
                            if (response!=null){
                                if (response?.code() == 201) {
                                    // Registo bem sucedido
                                    Toast.makeText(this, "Utilizador registado com sucesso.", Toast.LENGTH_LONG).show()
                                    val intent = Intent(this, LoginActivity::class.java)
                                    startActivity(intent)
                                }else if (response?.code() == 409){
                                    // Utilizador já existe no sistema (username ou email)
                                    Toast.makeText(this, "O email ou username inseridos já se encontram registados.", Toast.LENGTH_LONG).show()
                                }else if (response?.code() == 200){
                                    // Existe algum erro que não permitiu o registo (falta de informação)
                                    Toast.makeText(this, response.body()?.message, Toast.LENGTH_LONG).show()
                                }else{
                                    // Erro não identificado / Falha no servidor
                                    Toast.makeText(this, "Erro. Contacte o Administrador", Toast.LENGTH_SHORT).show()
                                }
                            }else{
                                // Erro não identificado / Falha no servidor
                                Toast.makeText(this, "Erro. Contacte o Administrador", Toast.LENGTH_SHORT).show()
                            }

                        }
                    }else{
                        Toast.makeText(this, "As password's não coicidem.", Toast.LENGTH_LONG).show()
                    }
                }else{
                    Toast.makeText(this, "Email inválido.", Toast.LENGTH_LONG).show()

                }
            }else{
                Toast.makeText(this, "Preencha todos os campos.", Toast.LENGTH_LONG).show()
            }
        }
    }

    /**
     * Função para registar um novo Utilizador (User/Person)
     */
    private fun registerUser(user: RegisterRequest, onResult: (Response<ResponseAPI>?) -> Unit) {
        // Faz a chamada a API
        val call = RetrofitInitializer().APIService().register(user)

        call.enqueue(object : Callback<ResponseAPI> {
            // Tratamento de falha da chamada à API
            override fun onFailure(call: Call<ResponseAPI>, t: Throwable) {
                t?.message?.let { Log.e("onFailure error", it) }
                onResult(null)
            }

            // Tratamento da resposta bem-sucedida da chamada à API
            override fun onResponse(call: Call<ResponseAPI>, response: Response<ResponseAPI>) {
                    onResult(response) // Chama a função de retorno com a resposta da API
            }
        })
    }

}