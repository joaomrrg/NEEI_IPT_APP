package pt.ipt.dam2023.neei_ipt.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import pt.ipt.dam2023.neei_ipt.R

class SettingsViewFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Infla o layout para este fragmento
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        val switchDarkMode = view.findViewById<Switch>(R.id.switchDarkMode)

        switchDarkMode.setOnCheckedChangeListener { buttonView, isChecked ->
            //lógica quando o estado do Switch muda
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        return view
    }
}
