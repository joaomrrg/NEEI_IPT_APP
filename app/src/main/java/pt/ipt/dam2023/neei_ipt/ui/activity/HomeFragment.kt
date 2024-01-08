
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.os.Handler
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import pt.ipt.dam2023.neei_ipt.R
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Scanner

class HomeFragment : Fragment() {
    private var cursorVisible = false
    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Infla o layout para este fragmento
        val view = inflater.inflate(R.layout.activity_home_fragment, container, false)

        val directory: File = requireContext().filesDir
        val file: File = File(directory, "dados.txt")
        var newName = ""
        try {
            val fi: FileInputStream = FileInputStream(file)
            val sc: Scanner = Scanner(fi)
            // Atribui ao elemento usernameText o username guardado no internal storage
            newName = sc.nextLine()
        } catch (e: FileNotFoundException) {
            Toast.makeText(requireContext(), "File not found", Toast.LENGTH_LONG).show()
        }

        val home = view.findViewById<RelativeLayout>(R.id.home_layout)
        val spannabletext = view.findViewById<TextView>(R.id.spantext)
        val text = "guest@neei:/home/guest$"
        val modifiedText = text.replace("guest", newName)
        val spannableString = SpannableString(modifiedText)
        val azul = ContextCompat.getColor(requireContext(), R.color.blue)

        val animDrawable = home.background as AnimationDrawable
        animDrawable.setEnterFadeDuration(10)
        animDrawable.setExitFadeDuration(5000)
        animDrawable.start()

        // Exemplo de manipulação de um TextView com um gradiente
        val gradient = view.findViewById<TextView>(R.id.text_gradient)
        val paint = gradient.paint
        val width = paint.measureText(gradient.text.toString())
        gradient.paint.shader = LinearGradient(
            0f, 0f, width, gradient.textSize, intArrayOf(
                Color.parseColor("#4E75EE"),
                Color.parseColor("#7896f5"),
                Color.parseColor("#FFFFFF")
            ), null, Shader.TileMode.REPEAT
        )

        val colorSpan = ForegroundColorSpan(azul)
        val boldSpan = StyleSpan(android.graphics.Typeface.BOLD)
        spannableString.setSpan(colorSpan, 0, newName.length+5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableString.setSpan(boldSpan, 0, newName.length+5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        val restColorSpan = ForegroundColorSpan(Color.WHITE) // Change to your desired color
        spannableString.setSpan(restColorSpan, newName.length+5, modifiedText.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannabletext.text = spannableString

        // Set EditText focus and show keyboard
        val editText = view.findViewById<EditText>(R.id.command_input)
        editText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val command = editText.text.toString().trim()
                handleCommand(command, view)
                editText.text.clear()  // Limpar o texto após processar o comando
                return@setOnEditorActionListener true
            }
            false
        }
        editText.requestFocus()
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)

        return view
    }
    // Função para manipular comandos
    private fun handleCommand(command: String,view: View) {
        val response: String = when (command.toLowerCase()) {
            "hello" -> "Olá! Como estás?\n"
            "date" -> "Hoje é dia: "+SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())+"\n"
            "ls" -> "Não há nada a ver aqui!\n"
            "cd" -> "Estás no Núcleo de Estudantes Engenharia Informática\n"
            "clear" -> "clear"
            else -> "$$command: Comando não reconhecido. Tenta outro.\n"
        }

        // Exiba a resposta no seu TextView ou faça o que for necessário
        val outputTextView = view.findViewById<TextView>(R.id.output_text_view)
        if (response=="clear"){
            outputTextView.text=""
        }else{
            outputTextView.append(response)
        }
        val inputEditText = view.findViewById<EditText>(R.id.command_input)
        inputEditText.text.clear()
    }
}