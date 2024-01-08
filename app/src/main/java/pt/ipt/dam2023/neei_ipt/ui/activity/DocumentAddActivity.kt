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
import pt.ipt.dam2023.neei_ipt.model.ResponseAPI
import pt.ipt.dam2023.neei_ipt.retrofit.RetrofitInitializer
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileNotFoundException

class DocumentAddActivity : AppCompatActivity() {

    // Código de permissão
    private val PICK_FILE_REQUEST_CODE = 123
    // Guarda o ponteiro para o objeto TextView com o nome do ficheiro
    private lateinit var fileNameText: TextView
    // Uri do ficheiro
    private lateinit var selectedFileUri: Uri
    // Objeto File com o ficheiro
    private var file: File? = null
    // Nome do documento
    private lateinit var displayName: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_document_add)

        // Ponteiros de elementos da View
        val title = findViewById<EditText>(R.id.titleText) // Campo de título
        val description = findViewById<EditText>(R.id.descriptionText) // Campo de descrição
        val dateEditText = findViewById<EditText>(R.id.dateText) // Campo de data
        val button_document = findViewById<Button>(R.id.addDocumentButton) // Botão para adicionar documento
        val button_create = findViewById<Button>(R.id.createButton) // Botão para criar

        // Referência ao TextView onde será exibido o nome do ficheiro selecionado
        fileNameText = findViewById<TextView>(R.id.fileNameText)

        // Evento de Mouse Click no botão Documento (inserir ficheiro)
        button_document.setOnClickListener {
            // chamada da função para abrir o FilePicker
            openFilePicker()
        }

        // Evento de Mouse Click no botão Adicionar Documento
        button_create.setOnClickListener {
            // Guarda a data escolhida em string
            val dateString = dateEditText.text.toString()

            // Criação do objeto a enviar para adicionar um documento
            val documentreq = DocumentRequest(
                title = title.text.toString(),
                description = description.text.toString(),
                date= dateString,
                file = fileNameText.text.toString(),
                schoolYearId = 1
            )

            // Verifica se o ficheiro é diferente de null (ou seja, se foi escolhido um ficheiro)
            if (file!=null){
                // Chamada da função que comunica com a API, para dar upload a um ficheiro
                uploadFile(file!!){ response ->
                    // Verfica se a resposta é diferente de null
                    if (response!=null){
                        if (response.code() == 201) {
                            // Deu upload do ficheiro para a API
                            // Chamada da função que comunica com a API, para adicionar um documento
                            addDocument(documentreq){response ->
                                // Verfica se a resposta é diferente de null
                                if (response!=null){
                                    if (response.code() == 201) {
                                        // Documento adicionado com sucesso
                                        Toast.makeText(this, "Documento adicionado com sucesso.", Toast.LENGTH_LONG).show()
                                        // Adiciona um extra chamado "fragment_to_show" com o valor "DocumentFragment" ao Intent
                                        val intent = Intent(this, MainActivity::class.java)
                                        intent.putExtra("fragment_to_show", "DocumentFragment")
                                        startActivity(intent)
                                    }else if (response.code() == 200){
                                        // Existe algum erro que não permitiu adicionar o documento (falta de informação)
                                        Toast.makeText(this, response.body()!!.message, Toast.LENGTH_SHORT).show()
                                    }
                                }else{
                                    // Erro não identificado / Falha no servidor
                                    Toast.makeText(this, "Erro. Contacte o Administrador", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }else if (response.code() == 200) {
                            // Existe algum erro que não permitiu dar upload ao ficheiro (falta de informação)
                            Toast.makeText(this, response.body()!!.message, Toast.LENGTH_SHORT).show()
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
                Toast.makeText(this, "Ficheiro não fornecido", Toast.LENGTH_SHORT).show()
            }


        }
    }

    /**
     * Função para abrir o FilePicker
     */
    private fun openFilePicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "application/pdf"
        startActivityForResult(intent, PICK_FILE_REQUEST_CODE)
    }

    /**
     * Função chamada quando um ficheiro é selecionado no FilePicker
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Verifica se a permissão está OK, e se foi selecionado um ficheiro
        if (requestCode == PICK_FILE_REQUEST_CODE && resultCode == RESULT_OK) {
            // Guarda o Uri do ficheiro selecionado
            selectedFileUri = data?.data!!
            // Constrói um ficheiro através do Uri
            file = getFileFromUri(selectedFileUri)
            // Guarda o nome do ficheiro selecionado
            displayName = getFileName(selectedFileUri)

            // Mostra no TextView correspondente o nome do ficheiro
            fileNameText.text = displayName ?: " "
        }
    }



    /**
     * Função para obter o nome de um ficheiro a partir do seu URI
     */
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

    /**
     * Função para receber um ficheiro a partir do seu URI
     */
    private fun getFileFromUri(uri: Uri): File {
        val inputStream = contentResolver.openInputStream(uri)
        if (inputStream != null) {
            // Cria um arquivo temporário para armazenar os dados do InputStream
            val tempFile = createTempFile("temp", null, cacheDir)
            tempFile.outputStream().use { output ->
                inputStream.copyTo(output)
            }
            return tempFile
        } else {
            throw FileNotFoundException("Não foi possível obter o ficheiro a partir da Uri.")
        }
    }

    /**
     * Função para adiconar um novo Documento
     */
    private fun addDocument(document: DocumentRequest, onResult: (Response<ResponseAPI>?) -> Unit) {
        // Faz a chamada a API
        val call = RetrofitInitializer().APIService().addDocument(document)
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

    /**
     * Função para dar upload de um ficheiro
     */
    private fun uploadFile(file: File, onResult: (Response<ResponseAPI>?) -> Unit) {
        // Cria uma instância de MultipartBody.Part para o ficheiro
        val requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file)
        val filePart = MultipartBody.Part.createFormData("file", displayName, requestFile)
        // Faz a chamada a API
        val call = RetrofitInitializer().APIService().uploadFile(filePart)

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
