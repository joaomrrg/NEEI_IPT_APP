package pt.ipt.dam2023.neei_ipt.ui.fragment
import android.content.Intent
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
import com.github.sundeepk.compactcalendarview.domain.Event
import com.google.android.material.floatingactionbutton.FloatingActionButton
import pt.ipt.dam2023.neei_ipt.R
import pt.ipt.dam2023.neei_ipt.model.CalendarWithColor
import pt.ipt.dam2023.neei_ipt.retrofit.RetrofitInitializer
import pt.ipt.dam2023.neei_ipt.ui.activity.CalendarAddEventActivity
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
    // Declaração de uma variável CompactCalendarView
    private lateinit var compactCalendar: CompactCalendarView
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Infla (hierarquiza) o layout para este fragmento
        val view = inflater.inflate(R.layout.activity_calendar_neei, container, false)
        // Ponteiro do botão de adicionar da View
        val btnAddEvent = view.findViewById<FloatingActionButton>(R.id.floatingActionButton)

        // Evento de Mouse Click no botão Adicionar Evento
        btnAddEvent.setOnClickListener {
            // Cria uma Intent para iniciar o CalendarAddEventActivity
            val intent = Intent(requireContext(), CalendarAddEventActivity::class.java)
            startActivity(intent)
        }
        // Guarda numa variável o objeto referido por um ponteiro da view
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
            // Guarda a role do user em variável
            val role =  sc.nextLine().toInt()
            // Mostra o botao de adicionar se for admin
            btnAddEvent.isVisible = role==1
            sc.close()
            fi.close()
        } catch (e: FileNotFoundException) {
            Toast.makeText(requireContext(), "File not found", Toast.LENGTH_LONG).show()
        }

        // Manipulação de um TextView com um gradient
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

        // Array estático com os dias da semana abreviados em portugues
        val localizedDayInitials = arrayOf("DOM", "SEG", "TER", "QUA", "QUI", "SEX", "SÁB")
        // Array estático com os meses do ano escritos em portugues
        val localizedMonthNames = arrayOf(
            "Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho",
            "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"
        )

        // Configuração da ActionBar
        val actionBar = (requireActivity() as AppCompatActivity?)?.supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(false)
        actionBar?.title = null

        // Ponteiro do objeto CompactCalendarView no layout
        compactCalendar = view.findViewById(R.id.calenderView)
        val ptTimeZone = TimeZone.getTimeZone("Europe/Lisbon")
        // Configuração do Calendário
        compactCalendar.setLocale(ptTimeZone, Locale("pt", "PT"))
        compactCalendar.setFirstDayOfWeek(Calendar.SUNDAY)
        compactCalendar.setDayColumnNames(localizedDayInitials)
        compactCalendar.setCurrentDate(Calendar.getInstance().time)

        // Ponteiros dos elementos TextViews para exibir o ano e o mês
        val yearTextView: TextView = view.findViewById(R.id.year)
        val monthTextView: TextView = view.findViewById(R.id.month)
        monthTextView.text = localizedMonthNames[Calendar.getInstance().get(Calendar.MONTH)]
        yearTextView.text = Calendar.getInstance().get(Calendar.YEAR).toString()

        // Definição de um litstener para os eventos do CompactCalendarView (onMonthScroll)
        compactCalendar.setListener(object : CompactCalendarView.CompactCalendarViewListener {
            override fun onDayClick(dateClicked: Date?) {
            }

            // Ao dar scroll nos meses
            override fun onMonthScroll(firstDayOfNewMonth: Date?) {
                firstDayOfNewMonth?.let {
                    val calendar = Calendar.getInstance()
                    calendar.time = it

                    // Extrai o ano e o mês selecionado
                    val year = calendar.get(Calendar.YEAR)
                    val month = calendar.get(Calendar.MONTH)

                    // Mostra o mes e o ano selecionado
                    monthTextView.text = localizedMonthNames[month]
                    yearTextView.text = year.toString()
                }
            }
        })

        // Chamada da função que comunica com a API, para obter a lista de eventos
        getEvents { result ->
            if (result != null) {
                val calendarList = result.filterNotNull() // Filtrar documentos nulos
                // Inicializar a ListView e configurar o adaptador
                val adapter = CalendarItemAdapter(requireContext(), R.layout.item_calendar, calendarList)
                listView.adapter = adapter
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
            }
        }

        return view
    }

    /**
     * Função para obter eventos
     */
    private fun getEvents(onResult: (List<CalendarWithColor>?) -> Unit) {
        // Faz a chamada a API
        val call = RetrofitInitializer().APIService().listEvents()
        call.enqueue(object : Callback<List<CalendarWithColor>?> {
            // Tratamento da resposta bem-sucedida da chamada à API
            override fun onResponse(call: Call<List<CalendarWithColor>?>?, response: Response<List<CalendarWithColor>?>?) {
                response?.body()?.let {
                    // Guarda numa variável a lista de eventos recebida pela API
                    val events: List<CalendarWithColor> = it
                    onResult(events) // Chama a função de retorno com a resposta da API
                }
            }
            // Tratamento de falha da chamada à API
            override fun onFailure(call: Call<List<CalendarWithColor>?>, t: Throwable) {
                t?.message?.let { Log.e("onFailure error", it) }
            }
        })
    }
}
