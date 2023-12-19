package pt.ipt.dam2023.neei_ipt.ui.activity

import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.DatePicker
import android.widget.Spinner
import android.widget.TextView
import android.widget.TimePicker
import androidx.appcompat.app.AppCompatActivity
import pt.ipt.dam2023.neei_ipt.R
import pt.ipt.dam2023.neei_ipt.model.User
import pt.ipt.dam2023.neei_ipt.retrofit.RetrofitInitializer
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
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
        val spinner = findViewById<Spinner>(R.id.spinner)

        // Configuração do adaptador do Spinner com as opções
        val options = arrayOf("Opção 1", "Opção 2", "Opção 3") //-------!!listGroups()!!--------elementos da dropdown
        val adapter = ArrayAdapter(this, R.layout.custom_spinner, options)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        // Ações ao carregar em 'Adicionar'
        addButton.setOnClickListener {
            val day = datePicker.dayOfMonth
            val month = datePicker.month
            val year = datePicker.year

            val calendar = Calendar.getInstance()
            calendar.set(year, month, day)

            // Data selecionada em milissegundos
            val selectedDateInMillis = calendar.timeInMillis

            val timePicker = findViewById<TimePicker>(R.id.timepicker)

            timePicker.setOnTimeChangedListener { _, hourOfDay, minute ->
                val calendar = Calendar.getInstance()
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)

                // horário escolhido como um objeto Date
                val chosenTime: Date = calendar.time

                //horário em string conforme necessário "hh:mm a"
                val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                val formattedTime: String = timeFormat.format(chosenTime)
            }
        }
    }

    // listGroups pááááááá
    private fun getGroups() {
        val call = RetrofitInitializer().APIService().listUsers()
        call.enqueue(object : Callback<List<User>?> {
            override fun onResponse(call: Call<List<User>?>?, response: Response<List<User>?>?) {
                response?.body()?.let {
                    val users: List<User> = it
                    println(users)
                }
            }

            override fun onFailure(call: Call<List<User>?>, t: Throwable) {
                t?.message?.let { Log.e("Erro onFailure", it) }
            }
        })
    }
}
