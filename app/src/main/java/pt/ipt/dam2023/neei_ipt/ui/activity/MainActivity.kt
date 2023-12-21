package pt.ipt.dam2023.neei_ipt.ui.activity

import AboutUsFragment
import CalendarViewFragment
import DocumentFragment
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import pt.ipt.dam2023.neei_ipt.R
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.util.Scanner

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        drawerLayout = findViewById(R.id.drawer_layout)

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
            Glide.with(this)
                .load(sc.nextLine())
                .into(imageView)
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


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> {
                // Adicione o código para lidar com a seleção do item Home aqui

            }
            // Adicione outros casos conforme necessário
            R.id.nav_calendario -> {
                val calendarViewFragment = CalendarViewFragment() // Create an instance of your AboutUsFragment
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, calendarViewFragment)
                    .commit()
            }
            R.id.nav_logout -> {
                terminarSessao()
            }
            R.id.nav_about -> {
                val aboutUsFragment = AboutUsFragment() // Create an instance of your AboutUsFragment
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, aboutUsFragment)
                    .commit()
            }
            R.id.nav_documentacao -> {
                val documentsFragment = DocumentFragment() // Create an instance of your AboutUsFragment
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, documentsFragment)
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
    fun terminarSessao(){
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
    }

