package pt.ipt.dam2023.neei_ipt.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat.recreate
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

        // Verifica se o Night Mode está ativado
        val isNightModeOn = when (AppCompatDelegate.getDefaultNightMode()) {
            AppCompatDelegate.MODE_NIGHT_YES -> true
            else -> false
        }
        if (isNightModeOn){
            switchDarkMode.isChecked = true
        }
        switchDarkMode.setOnCheckedChangeListener { buttonView, isChecked ->
            //lógica quando o estado do Switch muda
            setNightMode(isChecked)
        }

        return view
    }

    private fun setNightMode(isNightModeOn: Boolean) {
        if (isNightModeOn) {
            // Night Mode
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            // Day Mode
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

}
