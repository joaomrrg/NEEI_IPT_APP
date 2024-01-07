package pt.ipt.dam2023.neei_ipt.ui.adapter

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.vmadalin.easypermissions.EasyPermissions
import com.vmadalin.easypermissions.dialogs.SettingsDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pt.ipt.dam2023.neei_ipt.R
import pt.ipt.dam2023.neei_ipt.model.Document
import pt.ipt.dam2023.neei_ipt.model.TransactionRequest
import pt.ipt.dam2023.neei_ipt.retrofit.RetrofitInitializer
import pt.ipt.dam2023.neei_ipt.ui.activity.MainActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Scanner

class DocumentAdapter(context: Context, resource: Int, objects: List<Document>) :

    ArrayAdapter<Document>(context, resource, objects){
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val itemView = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.item_document, parent, false)

        val document = getItem(position)

        val titleTextView = itemView.findViewById<TextView>(R.id.documentTitle)
        val descriptionTextView = itemView.findViewById<TextView>(R.id.documentDescription)
        val dateTextView = itemView.findViewById<TextView>(R.id.documentDate)
        val downloadImageView = itemView.findViewById<ImageView>(R.id.downloadButton)
        val removeImageView = itemView.findViewById<ImageView>(R.id.removeButton)
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
            removeImageView.isVisible = role==1
            sc.close()
            fi.close()
        } catch (e: FileNotFoundException) {
            Toast.makeText(context, "File not found", Toast.LENGTH_LONG).show()
        }


        // Configurar a lógica de download (se necessário) para o ImageView
        downloadImageView.setOnClickListener {
            // Use corrotinas para realizar o download em segundo plano
            if (document != null) {
                downloadFile(document)

            }
        }

        removeImageView.setOnClickListener {
            removeTransaction(document?.id!!){statusCode ->
                if (statusCode == 200) {
                    // Registo bem sucedido
                    showToast("Documento removido com sucesso.",)
                    // Remove o documento da lista
                    remove(document)

                    // Notifica o adaptador sobre a mudança na lista
                    notifyDataSetChanged()
                }else{
                    // Erro não identificado / Falha no servidor
                    showToast("Erro. Contacte o Administrador",)
                }
            }
        }
        return itemView
    }

    // Função para remover um documento
    private fun removeTransaction(id: Int, onResult: (Int) -> Unit) {
        // Faz a chamada a API
        val call = RetrofitInitializer().APIService().removeDocument(id)
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
    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private fun downloadFile(document: Document){
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
                    showOpenFilePopup(file)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    showToast("Erro ao fazer download§")
                    println("Erro durante o download: ${e.message}")
                }
            }
        }
    }

    private fun showOpenFilePopup(file: File) {
        val alertDialogBuilder = AlertDialog.Builder(context)
        alertDialogBuilder.setTitle("Abrir ficheiro")
            .setMessage("A transferência foi concluída. Quer abrir o ficheiro?")
            .setPositiveButton("Sim") { _, _ ->
                openFile(context,file)
            }
            .setNegativeButton("Não") { _, _ ->
                // Opção para não abrir o arquivo, caso o usuário clique em "Não"
            }
            .setCancelable(false)
            .show()
    }

    private fun openFile(context: Context, file: File) {
        val uri = FileProvider.getUriForFile(context, "pt.ipt.dam2023.neei_ipt.fileprovider", file)
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(uri, getMimeType(file.absolutePath))
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_GRANT_READ_URI_PERMISSION

        try {
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            showToast("Nenhum aplicativo encontrado para abrir o arquivo")
        }
    }


    private fun getMimeType(url: String): String? {
        val extension = MimeTypeMap.getFileExtensionFromUrl(url)
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
    }


}