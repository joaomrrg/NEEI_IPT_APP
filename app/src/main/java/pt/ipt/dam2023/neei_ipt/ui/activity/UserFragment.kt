import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ListView
import androidx.fragment.app.Fragment
import pt.ipt.dam2023.neei_ipt.R
import pt.ipt.dam2023.neei_ipt.model.User
import pt.ipt.dam2023.neei_ipt.retrofit.RetrofitInitializer
import pt.ipt.dam2023.neei_ipt.ui.activity.DocumentAddActivity
import pt.ipt.dam2023.neei_ipt.ui.adapter.DocumentAdapter
import pt.ipt.dam2023.neei_ipt.ui.adapter.UserAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class UserFragment : Fragment() {
    private lateinit var listView: ListView
    private lateinit var documentAdapter: DocumentAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflar o layout do fragmento
        val view = inflater.inflate(R.layout.fragment_user_layout, container, false)

        // Inicializar a ListView
        listView = view.findViewById(R.id.userListView)

        // Referenciar o botão de adicionar usuário
        val btnAddUser = view.findViewById<ImageView>(R.id.addUser)

        // Leitura da Internal Storage
        val directory: File = requireContext().filesDir
        val file: File = File(directory, "dados.txt")

        // Obter a lista de usuários utilizando Retrofit
        getUsers { result ->
            if (result != null) {
                // Filtrar usuários nulos
                val userList = result.filterNotNull()
                // Inicializar e configurar o adaptador para a ListView
                val adapter = UserAdapter(requireContext(), R.layout.item_user, userList)
                listView.adapter = adapter
            }
        }

        // Configurar o evento de clique do botão para adicionar usuário
        btnAddUser.setOnClickListener{
            val intent = Intent(requireActivity(), DocumentAddActivity::class.java)
            startActivity(intent)
        }

        return view
    }

    /**
     * Função para obter a lista de usuários usando Retrofit
     */
    private fun getUsers(onResult: (List<User?>) -> Unit) {
        val call = RetrofitInitializer().APIService().listUsers()
        call.enqueue(object : Callback<List<User>?> {
            override fun onResponse(call: Call<List<User>?>?, response: Response<List<User>?>?) {
                response?.body()?.let {
                    val users: List<User?> = it
                    onResult(users)
                }
            }

            override fun onFailure(call: Call<List<User>?>, t: Throwable) {
                // Em caso de falha na requisição, registra o erro no Log
                t.message?.let { Log.e("Erro onFailure", it) }
            }
        })
    }
}
