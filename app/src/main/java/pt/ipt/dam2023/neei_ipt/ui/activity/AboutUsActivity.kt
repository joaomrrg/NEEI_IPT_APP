package pt.ipt.dam2023.neei_ipt.ui.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import pt.ipt.dam2023.neei_ipt.R

class AboutUsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_us)

        //Ponteiro para as imageViews dos developers
        val imageGoncalo = findViewById<ImageView>(R.id.imageGoncalo)
        val imageJoao = findViewById<ImageView>(R.id.imageJoao)

        //Ponteiro para os linear Layout dos developers
        val llGoncalo = findViewById<LinearLayout>(R.id.llGoncalo)
        val llJoao = findViewById<LinearLayout>(R.id.llJoao)

        // Carrega uma imagem por URl no ImageView (Goncalo)
        Glide.with(this)
            .load("https://avatars.githubusercontent.com/u/55167343")
            .apply(RequestOptions.bitmapTransform(CircleCrop()))
            .into(imageGoncalo)

        // Carrega uma imagem por URl no ImageView (Joao)
        Glide.with(this)
            .load("https://avatars.githubusercontent.com/u/120472343")
            .apply(RequestOptions.bitmapTransform(CircleCrop()))
            .into(imageJoao)

        // Evento de clique no João para ir para a sua página de GitHub
        llJoao.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.githubJoao)))
            startActivity(intent)
        }

        // Evento de clique no Gonçalo para ir para a sua página de GitHub
        llGoncalo.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.githubGoncalo)))
            startActivity(intent)
        }

    }
}