package pt.ipt.dam2023.neei_ipt.ui.adapter

import android.content.Context
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.isVisible
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pt.ipt.dam2023.neei_ipt.R
import pt.ipt.dam2023.neei_ipt.model.Document
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Scanner

class DocumentAdapter(context: Context, resource: Int, objects: List<Document>) :
    ArrayAdapter<Document>(context, resource, objects) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val itemView = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.item_document, parent, false)

        val document = getItem(position)

        val titleTextView = itemView.findViewById<TextView>(R.id.documentTitle)
        val descriptionTextView = itemView.findViewById<TextView>(R.id.documentDescription)
        val dateTextView = itemView.findViewById<TextView>(R.id.documentDate)
        val downloadImageView = itemView.findViewById<ImageView>(R.id.downloadButton)
        val editImageView = itemView.findViewById<ImageView>(R.id.editButton)
        titleTextView.text = document?.title
        descriptionTextView.text = document?.description
        val dateFormatter = SimpleDateFormat("dd/MM/yyyy")
        val formattedDate = dateFormatter.format(document?.date)
        dateTextView.text = formattedDate

        // Leitura da Internal Storage
        val directory: File = context.filesDir
        val file: File = File(directory, "dados.txt")
        try {
            val fi: FileInputStream = FileInputStream(file)
            val sc: Scanner = Scanner(fi)
            sc.nextLine()
            sc.nextLine()
            sc.nextLine()
            // Guarda a role do user
            val role =  sc.nextLine().toInt()
            //Mostra o botao se for admin
            editImageView.isVisible = role==1
            sc.close()
            fi.close()
        } catch (e: FileNotFoundException) {
            Toast.makeText(context, "File not found", Toast.LENGTH_LONG).show()
        }



        // Configurar a lógica de download (se necessário) para o ImageView
        downloadImageView.setOnClickListener {
            // Use corrotinas para realizar o download em segundo plano
            GlobalScope.launch(Dispatchers.IO) {
                try {
                    val url = URL("https://neei.eu.pythonanywhere.com/files/" + document?.file)
                    val connection = url.openConnection()
                    val inputStream = connection.getInputStream()
                    // Diretório de Downloads
                    val downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)

                    // Nome do arquivo no diretório de Downloads
                    val fileName = document?.file  // Substitua pelo nome real do arquivo

                    val file = File(downloadDir, fileName)
                    val outputStream = FileOutputStream(file)

                    val buffer = ByteArray(1024)
                    var bytesRead: Int
                    while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                        outputStream.write(buffer, 0, bytesRead)
                    }
                    outputStream.close()
                    inputStream.close()
                    withContext(Dispatchers.Main) {
                        showToast("Download completo")
                        println("Download completo para: ${file.absolutePath}")
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        showToast("Erro ao fazer download§")
                        println("Erro durante o download: ${e.message}")
                    }
                }
            }
        }
        return itemView
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

}