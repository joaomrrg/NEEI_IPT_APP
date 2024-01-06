package pt.ipt.dam2023.neei_ipt.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import pt.ipt.dam2023.neei_ipt.R
import pt.ipt.dam2023.neei_ipt.model.RegisterRequest
import pt.ipt.dam2023.neei_ipt.retrofit.RetrofitInitializer
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminRegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_register)

        // Ponteiros de elementos da View
        val nomeText = findViewById<EditText>(R.id.nomeText2)
        val apelidoText = findViewById<EditText>(R.id.apelidoText2)
        val emailText = findViewById<EditText>(R.id.emailText2)
        val usernameText = findViewById<EditText>(R.id.usernameText2)
        val passwordText = findViewById<EditText>(R.id.passwordText2)
        val repetePasswordText = findViewById<EditText>(R.id.repetePasswordText2)
        val registarBtn = findViewById<Button>(R.id.registarBtn2)
        val rolesSpinner = findViewById<Spinner>(R.id.roleSpinner2)

        val roles = arrayOf("Administrador", "Membro", "Aluno", "Alumni", "Convidado")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, roles)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        rolesSpinner.adapter = adapter
        rolesSpinner.setSelection(2)


        // Evento de Mouse Click no botão Registar
        registarBtn.setOnClickListener{
            // Guarda os valores em texto dos elementos da View
            val nome = nomeText.text.toString()
            val apelido = apelidoText.text.toString()
            val email = emailText.text.toString()
            val username = usernameText.text.toString()
            val password = passwordText.text.toString()
            val repetePassword = repetePasswordText.text.toString()
            val role = rolesSpinner.selectedItemPosition+1

            // Verifica se todas as EditText estão preenchidas
            if (nome.isNotEmpty() && apelido.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()
                && repetePassword.isNotEmpty()) {
                // Verificar se as passwords coicidem
                if(password.equals(repetePassword)){
                    //Criação do objeto a enviar para Registar um utilizador
                    val registerRequest = RegisterRequest(
                        email = email,
                        username = username,
                        password = password,
                        role = role,
                        name = nome,
                        surname = apelido
                    )
                    // Chamada da função que comunica com a API, para registar um utilizador
                    registerUser(registerRequest){statusCode ->
                        if (statusCode == 201) {
                            // Registo bem sucedido
                            Toast.makeText(this, "Utilizador registado com sucesso.", Toast.LENGTH_LONG).show()
                            val intent = Intent(this, MainActivity::class.java)
                            // Adicionando um extra chamado "fragment_to_show" com o valor "DocumentFragment" ao Intent
                            intent.putExtra("fragment_to_show", "UserFragment")
                            startActivity(intent)
                        }else if (statusCode == 409){
                            // Utilizador já existe no sistema (username ou email)
                            Toast.makeText(this, "O email ou username inseridos já se encontram registados.", Toast.LENGTH_LONG).show()
                        }else{
                            // Erro não identificado / Falha no servidor
                            Toast.makeText(this, "Erro. Contacte o Administrador", Toast.LENGTH_SHORT).show()
                        }
                    }
                }else{
                    Toast.makeText(this, "As password's não coicidem.", Toast.LENGTH_LONG).show()
                }
            }else{
                Toast.makeText(this, "Preencha todos os campos.", Toast.LENGTH_LONG).show()
            }
        }
    }



    // Função para registar um novo Utilizador (User/Person)
    private fun registerUser(user: RegisterRequest, onResult: (Int) -> Unit) {
        // Faz a chamada a API
        val call = RetrofitInitializer().APIService().register(user)

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