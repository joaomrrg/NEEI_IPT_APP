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
import pt.ipt.dam2023.neei_ipt.model.Note
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

class ApontamentoAdapter(context: Context, resource: Int, objects: List<Note>) :

    ArrayAdapter<Note>(context, resource, objects){
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val itemView = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.item_apontamento, parent, false)

        // Obtem o objeto (neste caso 1 apontamento) dada a sua posição
        val apontamento = getItem(position)

        // Ponteiros de elementos da View
        val titleTextView = itemView.findViewById<TextView>(R.id.apontamentoTitle)
        val subjectTextView = itemView.findViewById<TextView>(R.id.apontamentoSubject)
        val authorTextView = itemView.findViewById<TextView>(R.id.apontamentoAuthor)
        val dateTextView = itemView.findViewById<TextView>(R.id.apontamentoDate)
        val downloadImageView = itemView.findViewById<ImageView>(R.id.downloadButton)
        val removeImageView = itemView.findViewById<ImageView>(R.id.removeButton)

        // Manipulação da informação a mostrar
        titleTextView.text = apontamento?.title
        subjectTextView.text = apontamento?.subject
        authorTextView.text = apontamento?.author
        // Formata a data recebida em String
        val dateFormatter = SimpleDateFormat("dd/MM/yyyy")
        val formattedDate = dateFormatter.format(apontamento?.createdAt)
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


        // Evento de Mouse Click no botão download
        downloadImageView.setOnClickListener {
            // Verifica se o apontamento não é nulo e chama a função de download
            if (apontamento != null) {
                downloadFile(apontamento)
            }
        }

        // Evento de Mouse Click no botão remover
        removeImageView.setOnClickListener {
            // Chamada da função que comunica com a API, para remover um apontamento
            removeApontamento(apontamento?.id!!){statusCode ->
                if (statusCode == 200) {
                    // Removido com sucesso
                    showToast("Apontamento removido com sucesso.",)
                    // Remove o documento da lista
                    remove(apontamento)
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

    /**
     * Função para remover um apontamento
     */
    private fun removeApontamento(id: Int, onResult: (Int) -> Unit) {
        // Faz a chamada a API
        val call = RetrofitInitializer().APIService().removeApontamento(id)
        call.enqueue(object : Callback<Void> {

            // Tratamento de falha da chamada à API
            override fun onFailure(call: Call<Void>, t: Throwable) {
                t?.message?.let { Log.e("onFailure error", it) }
                onResult(501)
            }

            // Tratamento da resposta bem-sucedida da chamada à API
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                onResult(response.code()) // Chama a função de retorno com o statusCode da resposta da API
            }
        })
    }

    /**
     * Função que mostra um Toast, dada uma mensagem
     */
    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    /**
     * Função para fazer download de um ficheiro
     */
    private fun downloadFile(apontamento: Note){
        // Utilizamos o CoroutineScope para fazer download de ficheiros
        GlobalScope.launch(Dispatchers.IO) {
            try {
                // Constrói a URL para o ficheiro a ser transferido
                val url = URL("https://neei.eu.pythonanywhere.com/files/" + apontamento?.file)
                // Abre uma ligação para a URL especificada
                val connection = url.openConnection()
                val inputStream = connection.getInputStream()
                // Guarda o caminho da pasta de Downloads
                val downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)

                // Guarda o nome do ficheiro
                val fileName = apontamento?.file

                // Cria um objeto File representando o caminho completo para o ficheiro na pasta de Downloads
                val file = File(downloadDir, fileName)
                // Cria um fluxo de saída para escrever os dados no ficheiro local
                val outputStream = FileOutputStream(file)

                // Lê e escreve os dados do ficheiro em blocos usando um buffer
                val buffer = ByteArray(1024)
                var bytesRead: Int
                while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                    outputStream.write(buffer, 0, bytesRead)
                }
                outputStream.close()
                inputStream.close()

                // Executa as operações na thread principal após o download ser concluído
                withContext(Dispatchers.Main) {
                    showToast("Download completo")
                    // Função para abrir ficheiro
                    showOpenFilePopup(file)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    showToast("Erro ao fazer download")
                    println("Erro durante o download: ${e.message}")
                }
            }
        }
    }

    /**
     * Função para mostrar uma AlertDialog para abrir o ficheiro imediatamente ao seu download
     */
    private fun showOpenFilePopup(file: File) {
        val alertDialogBuilder = AlertDialog.Builder(context)
        alertDialogBuilder.setTitle("Abrir ficheiro")
            .setMessage("A transferência foi concluída. Quer abrir o ficheiro?")
            .setPositiveButton("Sim") { _, _ ->
                // Abre o ficheiro
                openFile(context,file)
            }
            .setNegativeButton("Não") { _, _ ->
            }
            .setCancelable(false)
            .show()
    }

    /**
     * Função para abrir um ficheiro
     */
    private fun openFile(context: Context, file: File) {
        // Obtém o URI do ficheiro usando um FileProvider para garantir acesso seguro ao arquivo
        val uri = FileProvider.getUriForFile(context, "pt.ipt.dam2023.neei_ipt.fileprovider", file)
        // Cria uma Intent para visualizar o ficheiro
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(uri, getMimeType(file.absolutePath))
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_GRANT_READ_URI_PERMISSION

        try {
            // Inicia a atividade para visualizar o arquivo
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            showToast("Nenhuma aplicação encontrada para abrir o ficheiro")
        }
    }

    /**
     * Função para obter a extensão do ficheiro (tipo de ficheiro)
     */
    private fun getMimeType(url: String): String? {
        val extension = MimeTypeMap.getFileExtensionFromUrl(url)
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
    }
}