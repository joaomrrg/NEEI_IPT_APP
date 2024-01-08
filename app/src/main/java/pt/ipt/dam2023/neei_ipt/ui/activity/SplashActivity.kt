package pt.ipt.dam2023.neei_ipt.ui.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import pt.ipt.dam2023.neei_ipt.R


class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash)

        // Oculta a ActionBar durante a execução da SplashActivity
        supportActionBar?.hide()

        // Utiliza um Handler para atrasar a execução das próximas ações por 2000 milissegundos (2 segundos)
        Handler().postDelayed({
            val intent = Intent(this@SplashActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }, 2000)

    }
}