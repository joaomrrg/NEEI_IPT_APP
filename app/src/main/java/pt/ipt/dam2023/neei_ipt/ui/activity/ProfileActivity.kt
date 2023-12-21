package pt.ipt.dam2023.neei_ipt.ui.activity

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import pt.ipt.dam2023.neei_ipt.R
import pt.ipt.dam2023.neei_ipt.model.AuthResponse
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


class ProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        setSpinner()
        setDatePicker()

        //Ponteiros para os elementos do layout
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



        // Variavel com id do utilizador logado
        var userId = -1
        var imagePath= ""
        // Leitura da Internal Storage
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
            // Guarda na variavel o id do user
            userId = sc.nextLine().toInt()
            sc.close()
            fi.close()
        } catch (e: FileNotFoundException) {
            Toast.makeText(this, "File not found", Toast.LENGTH_LONG).show()
        }
        // chamada e tratamento do resultado da api
        getUser(userId) { result ->
            if (result != null) {
                //Carregar todas as informações para os elementos
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
                    .into(image)
            }
        }
    }

    /**
     * Coloca itens no Spinner
     */
    private fun setSpinner(){
        val spinner = findViewById<Spinner>(R.id.spinnerGender)
        // Crie um array de strings para os itens na lista
        val genderOptions = arrayOf("Masculino", "Feminino", "Outro")
        // Crie um ArrayAdapter usando o array e um layout predefinido para os itens
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, genderOptions)
        // Especifique o layout para o menu suspenso
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // Associe o ArrayAdapter ao Spinner
        spinner.adapter = adapter
    }

    /**
     * Torna o editText da Data clicável e a aparecer em Calendário
     */
    private fun setDatePicker(){
        val editTextBirthDate = findViewById<EditText>(R.id.editTextBirthDate);
        editTextBirthDate.setOnClickListener {
            // Get current date
            val calendar: Calendar = Calendar.getInstance()
            val year: Int = calendar.get(Calendar.YEAR)
            val month: Int = calendar.get(Calendar.MONTH)
            val day: Int = calendar.get(Calendar.DAY_OF_MONTH)

            // Create a DatePickerDialog
            val datePickerDialog = DatePickerDialog(
                this,
                { _, year, month, dayOfMonth -> // Handle the selected date
                    val selectedDate = dayOfMonth.toString() + "/" + (month + 1) + "/" + year
                    editTextBirthDate.setText(selectedDate)
                },
                year, month, day
            )
            // Show the DatePickerDialog
            datePickerDialog.show()
        }
    }

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
                    t.message?.let { Log.e("onFailure error", it) }
                }
            })
        } else {
            // Handle the case when id is null
        }
    }

}