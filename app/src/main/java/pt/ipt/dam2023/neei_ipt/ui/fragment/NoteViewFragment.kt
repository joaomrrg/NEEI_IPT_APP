package pt.ipt.dam2023.neei_ipt.ui.fragment
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment
import pt.ipt.dam2023.neei_ipt.R
import pt.ipt.dam2023.neei_ipt.model.Note
import pt.ipt.dam2023.neei_ipt.retrofit.RetrofitInitializer
import pt.ipt.dam2023.neei_ipt.ui.adapter.ApontamentoAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NoteViewFragment : Fragment() {
    // Variável que guarda o ponteiro para a ListView de apontamentos
    private lateinit var listView: ListView
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Infla (hierarquiza) o layout para este fragmento
        val view = inflater.inflate(R.layout.activity_apontamentos, container, false)
        // Associa a variável listView com o ponteiro do objeto na view
        listView = view.findViewById(R.id.apontamentosListView)

        // Chamada da função que comunica com a API, para obter a lista de apontamentos
        getNotes { result ->
            if (result != null) {
                val noteList = result.filterNotNull() // Filtrar documentos nulos
                // Inicializa a ListView e configura o adaptador
                val adapter = ApontamentoAdapter(requireContext(), R.layout.item_apontamento, noteList)
                listView.adapter = adapter
            }
        }
        return view
    }

    /**
     * Função chamada quando o focus é retomado (resumed) para listar todos os apontamentos
     * disponibilizados pelo NEEI.
     */
    override fun onResume() {
        super.onResume()
        // Chamada da função que comunica com a API, para obter a lista de apontamentos
        getNotes { result ->
            if (result != null) {
                val noteList = result.filterNotNull() // Filtrar documentos nulos
                // Inicializa a ListView e configura o adaptador
                val adapter = ApontamentoAdapter(requireContext(), R.layout.item_apontamento, noteList)
                listView.adapter = adapter
            }
        }
    }

    /**
     * Função para listar todos os apontamentos disponibilizados pelo NEEI.
     */
    private fun getNotes(onResult: (List<Note?>) -> Unit) {
        // Faz a chamada a API
        val call = RetrofitInitializer().APIService().listNotes()
        call.enqueue(object : Callback<List<Note>?> {
            // Tratamento da resposta bem-sucedida da chamada à API
            override fun onResponse(call: Call<List<Note>?>?, response: Response<List<Note>?>?) {
                response?.body()?.let {
                    // Guarda a lista de apontamentos
                    val notes: List<Note?> = it
                    onResult(notes) // Chama a função de retorno com a resposta da API
                }
            }
            // Tratamento de falha da chamada à API
            override fun onFailure(call: Call<List<Note>?>, t: Throwable) {
                t.message?.let { Log.e("Erro onFailure", it) }
            }
        })
    }
}
