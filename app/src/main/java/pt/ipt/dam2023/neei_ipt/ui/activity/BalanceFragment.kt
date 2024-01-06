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
import pt.ipt.dam2023.neei_ipt.model.Document
import pt.ipt.dam2023.neei_ipt.model.Transaction
import pt.ipt.dam2023.neei_ipt.model.TransactionBudget
import pt.ipt.dam2023.neei_ipt.model.User
import pt.ipt.dam2023.neei_ipt.retrofit.RetrofitInitializer
import pt.ipt.dam2023.neei_ipt.ui.adapter.DocumentAdapter
import pt.ipt.dam2023.neei_ipt.ui.adapter.TransactionAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BalanceFragment : Fragment() {

    // Saldo atual simulado
    private var currentBalance = "-"
    private lateinit var listView: ListView
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Infla o layout para este fragmento
        val view = inflater.inflate(R.layout.balance_layout, container, false)
        listView = view.findViewById(R.id.transactionsListView)
        // Encontra as views no layout inflado
        val currentBalanceText = view.findViewById<TextView>(R.id.currentBalanceText)
        val hideButton = view.findViewById<ImageView>(R.id.hideBttn)
        val addMoneyButton = view.findViewById<FloatingActionButton>(R.id.addMoneyBttn)
        val addMovementButton = view.findViewById<FloatingActionButton>(R.id.addMovementBttn)
        val checkMovementButton = view.findViewById<FloatingActionButton>(R.id.checkMovementBttn)
        val euroText = view.findViewById<TextView>(R.id.euro)

        getTransactions { result ->
            if (result != null) {
                val transactionList = result.filterNotNull() // Filtrar documentos nulos
                // Inicializar a ListView e configurar o adaptador
                val adapter = TransactionAdapter(requireContext(), R.layout.item_transaction, transactionList)
                listView.adapter = adapter
            }
        }

        getBudget { result ->
            if (result != null) {
                // Define o saldo atual no TextView correspondente
                currentBalanceText.text = result.budget.toString()
                currentBalance = result.budget.toString()
            }
        }


        // Define o comportamento do botão de ocultar/mostrar saldo
        hideButton.setOnClickListener {
            if (currentBalanceText.text == currentBalance) {
                currentBalanceText.text = "-"
                euroText.isVisible = false
            } else {
                currentBalanceText.text = currentBalance
                euroText.isVisible = true
            }
        }

        // Define o comportamento do botão de adicionar dinheiro
        addMoneyButton.setOnClickListener {

        }

        // Define o comportamento do botão de adicionar movimento
        addMovementButton.setOnClickListener {

        }

        // Define o comportamento do botão de verificar movimentos
        checkMovementButton.setOnClickListener {

        }

        return view
    }

    /**
     * Função para obter o Saldo da conta do NEEI
     */
    private fun getBudget(onResult: (TransactionBudget?) -> Unit) {
        val call = RetrofitInitializer().APIService().getBudget()
        call.enqueue(object : Callback<TransactionBudget?> {
            override fun onResponse(call: Call<TransactionBudget?>?, response: Response<TransactionBudget?>?) {
                response?.body()?.let {
                    val budget: TransactionBudget = it
                    onResult(budget)
                }
            }

            override fun onFailure(call: Call<TransactionBudget?>, t: Throwable) {
                // Em caso de falha na requisição, registra o erro no Log
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
