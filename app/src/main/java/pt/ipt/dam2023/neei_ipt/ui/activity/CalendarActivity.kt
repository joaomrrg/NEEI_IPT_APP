package pt.ipt.dam2023.neei_ipt.ui.activity

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.github.sundeepk.compactcalendarview.CompactCalendarView
import com.github.sundeepk.compactcalendarview.domain.Event
import com.google.android.material.floatingactionbutton.FloatingActionButton
import pt.ipt.dam2023.neei_ipt.R
import pt.ipt.dam2023.neei_ipt.model.CalendarWithColor
import pt.ipt.dam2023.neei_ipt.retrofit.RetrofitInitializer
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.Scanner

class CalendarActivity : AppCompatActivity() {

    // Declaração da variável CompactCalendarView
    private lateinit var compactCalendar: CompactCalendarView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar_neei)

        //Ponteiro para o botao de adicionar evento ao  calendário
        val btnAddEvent = findViewById<FloatingActionButton>(R.id.floatingActionButton)
        btnAddEvent.setOnClickListener {
            val intent = Intent(this, CalendarEvent::class.java)
            startActivity(intent)
        }
        // Leitura da Internal Storage
        val directory: File = filesDir
        val file: File = File(directory, "dados.txt")
        try {
            val fi: FileInputStream = FileInputStream(file)
            val sc: Scanner = Scanner(fi)
            sc.nextLine()
            sc.nextLine()
            sc.nextLine()
            // Guarda a role do user
            val role =  sc.nextLine().toInt()
            //Mostra o botao se for admin
            btnAddEvent.isVisible = role==1 || role==2
            sc.close()
            fi.close()
        } catch (e: FileNotFoundException) {
            Toast.makeText(this, "File not found", Toast.LENGTH_LONG).show()
        }




        // Declaração de uma variável local CompactCalendarView (não utilizada)
        lateinit var compactCalendar: CompactCalendarView

        // Formato de data para exibição do mês e ano
        val dateFormatMonth = SimpleDateFormat("MMMM- yyyy", Locale.getDefault())

        // Efeito de gradiente para o TextView
        val gradient = findViewById<TextView>(R.id.neei_gradient)
        val paint = gradient.paint
        val width = paint.measureText(gradient.text.toString())
        gradient.paint.shader = LinearGradient(
            0f, 0f, width, gradient.textSize, intArrayOf(
                Color.parseColor("#4E75EE"),
                Color.parseColor("#7896f5"),
                Color.parseColor("#FFFFFF")
            ), null, Shader.TileMode.REPEAT
        )

        // Configuração da ActionBar
        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(false)
        actionBar?.title = null

        // Localização do CompactCalendarView no layout
        compactCalendar = findViewById(R.id.calenderView)

        // Adiciona os eventos listados da bd no calendario
        getEvents{ result ->
            if (result != null) {
                if (result.isNotEmpty()){
                    //Lista todos os eventos
                    for (evento in result){
                        // Obtem o int da cor
                        val colorInt = Color.parseColor(evento.color)
                        // Cria um objeto evento
                        val ev1 = Event(colorInt, evento.initialDate.time, evento.description)
                        //Adiciona ao calendario o evento
                        compactCalendar.addEvent(ev1)
                    }
                }else{
                    Log.i("Calendar","Lista vazia")
                }
            }else{
                // Erro não identificado / Falha no servidor
                Toast.makeText(this, "Erro. Contacte o Administrador", Toast.LENGTH_SHORT).show()
            }

        }

        // Definição de um ouvinte para os eventos do CompactCalendarView (onDayClick e onMonthScroll)
        compactCalendar.setListener(object : CompactCalendarView.CompactCalendarViewListener {
            override fun onDayClick(dateClicked: Date) {
                // Manipulação do evento de clique no dia com uma mensagem Toast
                val context: Context = applicationContext
                if (dateClicked.toString() == "Tue Dec 19 02:18:23 GMT 2023") {
                    Toast.makeText(context, "Dia do Profissional de Educação", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Sem eventos planejados para este dia", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onMonthScroll(firstDayOfNewMonth: Date) {
                // Definição do título da ActionBar para o novo mês e ano
                actionBar?.title = dateFormatMonth.format(firstDayOfNewMonth)
            }
        })

        // Localização dos TextViews para exibir o ano e o mês
        val yearTextView: TextView = findViewById(R.id.year)
        val monthTextView: TextView = findViewById(R.id.month)

        // Definição de um ouvinte para os eventos do CompactCalendarView (onMonthScroll)
        compactCalendar.setListener(object : CompactCalendarView.CompactCalendarViewListener {
            override fun onDayClick(dateClicked: Date?) {
                // Lidar com o clique no dia, se necessário

            }

            override fun onMonthScroll(firstDayOfNewMonth: Date?) {
                // Atualização dos TextViews para ano e mês quando o mês do calendário muda
                firstDayOfNewMonth?.let {
                    val calendar = Calendar.getInstance()
                    calendar.time = it

                    // Extraindo o ano e o mês
                    val year = calendar.get(Calendar.YEAR)
                    val month = calendar.get(Calendar.MONTH) + 1 // Os meses são indexados a partir de zero
                    yearTextView.text = year.toString()
                    monthTextView.text = SimpleDateFormat("MMMM", Locale.getDefault()).format(it)
                }
            }
        })
    }

    private fun getEvents(onResult: (List<CalendarWithColor>?) -> Unit){
        val call = RetrofitInitializer().APIService().listEvents()
        call.enqueue(object : Callback<List<CalendarWithColor>?> {
            override fun onResponse(call: Call<List<CalendarWithColor>?>?,
                                    response: Response<List<CalendarWithColor>?>?) {
                response?.body()?.let {
                    val events: List<CalendarWithColor> = it
                    onResult(events)
                }
            }

            override fun onFailure(call: Call<List<CalendarWithColor>?>, t: Throwable) {
                t?.message?.let { Log.e("onFailure error", it) }
            }
        })
    }
}
