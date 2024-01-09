package pt.ipt.dam2023.neei_ipt.ui.activity

import pt.ipt.dam2023.neei_ipt.ui.fragment.AboutUsViewFragment
import pt.ipt.dam2023.neei_ipt.ui.fragment.BalanceViewFragment
import pt.ipt.dam2023.neei_ipt.ui.fragment.CalendarViewFragment
import DocumentFragment
import pt.ipt.dam2023.neei_ipt.ui.fragment.HomeViewFragment
import pt.ipt.dam2023.neei_ipt.ui.fragment.NoteViewFragment
import pt.ipt.dam2023.neei_ipt.ui.fragment.SettingsViewFragment
import pt.ipt.dam2023.neei_ipt.ui.fragment.TeamViewFragment
import pt.ipt.dam2023.neei_ipt.ui.fragment.TemporaryInfoViewFragment
import pt.ipt.dam2023.neei_ipt.ui.fragment.UserViewFragment
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.navigation.NavigationView
import pt.ipt.dam2023.neei_ipt.R
import pt.ipt.dam2023.neei_ipt.model.User
import pt.ipt.dam2023.neei_ipt.retrofit.RetrofitInitializer
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.util.Scanner

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var imagePath: String
    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val homeFrag = HomeViewFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, homeFrag)
            .commit()

        drawerLayout = findViewById(R.id.drawer_layout)

        //Informação passada pela Intent
        val fragmentToShow = intent.getStringExtra("fragment_to_show")

        // Lógica para substituir o fragmento conforme necessário
        if (fragmentToShow == "DocumentFragment") {
            val documentFragment = DocumentFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, documentFragment)
                .commit()
        }else if (fragmentToShow == "CalendarViewFragment") {
            val calendarFragment = CalendarViewFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, calendarFragment)
                .commit()
        }else if (fragmentToShow == "UserViewFragment") {
            val userViewFragment = UserViewFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, userViewFragment)
                .commit()
        }else if (fragmentToShow == "BalanceViewFragment") {
            val balanceViewFragment = BalanceViewFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, balanceViewFragment)
                .commit()
        }

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbars)
        setSupportActionBar(toolbar)

        // Ponteiro para a navigation view
        val navigationView = findViewById<NavigationView>(R.id.nav_view)

        // Obter o cabeçalho da NavigationView
        val headerView = navigationView.getHeaderView(0)

        // Ponteiro para o TextView username no cabeçalho pelo ID
        val usernameText = headerView.findViewById<TextView>(R.id.username_text)

        // Ponteiro para o Relative Layout onde aparece o username, image e cargo
        val profileLayout = headerView.findViewById<RelativeLayout>(R.id.rlUser)
        profileLayout.setOnClickListener {
            // Crie um Intent para a nova Activity
            val intent = Intent(this, ProfileActivity::class.java)
            // Inicie a nova Activity
            startActivity(intent)
        }
        // Ponteiro para o TextView cargo no cabeçalho pelo ID
        val cargoText = headerView.findViewById<TextView>(R.id.cargo_text)
        val imageView = headerView.findViewById<ImageView>(R.id.imageView)

        // Ponteiro para as funções de Administração no Menu
        val adminMenu = navigationView.menu.findItem(R.id.adminMenu)

        // Leitura da Internal Storage
        val directory: File = filesDir
        val file: File = File(directory, "dados.txt")
        try {
            val fi: FileInputStream = FileInputStream(file)
            val sc: Scanner = Scanner(fi)
            // Atribui ao elemento usernameText o username guardado no internal storage
            usernameText.text = "@"+sc.nextLine()
            sc.nextLine()
            sc.nextLine()
            // Guarda a role do user
            val role =  sc.nextLine().toInt()
            cargoText.text = sc.nextLine()
            // Mete a imagem no ImageView por url
            imagePath = sc.nextLine()
            Glide.with(this)
                .load("https://neei.eu.pythonanywhere.com/images/" + imagePath)
                .apply(RequestOptions.bitmapTransform(CircleCrop()))
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(imageView)
            userId = sc.nextLine()
            sc.close()
            fi.close()
            // Verifica se é administrador para mostrar o menu de Administração
            adminMenu.isVisible = role == 1
        } catch (e: FileNotFoundException) {
            Toast.makeText(this, "File not found", Toast.LENGTH_LONG).show()
        }

        navigationView.setNavigationItemSelectedListener(this)

        val toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.open_menu,
            R.string.close_menu
        )
        drawerLayout.addDrawerListener(toggle)

        toggle.syncState()
    }

    override fun onResume() {
        // Quando volta a ter foco
        super.onResume()
        // Ponteiro para a navigation view
        val navigationView = findViewById<NavigationView>(R.id.nav_view)

        // Obter o cabeçalho da NavigationView
        val headerView = navigationView.getHeaderView(0)

        getUser(userId.toInt()) { result ->
            if (result != null) {
                imagePath = result.person?.image!!
                // Ponteiro para a imagem de perfil do utilizador
                val imageView = headerView.findViewById<ImageView>(R.id.imageView)
                Glide.with(this)
                    .load("https://neei.eu.pythonanywhere.com/images/" + imagePath)
                    .apply(RequestOptions.bitmapTransform(CircleCrop()))
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(imageView)
            }
        }

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> {
                val homeFrag = HomeViewFragment()
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, homeFrag)
                    .commit()
            }
            // Adicione outros casos conforme necessário
            R.id.nav_calendario -> {
                val calendarViewFragment = CalendarViewFragment() // Create an instance of your pt.ipt.dam2023.neei_ipt.ui.fragment.AboutUsViewFragment
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, calendarViewFragment)
                    .commit()
            }
            R.id.nav_logout -> {
                terminarSessao()
            }
            R.id.nav_about -> {
                val aboutUsViewFragment = AboutUsViewFragment() // Create an instance of your pt.ipt.dam2023.neei_ipt.ui.fragment.AboutUsViewFragment
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, aboutUsViewFragment)
                    .commit()
            }
            R.id.nav_documentacao -> {
                val documentsFragment = DocumentFragment() // Create an instance of your pt.ipt.dam2023.neei_ipt.ui.fragment.AboutUsViewFragment
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, documentsFragment)
                    .commit()
            }
            R.id.nav_equipa -> {
                val teamViewFragment = TeamViewFragment() // Create an instance of your pt.ipt.dam2023.neei_ipt.ui.fragment.AboutUsViewFragment
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, teamViewFragment)
                    .commit()
            }
            R.id.nav_money -> {
                val balanceViewFragment = BalanceViewFragment()
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, balanceViewFragment)
                    .commit()
            }
            R.id.nav_users -> {
                val userViewFragment = UserViewFragment()
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, userViewFragment)
                    .commit()
            }
            R.id.nav_temporary -> {
                val temporaryFragment = TemporaryInfoViewFragment()
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, temporaryFragment)
                    .commit()
            }
            R.id.nav_settings -> {
                val settingsViewFragment = SettingsViewFragment()
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, settingsViewFragment)
                    .commit()
            }
            R.id.nav_apontamentos -> {
                val apontamentosFragment = NoteViewFragment() // Create an instance of your pt.ipt.dam2023.neei_ipt.ui.fragment.AboutUsViewFragment
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, apontamentosFragment)
                    .commit()
            }
        }

        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    // Função para um utilizador dar logout
    private fun terminarSessao(){
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Terminar Sessão")
            builder.setMessage("Tem a certeza que quer terminar sessão?")
            builder.setPositiveButton("Sim") { dialog, which ->
                // Código a ser executado quando o utilizador escolhe "Sim"
                // Apagar as informações de utilizador do localStorage

                //Vai para a página de Login
                // Crie um Intent para a nova Activity
                val intent = Intent(this, LoginActivity::class.java)
                // Inicie a nova Activity
                startActivity(intent)
            }
            builder.setNegativeButton("Não") { dialog, which ->
                // Não faz nada
            }

            builder.show()
        }

    private fun getUser(id: Int?, onResult: (User?) -> Unit) {
        if (id != null) {
            val call = RetrofitInitializer().APIService().getUserById(id)
            call.enqueue(object : Callback<User?> {
                override fun onResponse(call: Call<User?>?, response: Response<User?>?) {
                    response?.body()?.let {
                        val user: User = it
                        onResult(user)
                    }
                }

                override fun onFailure(call: Call<User?>, t: Throwable) {
                    Log.e("Erro", t.message ?: "Erro na chamada da API")
                }
            })
        } else {
            // Lidar com o caso em que o ID é nulo
        }
    }

    }

