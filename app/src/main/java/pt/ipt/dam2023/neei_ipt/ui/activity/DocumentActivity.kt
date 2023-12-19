package pt.ipt.dam2023.neei_ipt.ui.activity

import android.os.Bundle
import android.util.Log
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import pt.ipt.dam2023.neei_ipt.R
import pt.ipt.dam2023.neei_ipt.model.Document
import pt.ipt.dam2023.neei_ipt.model.Group
import pt.ipt.dam2023.neei_ipt.model.User
import pt.ipt.dam2023.neei_ipt.retrofit.RetrofitInitializer
import pt.ipt.dam2023.neei_ipt.ui.adapter.DocumentAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Date

class DocumentActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_documents)
        // Criar uma lista de documentos para exibição

        getDocumentation { result ->
            if (result != null) {
                var documentList = result as List<Document>
                // Inicializar a ListView e configurar o adaptador
                val listView: ListView = findViewById(R.id.documentListView)
                val adapter = DocumentAdapter(this, R.layout.item_document, documentList)
                listView.adapter = adapter
            }
        }


    }

    /**
     * Função para listar toda a documentação publica do NEEI
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
                t?.message?.let { Log.e("Erro onFailure", it) }
            }
        })
    }
}