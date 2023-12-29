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
            // Recebe o valor da chamada da API para Autenticar um User
            val user = AuthRequest(usernameText.text.toString(), passwordText.text.toString())
            authUser(user){ result ->
                if (result != null) {
                    // Utilizador autenticado com sucesso
                    if (result.code == 200) {
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
                    // Utilizador não encontrado
                    } else if (result.code == 404){
                        Toast.makeText(this, "Utilizador não encontrado", Toast.LENGTH_SHORT).show()
                    // Utilizador sem autorização (maioritariamente por ter a password errada)
                    }else if(result.code == 401){
                        Toast.makeText(this, "Utilizado sem autorização", Toast.LENGTH_SHORT).show()
                    }
                }else{
                    // Erro não identificado / Falha no servidor
                    Toast.makeText(this, "Erro. Contacte o Administrador", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

    /**
     * Função para listar todos os utilizadores do sistema
     */
    private fun getUsers(){
        val call = RetrofitInitializer().APIService().listUsers()
        call.enqueue(object : Callback<List<User>?> {
            override fun onResponse(call: Call<List<User>?>?,
                                    response: Response<List<User>?>?) {
                response?.body()?.let {
                    val users: List<User> = it
                    println(users)
                }
            }

            override fun onFailure(call: Call<List<User>?>, t: Throwable) {
                t?.message?.let { Log.e("onFailure error", it) }
            }
        })
    }

    /**
     * Função para autenticar um utilizador
     */
    private fun authUser(user: AuthRequest, onResult: (AuthResponse?) -> Unit){
        // Faz a chamada a API
        val call = RetrofitInitializer().APIService().authenticate(user)
        call.enqueue(
            object : Callback<AuthResponse> {
                // Escreve uma log sobre o erro
                override fun onFailure(call: Call<AuthResponse>?, t: Throwable) {
                    t?.message?.let { Log.e("onFailure error", it) }
                    onResult(null)
                }
                // Recebe a response
                override fun onResponse( call: Call<AuthResponse>, response: Response<AuthResponse>) {
                    // Guarda o resultado json
                    var result = response.body()
                    // Verificar os resultados
                    if (result == null){
                        result = AuthResponse(null,null,null,null,null,null,response.code(),null)
                    }else{
                        result.code = response.code()
                    }
                    onResult(result)
                }
            }
        )
    }

}
