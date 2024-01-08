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
import pt.ipt.dam2023.neei_ipt.model.AuthRequest
import pt.ipt.dam2023.neei_ipt.model.AuthResponse
import pt.ipt.dam2023.neei_ipt.model.User
import pt.ipt.dam2023.neei_ipt.retrofit.RetrofitInitializer
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.PrintStream

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Ponteiros de elementos da View
        val registarLabel = findViewById<TextView>(R.id.registar_label)
        val recuperarLabel = findViewById<TextView>(R.id.recuperar_label)
        val loginButton = findViewById<Button>(R.id.button_login)
        val usernameText = findViewById<EditText>(R.id.usernameText)
        val passwordText = findViewById<EditText>(R.id.passwordText)

        // Evento de Mouse click na label Registar
        registarLabel.setOnClickListener{
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        // Evento de Mouse Click na label Recuperar Password
        recuperarLabel.setOnClickListener{
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }

        // Evento de Mouse Click no botão de Login
        loginButton.setOnClickListener{
            // Verifica se a password contem a palavra recover
            if (passwordText.text.toString().contains("recover:")){
                // Se conntem, cria e inicia a Intent para alterar a password
                val intent = Intent(this, ChangePasswordActivity::class.java)
                // Passa por parametro o username
                intent.putExtra("username",usernameText.text.toString())
                startActivity(intent)
            }else{
                // Criação do objeto a enviar para autenticar um utilizador
                val user = AuthRequest(usernameText.text.toString(), passwordText.text.toString())
                // Chamada da função que comunica com a API, para registar um utilizador
                authUser(user){ result ->
                    if (result != null) {
                        if (result.code == 200) {
                            // Utilizador autenticado com sucesso

                            // Guarda informações no Internal Storage
                            val directory: File = getFilesDir()
                            val file: File = File(directory, "dados.txt")
                            val fo: FileOutputStream = FileOutputStream(file)
                            val ps: PrintStream = PrintStream(fo)
                            ps.println(user.username)
                            ps.println(result.name)
                            ps.println(result.surname)
                            ps.println(result.role)
                            ps.println(result.roleDescription)
                            ps.println(result.image)
                            ps.println(result.id)
                            ps.close()
                            fo.close()
                            Log.i("Internal Storage","Dados inseridos com sucesso")

                            Toast.makeText(this, "Bem-vindo, ${result.name} ${result.surname}", Toast.LENGTH_LONG).show()
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                        } else if (result.code == 404){
                            // Utilizador não encontrado
                            Toast.makeText(this, "Utilizador não encontrado", Toast.LENGTH_SHORT).show()
                        }else if(result.code == 401){
                            // Utilizador sem autorização (maioritariamente por ter a password errada)
                            Toast.makeText(this, "Utilizado sem autorização", Toast.LENGTH_SHORT).show()
                        }
                    }else{
                        // Erro não identificado / Falha no servidor
                        Toast.makeText(this, "Erro. Contacte o Administrador", Toast.LENGTH_SHORT).show()
                    }
                }
            }

        }

    }


    /**
     * Função para autenticar um utilizador
     */
    private fun authUser(user: AuthRequest, onResult: (AuthResponse?) -> Unit){
        // Faz a chamada a API
        val call = RetrofitInitializer().APIService().authenticate(user)
        call.enqueue(
            object : Callback<AuthResponse> {
                // Tratamento de falha da chamada à API
                override fun onFailure(call: Call<AuthResponse>?, t: Throwable) {
                    t?.message?.let { Log.e("onFailure error", it) }
                    onResult(null)
                }
                // Tratamento da resposta bem-sucedida da chamada à API
                override fun onResponse( call: Call<AuthResponse>, response: Response<AuthResponse>) {
                    // Guarda o resultado json
                    var result = response.body()
                    // Verificar os resultados
                    if (result == null){
                        result = AuthResponse(null,null,null,null,null,null,response.code(),null)
                    }else{
                        result.code = response.code()
                    }
                    onResult(result) // Chama a função de retorno com a o statusCode da resposta da API
                }
            }
        )
    }

}
