package pt.ipt.dam2023.neei_ipt.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import pt.ipt.dam2023.neei_ipt.R
import pt.ipt.dam2023.neei_ipt.model.Transaction
import pt.ipt.dam2023.neei_ipt.model.TransactionBudget
import pt.ipt.dam2023.neei_ipt.retrofit.RetrofitInitializer
import pt.ipt.dam2023.neei_ipt.ui.activity.TransactionAddActivity
import pt.ipt.dam2023.neei_ipt.ui.adapter.TransactionAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BalanceViewFragment : Fragment() {

    // Variável que guarda o valor do Saldo do NEEI
    private var currentBalance = "-"
    // Variável que guarda o ponteiro para a ListView de transações
    private lateinit var listView: ListView
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Infla (hierarquiza) o layout para este fragmento
        val view = inflater.inflate(R.layout.balance_layout, container, false)
        // Associa a variável listView com o ponteiro do objeto na view
        listView = view.findViewById(R.id.transactionsListView)
        // Ponteiros de elementos da View
        val currentBalanceText = view.findViewById<TextView>(R.id.currentBalanceText)
        val hideButton = view.findViewById<ImageView>(R.id.hideBttn)
        val addMovementButton = view.findViewById<FloatingActionButton>(R.id.addMovementBttn)
        val euroText = view.findViewById<TextView>(R.id.euro)

        // Chamada da função que comunica com a API, para obter a lista de transações
        getTransactions { result ->
            if (result != null) {
                val transactionList = result.filterNotNull() // Filtrar documentos nulos
                // Carrega a lista obtida na ListView e configura o adaptador
                val adapter = TransactionAdapter(requireContext(), R.layout.item_transaction, transactionList)
                listView.adapter = adapter
            }
        }

        // Chamada da função que comunica com a API, para o saldo do NEEI
        getBudget { result ->
            if (result != null) {
                // Verifica se o valor não é recebido a null
                if(result.budget.toString()!="null"){
                    // Carrega para a TextView o valor recebido da API
                    currentBalanceText.text = result.budget.toString()
                    // Carrega para a variável o valor recebido da API
                    currentBalance = result.budget.toString()
                }else{
                    currentBalanceText.text = "0"
                    currentBalance = "0"
                }

            }
        }


        // Define o comportamento do botão de ocultar/mostrar saldo, quando clicado
        hideButton.setOnClickListener {
            // Verifica se está a mostra o valor
            if (currentBalanceText.text == currentBalance) {
                // Se estiver a mostrar o valor, irá ocultar com um "-"
                currentBalanceText.text = "-"
                euroText.isVisible = false
            } else {
                currentBalanceText.text = currentBalance
                euroText.isVisible = true
            }
        }


        // Define o comportamento do botão de adicionar movimento, quando clicado
        addMovementButton.setOnClickListener {
            // Cria uma Intent para iniciar a TransactionAddActivity
            val intent = Intent(requireActivity(), TransactionAddActivity::class.java)
            // Inicia a TransactionAddActivity
            startActivity(intent)
        }
        return view
    }

    /**
     * Função para obter o Saldo da conta do NEEI
     */
    private fun getBudget(onResult: (TransactionBudget?) -> Unit) {
        // Faz a chamada a API
        val call = RetrofitInitializer().APIService().getBudget()
        call.enqueue(object : Callback<TransactionBudget?> {
            // Tratatamento da resposta bem-sucedida da chamada à API
            override fun onResponse(call: Call<TransactionBudget?>?, response: Response<TransactionBudget?>?) {
                response?.body()?.let {
                    // Guarda o resultado da apli ma variável budget
                    val budget: TransactionBudget = it
                    // Chama a função de retorno com a resposta da API
                    onResult(budget)
                }
            }

            // Tratatamento de falha da chamada à API
            override fun onFailure(call: Call<TransactionBudget?>, t: Throwable) {
                // Em caso de falha na requisição, registaa o erro no Log
                t.message?.let { Log.e("Erro onFailure", it) }
            }
        })
    }

    /**
     * Função para listar todas as transações do NEEI
     */
    private fun getTransactions(onResult: (List<Transaction?>) -> Unit) {
        val call = RetrofitInitializer().APIService().listTransactions()
        call.enqueue(object : Callback<List<Transaction>?> {
            override fun onResponse(call: Call<List<Transaction>?>?, response: Response<List<Transaction>?>?) {
                response?.body()?.let {
                    val transactions: List<Transaction?> = it
                    onResult(transactions)
                }
            }

            override fun onFailure(call: Call<List<Transaction>?>, t: Throwable) {
                t.message?.let { Log.e("Erro onFailure", it) }
            }
        })
    }
}
