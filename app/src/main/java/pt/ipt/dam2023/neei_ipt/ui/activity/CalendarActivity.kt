package pt.ipt.dam2023.neei_ipt.ui.activity

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.github.sundeepk.compactcalendarview.CompactCalendarView
import com.github.sundeepk.compactcalendarview.domain.Event
import com.google.android.material.floatingactionbutton.FloatingActionButton
import pt.ipt.dam2023.neei_ipt.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class CalendarActivity : AppCompatActivity() {

    // Declaração da variável CompactCalendarView
    private lateinit var compactCalendar: CompactCalendarView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar_neei)

        val buttonzinho = findViewById<FloatingActionButton>(R.id.floatingActionButton)

        buttonzinho.setOnClickListener {
            val intent = Intent(this, CalendarEvent::class.java)
            startActivity(intent)
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

        // Adição de um evento ao calendário (Dia do Profissional de Educação)
        val ev1 = Event(Color.RED, 1702952303000L, "Dia do Profissional de Educação")
        compactCalendar.addEvent(ev1)

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

        // Localização do CompactCalendarView novamente (não é necessário, já encontrado acima)
        val compactCalendarView: CompactCalendarView = findViewById(R.id.calenderView)

        // Localização dos TextViews para exibir o ano e o mês
        val yearTextView: TextView = findViewById(R.id.year)
        val monthTextView: TextView = findViewById(R.id.month)

        // Definição de um ouvinte para os eventos do CompactCalendarView (onMonthScroll)
        compactCalendarView.setListener(object : CompactCalendarView.CompactCalendarViewListener {
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
}
