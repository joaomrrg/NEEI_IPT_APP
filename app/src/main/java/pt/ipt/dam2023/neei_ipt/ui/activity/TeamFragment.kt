
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import pt.ipt.dam2023.neei_ipt.R

class TeamFragment : Fragment() {

    private val goncalo = "https://avatars.githubusercontent.com/u/55167343"
    private val joao = "https://avatars.githubusercontent.com/u/120472343"
    private val barbosa = "https://avatars.githubusercontent.com/u/101589640"
    private val tiago = "https://avatars.githubusercontent.com/u/86069519"
    private val guilherme = ""
    private val filipe = "https://avatars.githubusercontent.com/u/152807332"
    private val miguel = "https://avatars.githubusercontent.com/u/101656773"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Infla o layout para este fragmento
        val view = inflater.inflate(R.layout.team_layout, container, false)

        // Encontra as views no layout inflado
        val imageJoao = view.findViewById<ImageView>(R.id.imageJoao)
        val imageGoncalo = view.findViewById<ImageView>(R.id.imageGoncalo)
        val anotherGoncalo1 = view.findViewById<ImageView>(R.id.anotherGoncalo1)
        val imageFilipe =view.findViewById<ImageView>(R.id.imageFilipe)
        val anotherFilipe1 =view.findViewById<ImageView>(R.id.anotherFilipe1)
        val anotherFilipe2 =view.findViewById<ImageView>(R.id.anotherFilipe2)
        val imageTiago =view.findViewById<ImageView>(R.id.imageTiago)
        val anotherTiago1 =view.findViewById<ImageView>(R.id.anotherTiago1)
        val imageBarbosa =view.findViewById<ImageView>(R.id.imageBarbosa)
        val anotherBarbosa1 =view.findViewById<ImageView>(R.id.anotherBarbosa1)
        val imageMiguel =view.findViewById<ImageView>(R.id.imageMiguel)
        val anotherMiguel1 =view.findViewById<ImageView>(R.id.anotherMiguel1)
        val inJoao = view.findViewById<ImageView>(R.id.inJoao)
        val inGoncalo = view.findViewById<ImageView>(R.id.inGoncalo)
        val inBarbosa = view.findViewById<ImageView>(R.id.inBarbosa)
        val inMiguel = view.findViewById<ImageView>(R.id.inMiguel)
        val inTiago = view.findViewById<ImageView>(R.id.inTiago)
        val inFilipe = view.findViewById<ImageView>(R.id.inFilipe)
        val inGuilherme = view.findViewById<ImageView>(R.id.inGuilherme)


        loadImageView(joao, imageJoao)
        loadImageView(goncalo, imageGoncalo)
        loadImageView(goncalo, anotherGoncalo1)
        loadImageView(filipe, imageFilipe)
        loadImageView(filipe, anotherFilipe1)
        loadImageView(filipe, anotherFilipe2)
        loadImageView(tiago, imageTiago)
        loadImageView(tiago, anotherTiago1)
        loadImageView(barbosa, imageBarbosa)
        loadImageView(barbosa, anotherBarbosa1)
        loadImageView(miguel, imageMiguel)
        loadImageView(miguel, anotherMiguel1)

        inJoao.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.githubJoao)))
            startActivity(intent)
        }

        inMiguel.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.githubMiguel)))
            startActivity(intent)
        }

        inTiago.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.githubTiago)))
            startActivity(intent)
        }

        inBarbosa.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.githubBarbosa)))
            startActivity(intent)
        }

        inFilipe.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.githubFilipe)))
            startActivity(intent)
        }

        inGoncalo.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.githubGoncalo)))
            startActivity(intent)
        }

        inGuilherme.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.githubJoao)))
            startActivity(intent)
        }


        return view
    }

    fun loadImageView(url: String, image: ImageView){
        // Carrega as imagens nos ImageViews usando Glide
        Glide.with(this)
            .load(url)
            .apply(RequestOptions.bitmapTransform(CircleCrop()))
            .into(image)

    }
}
