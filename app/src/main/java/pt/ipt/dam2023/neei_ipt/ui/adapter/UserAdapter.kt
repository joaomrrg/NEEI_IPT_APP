package pt.ipt.dam2023.neei_ipt.ui.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import pt.ipt.dam2023.neei_ipt.R
import pt.ipt.dam2023.neei_ipt.model.UpdateRoleRequest
import pt.ipt.dam2023.neei_ipt.model.User
import pt.ipt.dam2023.neei_ipt.retrofit.RetrofitInitializer
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserAdapter(context: Context, resource: Int, objects: List<User>) :
    ArrayAdapter<User>(context, resource, objects) {


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        // Inflar o layout do item do utilizador
        val itemView = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.item_user, parent, false)

        // Obter o utilizador na posição atual
        val user = getItem(position)

        // Referenciar os elementos visuais do layout
        val userNameTextView = itemView.findViewById<TextView>(R.id.userName)
        val userGroupTextView = itemView.findViewById<TextView>(R.id.userGroup)
        val userRoleTextView = itemView.findViewById<TextView>(R.id.userRole)
        val editImageView = itemView.findViewById<ImageView>(R.id.editButton)

        val userImageView = itemView.findViewById<ImageView>(R.id.userImage)

        // Definir o texto e a cor do cargo do usuário com base no valor do campo 'role'
        userRoleTextView.text = when (user?.role) {
            1 -> "Administrador"
            2 -> "Membro"
            3 -> "Aluno"
            4 -> "Alumni"
            5 -> "Convidado"
            else -> ""
        }
        editImageView.isVisible = !userRoleTextView.text.equals("Administrador")

        editImageView.setOnClickListener {
            showCustomDialog(context,userRoleTextView.text.toString(), user?.id!!,userRoleTextView)
        }

        // Definir o nome do utilizador e o grupo de usuários nos campos correspondentes
        userNameTextView.text = user?.username
        userGroupTextView.text = user?.email
        val imageUrl = "https://neei.eu.pythonanywhere.com/images/"+ user?.person!!.image
        Glide.with(context)
            .load(imageUrl)
            .apply(RequestOptions.bitmapTransform(CircleCrop()))
            .skipMemoryCache(true)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .into(userImageView)

        return itemView
    }

    private fun showCustomDialog(context: Context, role:String, id:Int, roleDescriptionView: TextView){

        // Inflar o layout personalizado
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.custom_dialog_layout, null)
        // Configurar o spinner
        val spinner: Spinner = view.findViewById(R.id.spinner)
        val roles = arrayOf("Administrador", "Membro", "Aluno", "Alumni", "Convidado")
        val adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, roles)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        spinner.setSelection(
            when (role) {
                "Administrador" -> 0
                "Membro" -> 1
                "Aluno" -> 2
                "Alumni" -> 3
                "Convidado" -> 4
                else -> 5
            }
        )

        // Criar o AlertDialog
        val alertDialog = AlertDialog.Builder(context)
            .setTitle("Alteração de Cargo")
            .setView(view)
            .setPositiveButton("Editar") { dialog, which ->
                // Lógica ao clicar no botão OK
                val selectedItem = spinner.selectedItem.toString()
                val roleId = when (selectedItem) {
                    "Administrador" -> 1
                    "Membro" -> 2
                    "Aluno" -> 3
                    "Alumni" -> 4
                    "Convidado" -> 5
                    else -> 6
                }
                val roleReq = UpdateRoleRequest(id, roleId)
                updateRole(roleReq) { statusCode ->
                    if (statusCode == 200) {
                        showToast("Cargo alterado com sucesso")
                        roleDescriptionView.text = when (roleId) {
                            1 -> "Administrador"
                            2 -> "Membro"
                            3 -> "Aluno"
                            4 -> "Alumni"
                            5 -> "Convidado"
                            else -> ""
                        }
                    } else {
                        showToast("Erro ao alterar cargo")
                    }
                }
            }
            .setNegativeButton("Cancelar") { dialog, which ->
            }
            .create()
        // Exibir o pop-up
        alertDialog.show()
    }

    // Função para atualizar um cargo de umutilizador na bd
    private fun updateRole(roleR: UpdateRoleRequest, onResult: (Int) -> Unit) {
        // Faz a chamada a API
        val call = RetrofitInitializer().APIService().updateRole(roleR)

        call.enqueue(object : Callback<Void> {
            override fun onFailure(call: Call<Void>, t: Throwable) {
                t?.message?.let { Log.e("onFailure error", it) }
                onResult(501)
            }

            // Retorna o StatusCode da resposta
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                onResult(response.code())
            }
        })
    }

    // Função interna para exibir um Toast com uma mensagem
    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}
