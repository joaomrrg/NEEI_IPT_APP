
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
import pt.ipt.dam2023.neei_ipt.model.Note
import pt.ipt.dam2023.neei_ipt.retrofit.RetrofitInitializer
import pt.ipt.dam2023.neei_ipt.ui.activity.DocumentAddActivity
import pt.ipt.dam2023.neei_ipt.ui.adapter.ApontamentoAdapter
import pt.ipt.dam2023.neei_ipt.ui.adapter.DocumentAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.util.Scanner

class NotesFragment : Fragment() {
    private lateinit var listView: ListView
    private lateinit var apontamentoAdapter: ApontamentoAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_apontamentos, container, false)

        listView = view.findViewById(R.id.apontamentosListView)

        getNotes { result ->
            if (result != null) {
                val noteList = result.filterNotNull() // Filtrar documentos nulos
                // Inicializar a ListView e configurar o adaptador
                val adapter = ApontamentoAdapter(requireContext(), R.layout.item_apontamento, noteList)
                listView.adapter = adapter
            }
        }



        return view
    }

    override fun onResume() {
        super.onResume()
        getNotes { result ->
            if (result != null) {
                val noteList = result.filterNotNull() // Filtrar documentos nulos
                // Inicializar a ListView e configurar o adaptador
                val adapter = ApontamentoAdapter(requireContext(), R.layout.item_apontamento, noteList)
                listView.adapter = adapter
            }
        }
    }

    /**
     * Função para listar toda a documentação pública do NEEI
     */
    private fun getNotes(onResult: (List<Note?>) -> Unit) {
        val call = RetrofitInitializer().APIService().listNotes()
        call.enqueue(object : Callback<List<Note>?> {
            override fun onResponse(call: Call<List<Note>?>?, response: Response<List<Note>?>?) {
                response?.body()?.let {
                    val notes: List<Note?> = it
                    onResult(notes)
                }
            }

            override fun onFailure(call: Call<List<Note>?>, t: Throwable) {
                t.message?.let { Log.e("Erro onFailure", it) }
            }
        })
    }
}
