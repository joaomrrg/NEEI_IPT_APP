package pt.ipt.dam2023.neei_ipt.ui.activity

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import pt.ipt.dam2023.neei_ipt.R
import pt.ipt.dam2023.neei_ipt.model.ResponseAPI
import pt.ipt.dam2023.neei_ipt.model.UpdatePersonRequest
import pt.ipt.dam2023.neei_ipt.model.User
import pt.ipt.dam2023.neei_ipt.retrofit.RetrofitInitializer
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.PrintStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.Scanner


class ProfileActivity : AppCompatActivity(){
    val TAKE_PHOTO = 2
    var roleId = -1
    var roleDescription = ""
    var userId = -1
    private lateinit var displayName: String
    private lateinit var imageFile: File
    private lateinit var usernamePub: String
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>
    companion object {
        const val PERMISSION_REQUEST_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile) // Define o layout da atividade como activity_profile.xml

        setSpinner() // Configura o Spinner com opções de género
        setDatePicker() // Configura o DatePicker para a data de nascimento
        registerResult()
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
        buttEditar.setOnClickListener {
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
        buttConfirmar.setOnClickListener {
            var genderLow = genderText.selectedItem.toString()
            genderLow = if (genderLow == "Masculino") {
                "M"
            } else if (genderLow == "Feminino") {
                "F"
            } else {
                "O"
            }
            val personReq = UpdatePersonRequest(
                username = usernameText.text.toString(),
                name = nameText.text.toString(),
                surname = surnameText.text.toString(),
                birthDate = birthDateText.text.toString(),
                linkedIn = linkedinText.text.toString(),
                github = githubText.text.toString(),
                gender = genderLow,
                image = displayName,
                numAluno = numAlunoText.text.toString()
            )
            usernamePub = usernameText.text.toString()
            // Chamada da função que comunica com a API, para registar um utilizador
            if (imageFile.toString().contains(displayName)) {
                updateProfile(personReq) { result ->
                    if (result?.code() == 201) {
                        // Registo bem sucedido
                        Toast.makeText(applicationContext, "Perfil atualizado com sucesso.", Toast.LENGTH_LONG).show()
                        elementos.forEach { viewId ->
                            val textView = findViewById<EditText>(viewId)
                            textView.setHintTextColor(Color.GRAY)
                            textView.setTextColor(Color.GRAY)
                            textView.isEnabled = false
                            buttConfirmar.isEnabled = false
                            buttConfirmar.setBackgroundColor(night)
                            buttEditar.isEnabled = true
                            buttEditar.setBackgroundColor(azul)
                        }
                    }else if (result?.code() == 200) {
                        // Existe algum erro que não permitiu o registo (falta de informação)
                        Toast.makeText(applicationContext, result.body()?.message, Toast.LENGTH_LONG).show()
                    }else {
                        // Erro não identificado / Falha no servidor
                        Toast.makeText(this, "Erro. Contacte o Administrador", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            } else {
                uploadImage(imageFile) { statusCode ->
                    if (statusCode == 201) {
                        personReq.image = displayName
                        updateProfile(personReq) { result ->
                            if (result?.code() == 201) {
                                // Registo bem sucedido
                                Toast.makeText(
                                    applicationContext,
                                    "Perfil atualizado com sucesso.",
                                    Toast.LENGTH_LONG
                                ).show()
                                elementos.forEach { viewId ->
                                    val textView = findViewById<EditText>(viewId)
                                    textView.setHintTextColor(Color.GRAY)
                                    textView.setTextColor(Color.GRAY)
                                    textView.isEnabled = false
                                    buttConfirmar.isEnabled = false
                                    buttConfirmar.setBackgroundColor(night)
                                    buttEditar.isEnabled = true
                                    buttEditar.setBackgroundColor(azul)
                                }
                            }else if (result?.code() == 200) {
                                // Existe algum erro que não permitiu o registo (falta de informação)
                                Toast.makeText(
                                    applicationContext,
                                    result.body()?.message,
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                // Erro não identificado / Falha no servidor
                                Toast.makeText(
                                    applicationContext,
                                    "Erro. Contacte o Administrador",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    } else {
                        // Erro não identificado / Falha no servidor
                        Toast.makeText(this, "Erro. Contacte o Administrador", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
            val directory: File = getFilesDir()
            val file: File = File(directory, "dados.txt")
            val fo: FileOutputStream = FileOutputStream(file)
            val ps: PrintStream = PrintStream(fo)
            ps.println(usernameText.text.toString())
            ps.println(nameText.text.toString())
            ps.println(surnameText.text.toString())
            ps.println(roleId)
            ps.println(roleDescription)
            ps.println(displayName)
            ps.println(userId)
            ps.close()
            fo.close()
            Log.i("Internal Storage","Dados inseridos com sucesso")
        }
        var imagePath = "" // Caminho da imagem do perfil

        // Leitura da Internal Storage
        val directory: File = filesDir
        val file: File = File(directory, "dados.txt")
        try {
            val fi: FileInputStream = FileInputStream(file)
            val sc: Scanner = Scanner(fi)
            sc.nextLine()
            sc.nextLine()
            sc.nextLine()
            roleId = sc.nextLine().toInt()
            roleDescription = sc.nextLine()
            imagePath = sc.nextLine()
            userId = sc.nextLine().toInt() // ID do usuário armazenado
            sc.close()
            fi.close()
        } catch (e: FileNotFoundException) {
            Toast.makeText(this, "Arquivo não encontrado", Toast.LENGTH_LONG).show()
        }

        // Chamada e tratamento do resultado da API para obter informações do utilizador com base no userId
        getUser(userId) { result ->
            if (result != null) {
                // Carregar todas as informações para os elementos da interface
                usernameText.setText(result!!.username)
                usernameText.setTextColor(Color.GRAY)
                displayName = result.username.toString()
                emailText.setText(result.email)
                emailText.setTextColor(Color.GRAY)
                nameText.setText(result.person!!.name)
                nameText.setTextColor(Color.GRAY)
                surnameText.setText(result.person.surname)
                surnameText.setTextColor(Color.GRAY)
                val birthDate = result.person.birthDate
                if (birthDate != null) {
                    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    val formattedDate = dateFormat.format(birthDate)
                    birthDateText.setText(formattedDate)
                }
                if (result.person.gender == null) {
                    genderText.setSelection(2)
                } else if (result.person.gender == "M") {
                    genderText.setSelection(0)
                } else {
                    genderText.setSelection(1)
                }
                linkedinText.setText(result.person.linkedIn)
                githubText.setText(result.person.github)
                numAlunoText.setText(result.person.numAluno)
                val imageUrl = "https://neei.eu.pythonanywhere.com/images/$imagePath"
                imageFile = File(imageUrl)
                displayName = imagePath
                Glide.with(this)
                    .load(imageUrl)
                    .apply(RequestOptions.bitmapTransform(CircleCrop()))
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(image)
            }
        }

        image.setOnClickListener {
            if (hasCameraPermission()) {
                val options = arrayOf("Escolher da Galeria", "Tirar Foto")
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Escolher uma opção")
                builder.setItems(options) { dialog, which ->
                    when (which) {
                        0 -> openGallery()
                        1 -> openCamera()
                    }
                }
                builder.show()
            } else {
                requestCameraPermission()
            }
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "image/*"
        resultLauncher.launch(intent)
    }

    private fun registerResult(){
        resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            // Handle the result here
            if (result.resultCode == Activity.RESULT_OK) {
                // Handle success
                val data: Intent? = result.data
                // Foto escolhida da galeria
                val selectedImage = data?.data
                if (selectedImage != null) {
                    imageFile = getFileFromUri(selectedImage)
                    // Obter o nome do arquivo selecionado
                    displayName = getFileName(selectedImage)
                    val image = findViewById<ImageView>(R.id.imageViewProfile)
                    if (Glide.with(this) != null) {
                        Glide.with(this)
                            .load(selectedImage)
                            .apply(RequestOptions.bitmapTransform(CircleCrop()))
                            .into(image)
                    }
                }
            }
        }
    }
    private fun openCamera() {
        val takePhotoIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(takePhotoIntent, TAKE_PHOTO)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            when (requestCode) {
                TAKE_PHOTO -> {
                    val photo = data?.extras?.get("data") as Bitmap

                    // Salvar a foto num ficheiro temporário
                    val tempFile = createTempFile("temp", null, cacheDir)
                    tempFile.outputStream().use { output ->
                        photo.compress(Bitmap.CompressFormat.PNG, 100, output)
                    }

                    // Usar o ficheiro temporário para exibir a imagem na imageView
                    val image = findViewById<ImageView>(R.id.imageViewProfile)
                    Glide.with(this)
                        .load(tempFile)
                        .apply(RequestOptions.bitmapTransform(CircleCrop()))
                        .into(image)

                    // Configurar a variável imageFile para o ficheiro temporário
                    imageFile = tempFile
                }
            }
        }
    }


    // Configura o Spinner com opções de género
    private fun setSpinner() {
        val spinner = findViewById<Spinner>(R.id.spinnerGender)
        val genderOptions = arrayOf("Masculino", "Feminino", "Outro")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, genderOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }

    // Configura o DatePicker para a data de nascimento
    private fun setDatePicker() {
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

    // Função para obter informações do utilizador da API com base no ID
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
        }
    }

    //Função para dar upload a uma imagem
    private fun uploadImage(imageFile: File, onResult: (Int) -> Unit) {
        displayName = "$usernamePub.png"
        // Criar uma instância de MultipartBody.Part para o ficheiro
        val requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), imageFile)
        val filePart = MultipartBody.Part.createFormData("image", displayName, requestFile)
        // Faz a chamada a API
        val call = RetrofitInitializer().APIService().uploadImage(filePart)

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

    // Função para atualizar um utilizador na bd
    private fun updateProfile(person: UpdatePersonRequest, onResult: (Response<ResponseAPI>?) -> Unit) {
        // Faz a chamada a API
        val call = RetrofitInitializer().APIService().updateProfile(person)

        call.enqueue(object : Callback<ResponseAPI> {
            override fun onFailure(call: Call<ResponseAPI>, t: Throwable) {
                t?.message?.let { Log.e("onFailure error", it) }
                onResult(null)
            }

            // Retorna o StatusCode da resposta
            override fun onResponse(call: Call<ResponseAPI>, response: Response<ResponseAPI>) {
                onResult(response)
            }
        })
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val options = arrayOf("Escolher da Galeria", "Tirar Foto")
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Escolher uma opção")
                builder.setItems(options) { dialog, which ->
                    when (which) {
                        0 -> openGallery()
                        1 -> openCamera()
                    }
                }
                builder.show()
            } else {
            }
        }
    }

    // Função para receber um ficheiro
    private fun getFileFromUri(uri: Uri): File {
        val inputStream = contentResolver.openInputStream(uri)
        if (inputStream != null) {
            // Criar um arquivo temporário para armazenar os dados do InputStream
            val tempFile = createTempFile("temp", null, cacheDir)
            tempFile.outputStream().use { output ->
                inputStream.copyTo(output)
            }
            return tempFile
        } else {
            throw FileNotFoundException("Não foi possível obter o ficheiro a partir da Uri.")
        }
    }

    private fun getFileName(uri: Uri): String {
        val cursor = contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (it.moveToFirst()) {
                return it.getString(nameIndex)
            }
        }
        return ""
    }

    private fun hasCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.CAMERA),
            PERMISSION_REQUEST_CODE
        )
    }

}
