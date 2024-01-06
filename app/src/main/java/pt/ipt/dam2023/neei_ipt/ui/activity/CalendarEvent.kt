package pt.ipt.dam2023.neei_ipt.ui.activity

import CalendarViewFragment
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CalendarView
import android.widget.DatePicker
import android.widget.Spinner
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.github.sundeepk.compactcalendarview.domain.Event
import pt.ipt.dam2023.neei_ipt.R
import pt.ipt.dam2023.neei_ipt.model.AuthRequest
import pt.ipt.dam2023.neei_ipt.model.AuthResponse
import pt.ipt.dam2023.neei_ipt.model.CalendarRequest
import pt.ipt.dam2023.neei_ipt.model.CalendarResponse
import pt.ipt.dam2023.neei_ipt.model.Group
import pt.ipt.dam2023.neei_ipt.model.User
import pt.ipt.dam2023.neei_ipt.retrofit.RetrofitInitializer
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.PrintStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class CalendarEvent : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_event_neei)

        // Inicialização dos elementos de interface
        val addButton = findViewById<Button>(R.id.addbutton)
        val datePicker = findViewById<DatePicker>(R.id.datepicker)
        val eventName = findViewById<TextView>(R.id.nomeevento)
        val eventDescription = findViewById<TextView>(R.id.descricaoevento)

        val spinner = findViewById<Spinner>(R.id.spinner)
        getGroups { result ->
            if (result != null) {
                if (result.isNotEmpty()) {
                    //Lista todos os eventos
                    for (it in result) {
                        // Lista todos os eventos
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

        // Ações ao carregar em 'Adicionar'
        addButton.setOnClickListener {
            val day = datePicker.dayOfMonth
            val month = datePicker.month
            val year = datePicker.year

            val calendar = Calendar.getInstance()
            calendar.set(year, month, day)

            val timePicker = findViewById<TimePicker>(R.id.timepicker)

            calendar.set(Calendar.HOUR_OF_DAY, timePicker.hour)
            calendar.set(Calendar.MINUTE, timePicker.minute)
            // horário escolhido como um objeto Date
            val chosenTime: Date = calendar.time

            // Formatar a data como uma string no formato ISO 8601
            val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val formattedTime: String = isoFormat.format(calendar.time)

            // Adicionar evento ao calendário
            val evento = CalendarRequest(
                eventName.text.toString(),
                eventDescription.text.toString(),
                formattedTime,
                null,
                spinner.selectedItemPosition + 1
            )
            addEventToCalendar(evento) { result ->
                if (result != null) {
                    // Evento adicionado com sucesso
                    if (result.code == 201) {
                        Toast.makeText(this, "Evento adicionado com sucesso", Toast.LENGTH_LONG)
                            .show()
                        val intent = Intent(this, MainActivity::class.java)
                        // Adicionando um extra chamado "fragment_to_show" com o valor "DocumentFragment" ao Intent
                        intent.putExtra("fragment_to_show", "CalendarViewFragment")
                        startActivity(intent)
                    } else {
                        Toast.makeText(
                            this,
                            "Não foi possível adicionar evento",
                            Toast.LENGTH_SHORT
                        ).show()

                    }
                } else {
                    // Erro não identificado / Falha no servidor
                    Toast.makeText(this, "Erro. Contacte o Administrador", Toast.LENGTH_SHORT)
                        .show()
                }
            }

        }
    }

    /**
     * Obtem lista de grupos
     */
    private fun getGroups(onResult: (List<Group?>) -> Unit) {
        val call = RetrofitInitializer().APIService().listGroups()
        call.enqueue(object : Callback<List<Group>?> {
            override fun onResponse(call: Call<List<Group>?>?, response: Response<List<Group>?>?) {
                response?.body()?.let {
                    val groups: List<Group?> = it
                    onResult(groups)
                }
            }

            override fun onFailure(call: Call<List<Group>?>, t: Throwable) {
                t?.message?.let { Log.e("Erro onFailure", it) }
            }
        })
    }

    /**
     * Função para adicionar um evento ao calenddário
     */
    private fun addEventToCalendar(event: CalendarRequest, onResult: (CalendarResponse?) -> Unit) {
        // Faz a chamada a API
        val call = RetrofitInitializer().APIService().addEvent(event)
        call.enqueue(
            object : Callback<CalendarResponse> {
                // Escreve uma log sobre o erro
                override fun onFailure(call: Call<CalendarResponse>?, t: Throwable) {
                    t?.message?.let { Log.e("onFailure error", it) }
                    onResult(null)
                }

                // Recebe a response
                override fun onResponse(
                    call: Call<CalendarResponse>,
                    response: Response<CalendarResponse>
                ) {
                    // Guarda o resultado json
                    var result = response.body()
                    // Verificar os resultados
                    if (result == null) {
                        result = CalendarResponse("", response.code())
                    } else {
                        result = CalendarResponse(response.message(), response.code())
                    }
                    onResult(result)
                }
            }
        )
    }
}

