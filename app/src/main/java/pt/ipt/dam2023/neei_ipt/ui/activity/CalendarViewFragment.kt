import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

class CalendarViewFragment : Fragment() {

    private lateinit var compactCalendar: CompactCalendarView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_calendar_neei, container, false)

        val btnAddEvent = view.findViewById<FloatingActionButton>(R.id.floatingActionButton)

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
            btnAddEvent.isVisible = role==1 || role==2
            sc.close()
            fi.close()
        } catch (e: FileNotFoundException) {
            Toast.makeText(requireContext(), "File not found", Toast.LENGTH_LONG).show()
        }

        // Declaração de uma variável local CompactCalendarView (não utilizada)
        lateinit var compactCalendar: CompactCalendarView

        // Formato de data para exibição do mês e ano
        val dateFormatMonth = SimpleDateFormat("MMMM- yyyy", Locale.getDefault())

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

        // Configuração da ActionBar
        val actionBar = (requireActivity() as AppCompatActivity?)?.supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(false)
        actionBar?.title = null

        // Localização do CompactCalendarView no layout
        compactCalendar = view.findViewById(R.id.calenderView)


        // Localização dos TextViews para exibir o ano e o mês
        val yearTextView: TextView = view.findViewById(R.id.year)
        val monthTextView: TextView = view.findViewById(R.id.month)

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