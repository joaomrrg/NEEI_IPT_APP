package pt.ipt.dam2023.neei_ipt.ui.fragment
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

class TeamViewFragment : Fragment() {
    // Variáveis com as imagens do github dos membros do NEEI
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
        // Infla (hierarquia) o layout para este fragmento
        val view = inflater.inflate(R.layout.team_layout, container, false)

        // Ponteiros de elementos da View
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
        val gitBarbosa = view.findViewById<ImageView>(R.id.gitBarbosa)
        val gitMiguel = view.findViewById<ImageView>(R.id.gitMiguel)
        val inTiago = view.findViewById<ImageView>(R.id.inTiago)
        val gitFilipe = view.findViewById<ImageView>(R.id.gitFilipe)
        val gitGuilherme = view.findViewById<ImageView>(R.id.gitGuilherme)
        val gitJoao = view.findViewById<ImageView>(R.id.gitJoao)
        val gitGoncalo = view.findViewById<ImageView>(R.id.gitGoncalo)
        val gitTiago = view.findViewById<ImageView>(R.id.gitTiago)
        val instaGuilherme = view.findViewById<ImageView>(R.id.instaGuilherme)
        val instaFilipe = view.findViewById<ImageView>(R.id.instaFilipe)
        val instaMiguel = view.findViewById<ImageView>(R.id.instaMiguel)
        val instaBarbosa = view.findViewById<ImageView>(R.id.instaBarbosa)

        // Carregas as imagens para os respetivos ImageView
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

        // Evento de Mouse Click nos icones de cada membro
        inJoao.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.linkedInJoao)))
            startActivity(intent)
        }
        gitJoao.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.githubJoao)))
            startActivity(intent)
        }


        inGoncalo.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.linkedInGoncalo)))
            startActivity(intent)
        }
        gitGoncalo.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.githubGoncalo)))
            startActivity(intent)
        }


        inTiago.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.linkedInTiago)))
            startActivity(intent)
        }
        gitTiago.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.githubTiago)))
            startActivity(intent)
        }


        gitBarbosa.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.githubBarbosa)))
            startActivity(intent)
        }
        instaBarbosa.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.instagramBarbosa)))
            startActivity(intent)
        }


        gitGuilherme.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.githubGuilherme)))
            startActivity(intent)
        }
        instaGuilherme.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.instagramGuilherme)))
            startActivity(intent)
        }


        gitFilipe.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.githubFilipe)))
            startActivity(intent)
        }
        instaFilipe.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.instagramFilipe)))
            startActivity(intent)
        }


        gitMiguel.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.githubMiguel)))
            startActivity(intent)
        }
        instaMiguel.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.instagramMiguel)))
            startActivity(intent)
        }

        return view
    }

    /**
     * Função para carregar imagens no ImageView utilizando o Glide
     */
    fun loadImageView(url: String, image: ImageView){
        // Carrega as imagens nos ImageViews usando Glide
        Glide.with(this)
            .load(url)
            .apply(RequestOptions.bitmapTransform(CircleCrop()))
            .into(image)
    }
}
