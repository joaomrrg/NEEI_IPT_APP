package pt.ipt.dam2023.neei_ipt

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class Login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val registarLabel = findViewById<TextView>(R.id.registar_label)
        val recuperarLabel = findViewById<TextView>(R.id.recuperar_label)
        val loginButton = findViewById<Button>(R.id.button_login)

        registarLabel.setOnClickListener{
            val intent = Intent(this, Register::class.java)
            startActivity(intent)
        }

        recuperarLabel.setOnClickListener{
            val intent = Intent(this, RecuperarPass::class.java)
            startActivity(intent)
        }

        loginButton.setOnClickListener{
            val intent = Intent(this, Main::class.java)
            startActivity(intent)
        }

    }



}
