package pt.ipt.dam2023.neei_ipt.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import pt.ipt.dam2023.neei_ipt.R
import pt.ipt.dam2023.neei_ipt.model.User

class UserAdapter(context: Context, resource: Int, objects: List<User>) :
    ArrayAdapter<User>(context, resource, objects) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        // Inflar o layout do item de usuário
        val itemView = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.item_user, parent, false)

        // Obter o usuário na posição atual
        val user = getItem(position)

        // Referenciar os elementos visuais do layout
        val userNameTextView = itemView.findViewById<TextView>(R.id.userName)
        val userGroupTextView = itemView.findViewById<TextView>(R.id.userGroup)
        val userRoleTextView = itemView.findViewById<TextView>(R.id.userRole)
        val editImageView = itemView.findViewById<ImageView>(R.id.editButton)

        // Definir o texto e a cor do cargo do usuário com base no valor do campo 'role'
        userRoleTextView.text = when (user?.role) {
            1 -> "Administrador"
            2 -> "Membro"
            3 -> "Aluno"
            4 -> "Alumni"
            5 -> "Outro"
            else -> ""
        }

        // Definir o nome do usuário e o grupo de usuários nos campos correspondentes
        userNameTextView.text = user?.username
        userGroupTextView.text = user?.email

        return itemView
    }

    // Função interna para exibir um Toast com uma mensagem
    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}
