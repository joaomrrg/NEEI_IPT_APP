package pt.ipt.dam2023.neei_ipt.ui.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import pt.ipt.dam2023.neei_ipt.R
import pt.ipt.dam2023.neei_ipt.model.DocumentRequest
import pt.ipt.dam2023.neei_ipt.retrofit.RetrofitInitializer
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileNotFoundException

class DocumentAddActivity : AppCompatActivity() {

    private val PICK_FILE_REQUEST_CODE = 123
    private lateinit var fileNameText: TextView
    private lateinit var selectedFileUri: Uri
    private lateinit var file: File
    private lateinit var displayName: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_document_add)
        // Inicialização dos elementos de interface
        val title = findViewById<EditText>(R.id.titleText) // Campo de título
        val description = findViewById<EditText>(R.id.descriptionText) // Campo de descrição
        val dateEditText = findViewById<EditText>(R.id.dateText) // Campo de data
        val button_document = findViewById<Button>(R.id.addDocumentButton) // Botão para adicionar documento
        val button_create = findViewById<Button>(R.id.createButton) // Botão para criar

        // Referência ao TextView onde será exibido o nome do arquivo selecionado
        fileNameText = findViewById<TextView>(R.id.fileNameText)

        // Definição do comportamento ao clicar no botão para adicionar documento
        button_document.setOnClickListener {
            openFilePicker()
        }

        // Definição do comportamento ao clicar no botão para criar
        button_create.setOnClickListener {
            // Conversão do EditText para String
            val dateString = dateEditText.text.toString()

            val documentreq = DocumentRequest(
                title = title.text.toString(),
                description = description.text.toString(),
                date= dateString,
                file = fileNameText.text.toString(),
                schoolYearId = 1
            )
            // Chamada da função que comunica com a API, para registar um utilizador

            uploadFile(file){statusCode ->
                if (statusCode == 201) {
                    addDocument(documentreq){statusCode ->
                        if (statusCode == 201) {
                            // Registo bem sucedido
                            Toast.makeText(this, "Documento adicionado com sucesso.", Toast.LENGTH_LONG).show()
                            val intent = Intent(this, MainActivity::class.java)
                            // Adicionando um extra chamado "fragment_to_show" com o valor "DocumentFragment" ao Intent
                            intent.putExtra("fragment_to_show", "DocumentFragment")
                            startActivity(intent)
                        }else{
                            // Erro não identificado / Falha no servidor
                            Toast.makeText(this, "Erro. Contacte o Administrador", Toast.LENGTH_SHORT).show()
                        }
                    }
                }else{
                    // Erro não identificado / Falha no servidor
                    Toast.makeText(this, "Erro. Contacte o Administrador", Toast.LENGTH_SHORT).show()
                }
            }

        }
    }

    // Função para abrir o seletor de arquivos
    private fun openFilePicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "application/pdf"
        startActivityForResult(intent, PICK_FILE_REQUEST_CODE)
    }

    // Função chamada quando um arquivo é selecionado no seletor
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_FILE_REQUEST_CODE && resultCode == RESULT_OK) {
            // Manipular o arquivo selecionado aqui
            selectedFileUri = data?.data!! // URI do arquivo selecionado

                file = getFileFromUri(selectedFileUri)
                // Obter o nome do arquivo selecionado
                displayName = getFileName(selectedFileUri)

                // Exibir o nome do arquivo no TextView
                fileNameText.text = displayName ?: " "


        }
    }


    // Função para obter o nome de um arquivo a partir de sua URI
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

    // Função para adiconar um novo Documento
    private fun addDocument(document: DocumentRequest, onResult: (Int) -> Unit) {
        // Faz a chamada a API
        val call = RetrofitInitializer().APIService().addDocument(document)

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

    //Função para dar upload de um ficheiro
    private fun uploadFile(file: File, onResult: (Int) -> Unit) {
        // Criar uma instância de MultipartBody.Part para o arquivo
        val requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file)
        val filePart = MultipartBody.Part.createFormData("file", displayName, requestFile)
        // Faz a chamada a API
        val call = RetrofitInitializer().APIService().uploadFile(filePart)

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
