import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import pt.ipt.dam2023.neei_ipt.R
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import androidx.core.content.ContextCompat
import android.text.style.StyleSpan
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getSystemService
import android.content.Context
import android.os.Handler

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

        val home = view.findViewById<LinearLayout>(R.id.home_layout)
        val spannabletext = view.findViewById<TextView>(R.id.spantext)
        val text = "guest@neei:/home/guest$"
        val spannableString = SpannableString(text)
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
        spannableString.setSpan(colorSpan, 0, 10, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableString.setSpan(boldSpan, 0, 10, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        val restColorSpan = ForegroundColorSpan(Color.WHITE) // Change to your desired color
        spannableString.setSpan(restColorSpan, 10, text.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        spannabletext.text = spannableString


        val textCursor = view.findViewById<TextView>(R.id.text_cursor)
        textCursor.visibility = View.INVISIBLE
        // Handler for cursor blinking effect
        val handler = Handler()
        handler.postDelayed(object : Runnable {
            override fun run() {
                cursorVisible = !cursorVisible
                textCursor.visibility = if (cursorVisible) View.VISIBLE else View.INVISIBLE
                handler.postDelayed(this, 500) // Adjust blinking speed here (500ms for example)
            }
        }, 500)

        // Set EditText focus and show keyboard
        val editText = view.findViewById<EditText>(R.id.command_input)
        editText.requestFocus()
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)

        return view
    }
}