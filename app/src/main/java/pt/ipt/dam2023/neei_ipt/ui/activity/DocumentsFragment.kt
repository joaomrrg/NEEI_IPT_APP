import android.media.Image
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ListView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import pt.ipt.dam2023.neei_ipt.R
import pt.ipt.dam2023.neei_ipt.model.Document
import pt.ipt.dam2023.neei_ipt.retrofit.RetrofitInitializer
import pt.ipt.dam2023.neei_ipt.ui.adapter.DocumentAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.util.Scanner

class DocumentFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_documents, container, false)

        val listView: ListView = view.findViewById(R.id.documentListView)
        val btnAddDocument = view.findViewById<ImageView>(R.id.addDocument)
        // Leitura da Internal Storage
        val directory: File = requireContext().filesDir
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
            btnAddDocument.isVisible = role==1
            sc.close()
            fi.close()
        } catch (e: FileNotFoundException) {
            Toast.makeText(requireContext(), "File not found", Toast.LENGTH_LONG).show()
        }

        getDocumentation { result ->
            if (result != null) {
                val documentList = result.filterNotNull() // Filtrar documentos nulos
                // Inicializar a ListView e configurar o adaptador
                val adapter = DocumentAdapter(requireContext(), R.layout.item_document, documentList)
                listView.adapter = adapter
            }
        }

        return view
    }

    /**
     * Função para listar toda a documentação pública do NEEI
     */
    private fun getDocumentation(onResult: (List<Document?>) -> Unit) {
        val call = RetrofitInitializer().APIService().listDocs()
        call.enqueue(object : Callback<List<Document>?> {
            override fun onResponse(call: Call<List<Document>?>?, response: Response<List<Document>?>?) {
                response?.body()?.let {
                    val docs: List<Document?> = it
                    onResult(docs)
                }
            }

            override fun onFailure(call: Call<List<Document>?>, t: Throwable) {
                t.message?.let { Log.e("Erro onFailure", it) }
            }
        })
    }
}
