package pt.ipt.dam2023.neei_ipt

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.content.Intent


class Splash : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash)

        supportActionBar?.hide()

        Handler().postDelayed({
            val intent = Intent(this@Splash, Login::class.java)
            startActivity(intent)
            finish() // Optional, depending on whether you want to finish the splash screen activity or not
        }, 2000)


    }
}