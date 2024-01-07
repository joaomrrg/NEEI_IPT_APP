import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ListView
import android.widget.Switch
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import pt.ipt.dam2023.neei_ipt.R
import pt.ipt.dam2023.neei_ipt.model.Document
import pt.ipt.dam2023.neei_ipt.model.Transaction
import pt.ipt.dam2023.neei_ipt.model.TransactionBudget
import pt.ipt.dam2023.neei_ipt.model.User
import pt.ipt.dam2023.neei_ipt.retrofit.RetrofitInitializer
import pt.ipt.dam2023.neei_ipt.ui.activity.DocumentAddActivity
import pt.ipt.dam2023.neei_ipt.ui.activity.TransactionAddActivity
import pt.ipt.dam2023.neei_ipt.ui.adapter.DocumentAdapter
import pt.ipt.dam2023.neei_ipt.ui.adapter.TransactionAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SettingsFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Infla o layout para este fragmento
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        val switchDarkMode = view.findViewById<Switch>(R.id.switchDarkMode)

        switchDarkMode.setOnCheckedChangeListener { buttonView, isChecked ->
            // Aqui podes executar a lógica quando o estado do Switch muda
            if (isChecked) {
                // Switch está ligado (modo escuro ativado)
                // Executar a lógica para o modo escuro
            } else {
                // Switch está desligado (modo escuro desativado)
                // Executar a lógica para o modo claro
            }
        }

        return view
    }
}
