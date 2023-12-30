import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import pt.ipt.dam2023.neei_ipt.R

class BalanceFragment : Fragment() {

    // Saldo atual simulado
    private val currentBalance = "1003.54"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Infla o layout para este fragmento
        val view = inflater.inflate(R.layout.balance_layout, container, false)

        // Encontra as views no layout inflado
        val currentBalanceText = view.findViewById<TextView>(R.id.currentBalanceText)
        val hideButton = view.findViewById<ImageView>(R.id.hideBttn)
        val addMoneyButton = view.findViewById<FloatingActionButton>(R.id.addMoneyBttn)
        val addMovementButton = view.findViewById<FloatingActionButton>(R.id.addMovementBttn)
        val checkMovementButton = view.findViewById<FloatingActionButton>(R.id.checkMovementBttn)
        val euroText = view.findViewById<TextView>(R.id.euro)

        // Define o saldo atual no TextView correspondente
        currentBalanceText.text = currentBalance

        // Define o comportamento do botão de ocultar/mostrar saldo
        hideButton.setOnClickListener {
            if (currentBalanceText.text == currentBalance) {
                currentBalanceText.text = "-"
                euroText.isVisible = false
            } else {
                currentBalanceText.text = currentBalance
                euroText.isVisible = true
            }
        }

        // Define o comportamento do botão de adicionar dinheiro
        addMoneyButton.setOnClickListener {

        }

        // Define o comportamento do botão de adicionar movimento
        addMovementButton.setOnClickListener {

        }

        // Define o comportamento do botão de verificar movimentos
        checkMovementButton.setOnClickListener {

        }

        return view
    }
}
