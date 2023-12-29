package pt.ipt.dam2023.neei_ipt.ui.activity

import android.Manifest
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AlertDialog
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
import androidx.core.net.toUri
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.vmadalin.easypermissions.EasyPermissions
import com.vmadalin.easypermissions.dialogs.SettingsDialog
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import pt.ipt.dam2023.neei_ipt.model.DocumentRequest
import pt.ipt.dam2023.neei_ipt.model.updatePersonRequest
import java.io.FileOutputStream
import java.io.PrintStream
import java.net.URI


class ProfileActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks {
    val PICK_IMAGE = 1
    val TAKE_PHOTO = 2
    var roleId = -1
    var roleDescription = ""
    var userId = -1
    private lateinit var displayName: String
    private lateinit var imageFile: File
    private lateinit var usernamePub: String


    companion object {
        const val PERMISSION_REQUEST_CODE = 1
    }

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
            val personReq = updatePersonRequest(
                username = usernameText.text.toString(),
                name = nameText.text.toString(),
                surname = surnameText.text.toString(),
                birthDate = birthDateText.text.toString(),
                linkedIn = linkedinText.text.toString(),
                github = githubText.text.toString(),
                gender = genderText.selectedItem.toString(),
                image = displayName,
                numAluno = numAlunoText.text.toString()
            )
            usernamePub = usernameText.text.toString()
            // Chamada da função que comunica com a API, para registar um utilizador
            if (imageFile.toString().contains(displayName)) {
                updateProfile(personReq) { statusCode ->
                    if (statusCode == 200) {
                        // Registo bem sucedido
                        Toast.makeText(this, "Perfil atualizado com sucesso.", Toast.LENGTH_LONG)
                            .show()
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
                    } else {
                        // Erro não identificado / Falha no servidor
                        Toast.makeText(this, "Erro. Contacte o Administrador", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            } else {
                uploadImage(imageFile) { statusCode ->
                    if (statusCode == 201) {
                        personReq.image = displayName
                        updateProfile(personReq) { statusCode ->
                            if (statusCode == 200) {
                                // Registo bem sucedido
                                Toast.makeText(
                                    this,
                                    "Perfil atualizado com sucesso.",
                                    Toast.LENGTH_LONG
                                ).show()
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
                            } else {
                                // Erro não identificado / Falha no servidor
                                Toast.makeText(
                                    this,
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

        // Leitura da Internal Storage para obter dados do usuário de um arquivo dados.txt
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

        // Chamada e tratamento do resultado da API para obter informações do usuário com base no userId
        getUser(userId) { result ->
            if (result != null) {
                // Carregar todas as informações para os elementos da interface
                usernameText.setText(result.username)
                displayName = result.username
                emailText.setText(result.email)
                nameText.setText(result.person.name)
                surnameText.setText(result.person.surname)
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
                Glide.with(this)
                    .load("https://neei.eu.pythonanywhere.com/images/$imagePath")
                    .apply(RequestOptions.bitmapTransform(CircleCrop()))
                    .into(image)
                val imageUrl = "https://neei.eu.pythonanywhere.com/images/$imagePath"
                imageFile = File(imageUrl)
                displayName = imagePath
            }
        }

        image.setOnClickListener {
            if (hasPermissions()) {
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
                requestPermissions()
            }

        }
    }

    private fun openGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, PICK_IMAGE)
    }

    private fun openCamera() {
        val takePhotoIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(takePhotoIntent, TAKE_PHOTO)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            when (requestCode) {
                PICK_IMAGE -> {
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
                        } else {
                            // Lógica de tratamento se Glide for nulo
                        }
                    } else {
                        // Lógica de tratamento se selectedImage for nulo
                    }
                }

                TAKE_PHOTO -> {
                    val photo = data?.extras?.get("data") as Bitmap

                    // Salvar a foto em um arquivo temporário
                    val tempFile = createTempFile("temp", null, cacheDir)
                    tempFile.outputStream().use { output ->
                        photo.compress(Bitmap.CompressFormat.PNG, 100, output)
                    }

                    // Usar o arquivo temporário para exibir a imagem na imageView
                    val image = findViewById<ImageView>(R.id.imageViewProfile)
                    Glide.with(this)
                        .load(tempFile)
                        .apply(RequestOptions.bitmapTransform(CircleCrop()))
                        .into(image)

                    // Configurar a variável imageFile para o arquivo temporário
                    imageFile = tempFile
                }
            }
        }
    }


    // Configura o Spinner com opções de gênero
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
        } else {
            // Lidar com o caso em que o ID é nulo
        }
    }

    //Função para dar upload a uma imagem
    private fun uploadImage(imageFile: File, onResult: (Int) -> Unit) {
        displayName = "$usernamePub.png"
        // Criar uma instância de MultipartBody.Part para o arquivo
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
    private fun updateProfile(person: updatePersonRequest, onResult: (Int) -> Unit) {
        // Faz a chamada a API
        val call = RetrofitInitializer().APIService().updateProfile(person)

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


    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            SettingsDialog.Builder(this).build().show()
        } else {
            requestPermissions()
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        showToast("Permissões garantidas com sucesso")

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    private fun hasPermissions() =
        EasyPermissions.hasPermissions(
            this,
            Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.CAMERA
        )


    private fun requestPermissions() {
        EasyPermissions.requestPermissions(
            this,
            "Esta permissão é requerida para fazer download",
            PERMISSION_REQUEST_CODE,
            Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.CAMERA
        )
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
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

}
