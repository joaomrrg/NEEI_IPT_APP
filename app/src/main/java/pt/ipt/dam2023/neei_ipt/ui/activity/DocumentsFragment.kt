import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment
import pt.ipt.dam2023.neei_ipt.R
import pt.ipt.dam2023.neei_ipt.model.Document
import pt.ipt.dam2023.neei_ipt.retrofit.RetrofitInitializer
import pt.ipt.dam2023.neei_ipt.ui.adapter.DocumentAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DocumentFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_documents, container, false)

        getDocumentation { result ->
            if (result != null) {
                val documentList = result.filterNotNull() // Filtrar documentos nulos
                // Inicializar a ListView e configurar o adaptador
                val listView: ListView = view.findViewById(R.id.documentListView)
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
