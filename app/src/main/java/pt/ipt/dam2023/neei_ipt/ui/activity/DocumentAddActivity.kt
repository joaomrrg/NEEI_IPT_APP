package pt.ipt.dam2023.neei_ipt.ui.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import pt.ipt.dam2023.neei_ipt.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DocumentAddActivity : AppCompatActivity() {

    private val PICK_FILE_REQUEST_CODE = 123
    private lateinit var fileNameText: TextView

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

            // Conversão da string de data para o formato Date
            val date = stringToDate(dateString)
        }
    }

    // Função para converter uma string em um objeto Date
    fun stringToDate(date_as_String: String): Date? {
        val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return format.parse(date_as_String)
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
            val selectedFileUri = data?.data // URI do arquivo selecionado

            // Obter o nome do arquivo selecionado
            val displayName = selectedFileUri?.let { getFileName(it) }

            // Exibir o nome do arquivo no TextView
            fileNameText.text = displayName ?: " "
        }
    }

    // Função para obter o nome de um arquivo a partir de sua URI
    private fun getFileName(uri: Uri): String? {
        val cursor = contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (it.moveToFirst()) {
                return it.getString(nameIndex)
            }
        }
        return null
    }
}
