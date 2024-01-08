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
        // Inicialização dos elementos de interface
        val value = findViewById<EditText>(R.id.valueText) // Campo de value
        val description = findViewById<EditText>(R.id.descriptionText) // Campo de descrição
        val dateEditText = findViewById<EditText>(R.id.dateText) // Campo de data
        val buttonCreate = findViewById<Button>(R.id.createButton)
        val radioGroup = findViewById<RadioGroup>(R.id.transactionTypeRadioGroup)
        // Definição do comportamento ao clicar no botão para criar
        buttonCreate.setOnClickListener {
            // Conversão do EditText para String
            val dateString = dateEditText.text.toString()
            val selectedRadioButtonId = radioGroup.checkedRadioButtonId
            if (selectedRadioButtonId != -1) {
                // Se um RadioButton está selecionado
                val selectedRadioButton = findViewById<RadioButton>(selectedRadioButtonId)
                val transactionType = selectedRadioButton.text.toString()
                var valueTransaction: Float? = 0.0f
                if (valueTransaction != null) {
                    if(transactionType=="Gasto"){
                        valueTransaction -= value.text.toString().toFloatOrNull() ?: 0.0f
                    }else{
                        valueTransaction = value.text.toString().toFloatOrNull() ?: 0.0f

                    }
                }
                val transactioReq = TransactionRequest(
                    description = description.text.toString(),
                    date= dateString,
                    value = valueTransaction
                )
                // Chamada da função que comunica com a API, para registar uma transação
                addTransaction(transactioReq){response ->
                    if (response?.code() == 201) {
                        // Registo bem sucedido
                        Toast.makeText(this, "Transação adicionada com sucesso.", Toast.LENGTH_LONG).show()
                        val intent = Intent(this, MainActivity::class.java)
                        // Adicionando um extra chamado "fragment_to_show" com o valor "DocumentFragment" ao Intent
                        intent.putExtra("fragment_to_show", "BalanceFragment")
                        startActivity(intent)
                    }else if (response?.code() == 200) {
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


    // Função para adiconar uma nova transação
    private fun addTransaction(transaction: TransactionRequest, onResult: (Response<ResponseAPI>?) -> Unit) {
        // Faz a chamada a API
        val call = RetrofitInitializer().APIService().addTransaction(transaction)

        call.enqueue(object : Callback<ResponseAPI> {
            override fun onFailure(call: Call<ResponseAPI>, t: Throwable) {
                t?.message?.let { Log.e("onFailure error", it) }
                onResult(null)
            }
            // Retorna o StatusCode da resposta
            override fun onResponse(call: Call<ResponseAPI>, response: Response<ResponseAPI>) {
                onResult(response)
            }
        })
    }
}
