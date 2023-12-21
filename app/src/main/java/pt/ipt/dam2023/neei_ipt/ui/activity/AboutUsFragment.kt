
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import pt.ipt.dam2023.neei_ipt.R

class AboutUsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_about_us, container, false)

        // Find views from the inflated layout
        val imgNucleo = view.findViewById<ImageView>(R.id.imgNucleo)
        val tvSobreNosTitle = view.findViewById<TextView>(R.id.tvSobreNosTitle)
        val tvNucleoDescription = view.findViewById<TextView>(R.id.tvNucleoDescription)
        val tvOndeEstamosTitle = view.findViewById<TextView>(R.id.tvOndeEstamosTitle)
        val tvMorada = view.findViewById<TextView>(R.id.tvMorada)
        val tvDevelopersTitle = view.findViewById<TextView>(R.id.tvDevelopersTitle)
        val llGoncalo = view.findViewById<LinearLayout>(R.id.llGoncalo)
        val llJoao = view.findViewById<LinearLayout>(R.id.llJoao)
        val imageGoncalo = view.findViewById<ImageView>(R.id.imageGoncalo)
        val imageJoao = view.findViewById<ImageView>(R.id.imageJoao)

        // Load images into ImageViews using Glide
        Glide.with(this)
            .load("https://avatars.githubusercontent.com/u/55167343")
            .apply(RequestOptions.bitmapTransform(CircleCrop()))
            .into(imageGoncalo)

        Glide.with(this)
            .load("https://avatars.githubusercontent.com/u/120472343")
            .apply(RequestOptions.bitmapTransform(CircleCrop()))
            .into(imageJoao)

        // Set click listeners for the LinearLayouts
        llJoao.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.githubJoao)))
            startActivity(intent)
        }

        llGoncalo.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.githubGoncalo)))
            startActivity(intent)
        }

        return view
    }
}
