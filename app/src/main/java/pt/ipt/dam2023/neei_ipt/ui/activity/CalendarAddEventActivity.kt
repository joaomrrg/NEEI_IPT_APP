package pt.ipt.dam2023.neei_ipt.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.DatePicker
import android.widget.Spinner
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import pt.ipt.dam2023.neei_ipt.R
import pt.ipt.dam2023.neei_ipt.model.CalendarRequest
import pt.ipt.dam2023.neei_ipt.model.CalendarResponse
import pt.ipt.dam2023.neei_ipt.model.Group
import pt.ipt.dam2023.neei_ipt.retrofit.RetrofitInitializer
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class CalendarAddEventActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_event_neei)

        // Ponteiros de elementos da View
        val addButton = findViewById<Button>(R.id.addbutton)
        val datePicker = findViewById<DatePicker>(R.id.datepicker)
        val eventName = findViewById<TextView>(R.id.nomeevento)
        val eventDescription = findViewById<TextView>(R.id.descricaoevento)
        val spinner = findViewById<Spinner>(R.id.spinner)

        // Chamada da função que comunica com a API, e recebe a lista de grupos
        getGroups { result ->
            if (result != null) {
                // Verifica se a lista não vem vazia
                if (result.isNotEmpty()) {
                    // Percorre cada elemento da lista
                    for (it in result) {
                        // Cria um array com as descrições de cada grupo a partir dos elementos da lista
                        val groupDescriptions = result.map { it?.description }.toTypedArray()

                        // Configuração do adaptador do Spinner com as opções da API
                        val adapter = ArrayAdapter(this, R.layout.custom_spinner, groupDescriptions)
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        spinner.adapter = adapter
                    }
                } else {
                    Log.i("Calendar", "Lista vazia")
                }
            } else {
                // Erro não identificado / Falha no servidor
                Toast.makeText(this, "Erro. Contacte o Administrador", Toast.LENGTH_SHORT).show()
            }
        }

        // Evento de Mouse Click no botão Adicionar
        addButton.setOnClickListener {
            // Guarda em variáveis os valores da data, mês e ano recebidos do calendário (datePicker)
            val day = datePicker.dayOfMonth
            val month = datePicker.month
            val year = datePicker.year

            // Define um objeto do tipo calendar com esses valores
            val calendar = Calendar.getInstance()
            calendar.set(year, month, day)

            // Ponteiro para o elemento de TimePicker
            val timePicker = findViewById<TimePicker>(R.id.timepicker)

            // Define no objeto Calendar as horas e minutos escolhidos no TimePicker
            calendar.set(Calendar.HOUR_OF_DAY, timePicker.hour)
            calendar.set(Calendar.MINUTE, timePicker.minute)

            // Formata a data como uma string no formato ISO 8601
            val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val formattedTime: String = isoFormat.format(calendar.time)

            // Cria o objeto evento com os dados requeridos pela API, para adicionar
            val evento = CalendarRequest(
                eventName.text.toString(),
                eventDescription.text.toString(),
                formattedTime,
                null,
                spinner.selectedItemPosition + 1
            )
            // Chamada da função que comunica com a API, para adicionar um evento ao Calendário
            addEventToCalendar(evento) { result ->
                if (result != null) {
                    // Evento adicionado com sucesso
                    if (result.code == 201) {
                        Toast.makeText(this, "Evento adicionado com sucesso", Toast.LENGTH_LONG).show()
                        // Adiciona um extra chamado "fragment_to_show" com o valor "pt.ipt.dam2023.neei_ipt.ui.fragment.CalendarViewFragment" ao Intent
                        val intent = Intent(this, MainActivity::class.java)
                        intent.putExtra("fragment_to_show", "CalendarViewFragment")
                        startActivity(intent)
                    }else if (result.code == 200) {
                        // Existe algum erro que não permitiu adicionar o evento (falta de informação)
                        Toast.makeText(this, result.message, Toast.LENGTH_LONG).show()
                    } else {
                        // Erro não identificado / Falha no servidor
                        Toast.makeText(this, "Não foi possível adicionar o evento", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // Erro não identificado / Falha no servidor
                    Toast.makeText(this, "Erro. Contacte o Administrador", Toast.LENGTH_SHORT).show()
                }
            }

        }
    }

    /**
     * Função que obtém a lista de grupos
     */
    private fun getGroups(onResult: (List<Group?>) -> Unit) {
        // Faz a chamada a API
        val call = RetrofitInitializer().APIService().listGroups()
        call.enqueue(object : Callback<List<Group>?> {
            // Tratatamento da resposta bem-sucedida da chamada à API
            override fun onResponse(call: Call<List<Group>?>?, response: Response<List<Group>?>?) {
                response?.body()?.let {
                    // Guarda a lista de grupos recebido da api
                    val groups: List<Group?> = it
                    // Chama a função de retorno com a resposta da API
                    onResult(groups)
                }
            }

            // Tratatamento de falha da chamada à API
            override fun onFailure(call: Call<List<Group>?>, t: Throwable) {
                t?.message?.let { Log.e("Erro onFailure", it) }
            }
        })
    }

    /**
     * Função para adicionar um evento ao calendário
     */
    private fun addEventToCalendar(event: CalendarRequest, onResult: (CalendarResponse?) -> Unit) {
        // Faz a chamada a API
        val call = RetrofitInitializer().APIService().addEvent(event)
        call.enqueue(
            object : Callback<CalendarResponse> {
                // Tratatamento de falha da chamada à API
                override fun onFailure(call: Call<CalendarResponse>?, t: Throwable) {
                    t?.message?.let { Log.e("onFailure error", it) }
                    onResult(null)
                }

                // Tratatamento da resposta bem-sucedida da chamada à API
                override fun onResponse(call: Call<CalendarResponse>, response: Response<CalendarResponse>) {
                    // Guarda o resultado json recebido pela API
                    var result = response.body()
                    // Verifica se o resutado veio a null
                    if (result == null) {
                        result = CalendarResponse("Erro ao adicionar um evento ao Calendário.", response.code())
                    } else {
                        // Guarda na variável result a resposta do servidor
                        result = CalendarResponse(response.body()?.message, response.code())
                    }
                    // Chama a função de retorno com a resposta da API
                    onResult(result)
                }
            }
        )
    }
}

