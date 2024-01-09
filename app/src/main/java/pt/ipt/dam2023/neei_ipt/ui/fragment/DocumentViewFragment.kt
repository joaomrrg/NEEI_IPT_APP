
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

class DocumentViewFragment : Fragment() {
    // Variável que guarda o ponteiro para a ListView de documentos
    private lateinit var listView: ListView
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Infla (hierarquiza) o layout para este fragmento
        val view = inflater.inflate(R.layout.activity_documents, container, false)
        // Associa a variável listView com o ponteiro do objeto na view
        listView = view.findViewById(R.id.documentListView)
        // Ponteiros de elementos da View
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
            // Mostra o botao se for admin
            btnAddDocument.isVisible = role==1
            sc.close()
            fi.close()
        } catch (e: FileNotFoundException) {
            Toast.makeText(requireContext(), "File not found", Toast.LENGTH_LONG).show()
        }

        // Chamada da função que comunica com a API, para obter a lista de documentos
        getDocumentation { result ->
            if (result != null) {
                val documentList = result.filterNotNull() // Filtrar documentos nulos
                // Inicializa a ListView e configura o adaptador
                val adapter = DocumentAdapter(requireContext(), R.layout.item_document, documentList)
                listView.adapter = adapter
            }
        }

        // Evento de Mouse Click no botão Adicionar Documento
        btnAddDocument.setOnClickListener{
            val intent = Intent(requireActivity(), DocumentAddActivity::class.java)
            startActivity(intent)
        }
        return view
    }

    /**
     * Função chamada quando o focus é retomado (resumed) para listar toda a documentação pública do NEEI.
     */
    override fun onResume() {
        super.onResume()
        // Chamada da função que comunica com a API, para obter a lista de documentos
        getDocumentation { result ->
            if (result != null) {
                val documentList = result.filterNotNull() // Filtrar documentos nulos
                // Inicializa a ListView e configura o adaptador
                val adapter = DocumentAdapter(requireContext(), R.layout.item_document, documentList)
                listView.adapter = adapter
            }
        }
    }

    /**
     * Função para listar toda a documentação pública do NEEI
     */
    private fun getDocumentation(onResult: (List<Document?>) -> Unit) {
        // Faz a chamada a API
        val call = RetrofitInitializer().APIService().listDocs()
        call.enqueue(object : Callback<List<Document>?> {
            // Tratamento da resposta bem-sucedida da chamada à API
            override fun onResponse(call: Call<List<Document>?>?, response: Response<List<Document>?>?) {
                response?.body()?.let {
                    // Guarda a lista de documentos
                    val docs: List<Document?> = it
                    onResult(docs) // Chama a função de retorno com a resposta da API
                }
            }

            // Tratamento de falha da chamada à API
            override fun onFailure(call: Call<List<Document>?>, t: Throwable) {
                t.message?.let { Log.e("Erro onFailure", it) }
            }
        })
    }
}
