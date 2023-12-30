
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import pt.ipt.dam2023.neei_ipt.R

class TemporaryInfoFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Infla o layout para este fragmento
        val view = inflater.inflate(R.layout.fragment_temporary_info, container, false)

        // Encontra as views no layout inflado
        val llGoncalo = view.findViewById<LinearLayout>(R.id.llGoncalo)
        val llJoao = view.findViewById<LinearLayout>(R.id.llJoao)
        val imageGoncalo = view.findViewById<ImageView>(R.id.imageGoncalo)
        val imageJoao = view.findViewById<ImageView>(R.id.imageJoao)
        val libraryImageView = view.findViewById<ImageView>(R.id.image)
        val llLibrary = view.findViewById<ImageView>(R.id.gitBttn)

        // Carrega as imagens nos ImageViews usando Glide
        Glide.with(this)
            .load("https://avatars.githubusercontent.com/u/55167343")
            .apply(RequestOptions.bitmapTransform(CircleCrop()))
            .into(imageGoncalo)

        Glide.with(this)
            .load("https://avatars.githubusercontent.com/u/120472343")
            .apply(RequestOptions.bitmapTransform(CircleCrop()))
            .into(imageJoao)

        Glide.with(this)
            .load("https://raw.githubusercontent.com/SundeepK/CompactCalendarView/master/images/compact-calendar-view-example-multi-events.png")
            .apply(RequestOptions.bitmapTransform(CircleCrop()))
            .into(libraryImageView)

        // Define os listeners de clique para os LinearLayouts
        llJoao.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.githubJoao)))
            startActivity(intent)
        }

        llGoncalo.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.githubGoncalo)))
            startActivity(intent)
        }

        llLibrary.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.githubLibrary)))
            startActivity(intent)
        }

        return view
    }
}
