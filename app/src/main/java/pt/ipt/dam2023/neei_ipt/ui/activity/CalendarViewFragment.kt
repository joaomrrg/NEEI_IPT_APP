
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.github.sundeepk.compactcalendarview.CompactCalendarView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import pt.ipt.dam2023.neei_ipt.R
import pt.ipt.dam2023.neei_ipt.model.CalendarWithColor
import pt.ipt.dam2023.neei_ipt.retrofit.RetrofitInitializer
import pt.ipt.dam2023.neei_ipt.ui.adapter.CalendarItemAdapter
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
import java.util.TimeZone

class CalendarViewFragment : Fragment() {

    private lateinit var compactCalendar: CompactCalendarView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_calendar_neei, container, false)

        val btnAddEvent = view.findViewById<FloatingActionButton>(R.id.floatingActionButton)
        val listView: ListView = view.findViewById(R.id.listViewCalendar)
        // Leitura da Internal Storage
        val directory: File = requireContext().filesDir
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
            btnAddEvent.isVisible = role==1
            sc.close()
            fi.close()
        } catch (e: FileNotFoundException) {
            Toast.makeText(requireContext(), "File not found", Toast.LENGTH_LONG).show()
        }

        // Declaração de uma variável local CompactCalendarView (não utilizada)
        lateinit var compactCalendar: CompactCalendarView

        // Formato de data para exibição do mês e ano
        val dateFormatMonth = SimpleDateFormat("MMMM- yyyy", Locale("pt", "PT"))

        //manipulação de um TextView com um gradiente
        val gradient = view.findViewById<TextView>(R.id.neei_gradient)
        val paint = gradient.paint
        val width = paint.measureText(gradient.text.toString())
        gradient.paint.shader = LinearGradient(
            0f, 0f, width, gradient.textSize, intArrayOf(
                Color.parseColor("#4E75EE"),
                Color.parseColor("#7896f5"),
                Color.parseColor("#FFFFFF")
            ), null, Shader.TileMode.REPEAT
        )


        val localizedDayInitials = arrayOf("DOM", "SEG", "TER", "QUA", "QUI", "SEX", "SÁB")
        val localizedMonthNames = arrayOf(
            "Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho",
            "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"
        )

        // Configuração da ActionBar
        val actionBar = (requireActivity() as AppCompatActivity?)?.supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(false)
        actionBar?.title = null

        // Localização do CompactCalendarView no layout
        compactCalendar = view.findViewById(R.id.calenderView)
        val ptTimeZone = TimeZone.getTimeZone("Europe/Lisbon")
        compactCalendar.setLocale(ptTimeZone, Locale("pt", "PT"))
        compactCalendar.setFirstDayOfWeek(Calendar.SUNDAY)
        compactCalendar.setDayColumnNames(localizedDayInitials)
        compactCalendar.setCurrentDate(Calendar.getInstance().time)

        // Localização dos TextViews para exibir o ano e o mês
        val yearTextView: TextView = view.findViewById(R.id.year)
        val monthTextView: TextView = view.findViewById(R.id.month)
        monthTextView.text = localizedMonthNames[Calendar.getInstance().get(Calendar.MONTH)]
        yearTextView.text = Calendar.getInstance().get(Calendar.YEAR).toString()

        // Definição de um ouvinte para os eventos do CompactCalendarView (onMonthScroll)
        compactCalendar.setListener(object : CompactCalendarView.CompactCalendarViewListener {
            override fun onDayClick(dateClicked: Date?) {
                // Lidar com o clique no dia, se necessário

            }

            // Inside onMonthScroll callback
            override fun onMonthScroll(firstDayOfNewMonth: Date?) {
                firstDayOfNewMonth?.let {
                    val calendar = Calendar.getInstance()
                    calendar.time = it

                    // Exctract year and month
                    val year = calendar.get(Calendar.YEAR)
                    val month = calendar.get(Calendar.MONTH)
                    val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1

                    // Set localized month name
                    monthTextView.text = localizedMonthNames[month]
                    yearTextView.text = year.toString()
                }
            }
        })

        getEvents { result ->
            if (result != null) {
                val calendarList = result.filterNotNull() // Filtrar documentos nulos
                // Inicializar a ListView e configurar o adaptador
                val adapter = CalendarItemAdapter(requireContext(), R.layout.item_calendar, calendarList)
                listView.adapter = adapter
            }
        }

        return view
    }

    private fun getEvents(onResult: (List<CalendarWithColor>?) -> Unit) {
        val call = RetrofitInitializer().APIService().listEvents()
        call.enqueue(object : Callback<List<CalendarWithColor>?> {
            override fun onResponse(
                call: Call<List<CalendarWithColor>?>?,
                response: Response<List<CalendarWithColor>?>?
            ) {
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
