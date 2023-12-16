package pt.ipt.dam2023.neei_ipt.ui.activity

import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import pt.ipt.dam2023.neei_ipt.R

class HomeFragment : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_fragment)

        val gradient = findViewById<TextView>(R.id.text_gradient)
        val paint = gradient.paint
        val width = paint.measureText(gradient.text.toString())
        gradient.paint.shader = LinearGradient(
            0f, 0f, width, gradient.textSize, intArrayOf(
                Color.parseColor("#7896f5"),
                Color.parseColor("#4E75EE")
            ), null, Shader.TileMode.REPEAT
        )
    }
}