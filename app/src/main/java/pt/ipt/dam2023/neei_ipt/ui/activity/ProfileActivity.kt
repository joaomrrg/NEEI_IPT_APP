package pt.ipt.dam2023.neei_ipt.ui.activity

import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import pt.ipt.dam2023.neei_ipt.R
import pt.ipt.dam2023.neei_ipt.model.User
import pt.ipt.dam2023.neei_ipt.retrofit.RetrofitInitializer
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.Scanner
import androidx.core.content.ContextCompat


class ProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile) // Define o layout da atividade como activity_profile.xml

        setSpinner() // Configura o Spinner com opções de gênero
        setDatePicker() // Configura o DatePicker para a data de nascimento

        // Ponteiros para os elementos do layout
        val usernameText = findViewById<EditText>(R.id.editTextUsername)
        val emailText = findViewById<EditText>(R.id.editTextEmail)
        val nameText = findViewById<EditText>(R.id.editTextName)
        val surnameText = findViewById<EditText>(R.id.editTextSurname)
        val birthDateText = findViewById<EditText>(R.id.editTextBirthDate)
        val genderText = findViewById<Spinner>(R.id.spinnerGender)
        val linkedinText = findViewById<EditText>(R.id.editTextLinkedIn)
        val githubText = findViewById<EditText>(R.id.editTextGithub)
        val numAlunoText = findViewById<EditText>(R.id.editTextNumAluno)
        val image = findViewById<ImageView>(R.id.imageViewProfile)

        // Lista de IDs de elementos a serem editados
        val elementos = listOf(
            R.id.editTextUsername,
            R.id.editTextEmail,
            R.id.editTextName,
            R.id.editTextSurname,
            R.id.editTextBirthDate,
            R.id.editTextLinkedIn,
            R.id.editTextGithub,
            R.id.editTextNumAluno
        )

        val azul = ContextCompat.getColor(this, R.color.blue)
        val night = ContextCompat.getColor(this, R.color.backgroundnight)

        // Botões de Editar e Confirmar
        val buttEditar = findViewById<Button>(R.id.btnEdit)
        val buttConfirmar = findViewById<Button>(R.id.btnConfirm)
        buttConfirmar.setBackgroundColor(night)

        // Ação ao clicar no botão Editar
        buttEditar.setOnClickListener{
            elementos.forEach { viewId ->
                val textView = findViewById<EditText>(viewId)
                textView.setHintTextColor(Color.WHITE)
                textView.setTextColor(Color.WHITE)
                textView.isEnabled = true
                buttConfirmar.isEnabled = true
                buttConfirmar.setBackgroundColor(azul)
                buttEditar.isEnabled = false
                buttEditar.setBackgroundColor(night)
            }
        }

        // Ação ao clicar no botão Confirmar
        buttConfirmar.setOnClickListener{
            elementos.forEach { viewId ->
                val textView = findViewById<EditText>(viewId)
                textView.setHintTextColor(Color.BLACK)
                textView.setTextColor(Color.BLACK)
                textView.isEnabled = false
                buttConfirmar.isEnabled = false
                buttConfirmar.setBackgroundColor(night)
                buttEditar.isEnabled = true
                buttEditar.setBackgroundColor(azul)
            }
        }

        var userId = -1 // ID do usuário logado
        var imagePath= "" // Caminho da imagem do perfil

        // Leitura da Internal Storage para obter dados do usuário de um arquivo dados.txt
        val directory: File = filesDir
        val file: File = File(directory, "dados.txt")
        try {
            val fi: FileInputStream = FileInputStream(file)
            val sc: Scanner = Scanner(fi)
            sc.nextLine()
            sc.nextLine()
            sc.nextLine()
            sc.nextLine()
            sc.nextLine()
            imagePath=sc.nextLine()
            userId = sc.nextLine().toInt() // ID do usuário armazenado
            sc.close()
            fi.close()
        } catch (e: FileNotFoundException) {
            Toast.makeText(this, "Arquivo não encontrado", Toast.LENGTH_LONG).show()
        }

        // Chamada e tratamento do resultado da API para obter informações do usuário com base no userId
        getUser(userId) { result ->
            if (result != null) {
                // Carregar todas as informações para os elementos da interface
                usernameText.setText(result.username)
                emailText.setText(result.email)
                nameText.setText(result.person.name)
                surnameText.setText(result.person.surname)
                val birthDate = result.person.birthDate
                if (birthDate != null) {
                    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    val formattedDate = dateFormat.format(birthDate)
                    birthDateText.setText(formattedDate)
                }
                if(result.person.gender==null){
                    genderText.setSelection(2)
                }else if(result.person.gender=="M"){
                    genderText.setSelection(0)
                }else{
                    genderText.setSelection(1)
                }
                linkedinText.setText(result.person.linkedIn)
                githubText.setText(result.person.github)
                numAlunoText.setText(result.person.numAluno)
                Glide.with(this)
                    .load(imagePath)
                    .into(image) // Carregar a imagem de perfil usando Glide
            }
        }
    }

    // Configura o Spinner com opções de gênero
    private fun setSpinner(){
        val spinner = findViewById<Spinner>(R.id.spinnerGender)
        val genderOptions = arrayOf("Masculino", "Feminino", "Outro")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, genderOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }

    // Configura o DatePicker para a data de nascimento
    private fun setDatePicker(){
        val editTextBirthDate = findViewById<EditText>(R.id.editTextBirthDate);
        editTextBirthDate.setOnClickListener {
            val calendar: Calendar = Calendar.getInstance()
            val year: Int = calendar.get(Calendar.YEAR)
            val month: Int = calendar.get(Calendar.MONTH)
            val day: Int = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    val selectedDate = dayOfMonth.toString() + "/" + (month + 1) + "/" + year
                    editTextBirthDate.setText(selectedDate)
                },
                year, month, day
            )
            datePickerDialog.show()
        }
    }

    // Função para obter informações do usuário da API com base no ID
    private fun getUser(id: Int?, onResult: (User?) -> Unit) {
        if (id != null) {
            val call = RetrofitInitializer().APIService().getUserById(id)
            call.enqueue(object : Callback<User?> {
                override fun onResponse(call: Call<User?>?, response: Response<User?>?) {
                    response?.body()?.let {
                        val user: User = it
                        onResult(user)
                    }
                }

                override fun onFailure(call: Call<User?>, t: Throwable) {
                    Log.e("Erro", t.message ?: "Erro na chamada da API")
                }
            })
        } else {
            // Lidar com o caso em que o ID é nulo
        }
    }
}
