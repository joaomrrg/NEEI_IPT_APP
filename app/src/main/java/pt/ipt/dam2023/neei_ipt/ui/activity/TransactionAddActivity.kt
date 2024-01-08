package pt.ipt.dam2023.neei_ipt.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import pt.ipt.dam2023.neei_ipt.R
import pt.ipt.dam2023.neei_ipt.model.ResponseAPI
import pt.ipt.dam2023.neei_ipt.model.TransactionRequest
import pt.ipt.dam2023.neei_ipt.retrofit.RetrofitInitializer
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TransactionAddActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transaction_add)
        // Ponteiros de elementos da View
        val value = findViewById<EditText>(R.id.valueText) // Campo de value
        val description = findViewById<EditText>(R.id.descriptionText) // Campo de descrição
        val dateEditText = findViewById<EditText>(R.id.dateText) // Campo de data
        val buttonCreate = findViewById<Button>(R.id.createButton)
        val radioGroup = findViewById<RadioGroup>(R.id.transactionTypeRadioGroup)

        // Evento de Mouse Click no botão Adicioanr
        buttonCreate.setOnClickListener {
            // Guarda o texo do EditText em varável
            val dateString = dateEditText.text.toString()
            // Guarda o valor escolhido no tipo de Transação
            val selectedRadioButtonId = radioGroup.checkedRadioButtonId
            // Verifica se foi selecionado algum
            if (selectedRadioButtonId != -1) {
                // Guarda o valor do radio buttton em texto numa variável
                val selectedRadioButton = findViewById<RadioButton>(selectedRadioButtonId)
                val transactionType = selectedRadioButton.text.toString()
                // Manipula o valor da transação inserido e conforme o tipo de transação guarda o número
                // em negativo ou positivo
                var valueTransaction: Float? = 0.0f
                if (valueTransaction != null) {
                    if(transactionType=="Gasto"){
                        valueTransaction -= value.text.toString().toFloatOrNull() ?: 0.0f
                    }else{
                        valueTransaction = value.text.toString().toFloatOrNull() ?: 0.0f

                    }
                }
                // Criação do objeto a enviar para adicionar uma transação
                val transactioReq = TransactionRequest(
                    description = description.text.toString(),
                    date= dateString,
                    value = valueTransaction
                )
                // Chamada da função que comunica com a API, para adicionar uma transação
                addTransaction(transactioReq){response ->
                    if (response?.code() == 201) {
                        // Registo bem sucedido
                        Toast.makeText(this, "Transação adicionada com sucesso.", Toast.LENGTH_LONG).show()
                        val intent = Intent(this, MainActivity::class.java)
                        // Adicionando um extra chamado "fragment_to_show" com o valor "BalanceViewFragment" ao Intent
                        intent.putExtra("fragment_to_show", "BalanceViewFragment")
                        startActivity(intent)
                    }else if (response?.code() == 200) {
                        // Existe algum erro que não permitiu o registo (falta de informação)
                        Toast.makeText(this, response.body()?.message, Toast.LENGTH_SHORT).show()
                    }else{
                        // Erro não identificado / Falha no servidor
                        Toast.makeText(this, "Erro. Contacte o Administrador", Toast.LENGTH_SHORT).show()
                    }
                }
            }else{
                Toast.makeText(this, "Preencha o tipo de Transação", Toast.LENGTH_SHORT).show()
            }
        }
    }



    /**
     * Função para adiconar uma nova transação
     */
    private fun addTransaction(transaction: TransactionRequest, onResult: (Response<ResponseAPI>?) -> Unit) {
        // Faz a chamada a API
        val call = RetrofitInitializer().APIService().addTransaction(transaction)

        call.enqueue(object : Callback<ResponseAPI> {
            // Tratamento de falha da chamada à API
            override fun onFailure(call: Call<ResponseAPI>, t: Throwable) {
                t?.message?.let { Log.e("onFailure error", it) }
                onResult(null)
            }
            // Tratamento da resposta bem-sucedida da chamada à API
            override fun onResponse(call: Call<ResponseAPI>, response: Response<ResponseAPI>) {
                onResult(response) // Chama a função de retorno com a resposta da API
            }
        })
    }
}
