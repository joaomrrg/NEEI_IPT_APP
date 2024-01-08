package pt.ipt.dam2023.neei_ipt.ui.fragment
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
import pt.ipt.dam2023.neei_ipt.ui.activity.AdminRegisterActivity
import pt.ipt.dam2023.neei_ipt.ui.adapter.UserAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserViewFragment : Fragment() {
    // Variável que guarda o ponteiro para a ListView de Utilizadores
    private lateinit var listView: ListView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Infla (hierarquiza) o layout para este fragmento
        val view = inflater.inflate(R.layout.fragment_user_layout, container, false)

        // Associa a variável listView com o ponteiro do objeto na view
        listView = view.findViewById(R.id.userListView)

        // Ponteiros de elementos da View
        val btnAddUser = view.findViewById<ImageView>(R.id.addUser)

        // Chamada da função que comunica com a API, para obter a lista de utilizadores
        getUsers { result ->
            if (result != null) {
                // Filtrar usuários nulos
                val userList = result.filterNotNull()
                // Inicializare configura o adaptador para a ListView
                val adapter = UserAdapter(requireContext(), R.layout.item_user, userList)
                listView.adapter = adapter
            }
        }

        // Evento de Mouse Click no botão Adicionar Utilizador
        btnAddUser.setOnClickListener{
            val intent = Intent(requireActivity(), AdminRegisterActivity::class.java)
            startActivity(intent)
        }
        return view
    }

    /**
     * Função para obter a lista de utilizadores
     */
    private fun getUsers(onResult: (List<User?>) -> Unit) {
        // Faz a chamada a API
        val call = RetrofitInitializer().APIService().listUsers()
        call.enqueue(object : Callback<List<User>?> {
            // Tratamento da resposta bem-sucedida da chamada à API
            override fun onResponse(call: Call<List<User>?>?, response: Response<List<User>?>?) {
                response?.body()?.let {
                    // Guarda a lista de utilizadores em variavel
                    val users: List<User?> = it
                    onResult(users) // Chama a função de retorno com a resposta da API
                }
            }

            // Tratamento de falha da chamada à API
            override fun onFailure(call: Call<List<User>?>, t: Throwable) {
                // Em caso de falha na requisição, regista o erro no Log
                t.message?.let { Log.e("Erro onFailure", it) }
            }
        })
    }
}
