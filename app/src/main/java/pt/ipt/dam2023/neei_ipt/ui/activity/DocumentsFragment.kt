
import android.content.Intent
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
import pt.ipt.dam2023.neei_ipt.R
import pt.ipt.dam2023.neei_ipt.model.Document
import pt.ipt.dam2023.neei_ipt.retrofit.RetrofitInitializer
import pt.ipt.dam2023.neei_ipt.ui.activity.DocumentAddActivity
import pt.ipt.dam2023.neei_ipt.ui.adapter.DocumentAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.util.Scanner

class DocumentFragment : Fragment() {
    private lateinit var listView: ListView
    private lateinit var documentAdapter: DocumentAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_documents, container, false)

        listView = view.findViewById(R.id.documentListView)
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

        btnAddDocument.setOnClickListener{
            val intent = Intent(requireActivity(), DocumentAddActivity::class.java)
            startActivity(intent)
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        getDocumentation { result ->
            if (result != null) {
                val documentList = result.filterNotNull() // Filtrar documentos nulos
                // Inicializar a ListView e configurar o adaptador
                val adapter = DocumentAdapter(requireContext(), R.layout.item_document, documentList)
                listView.adapter = adapter
            }
        }
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
