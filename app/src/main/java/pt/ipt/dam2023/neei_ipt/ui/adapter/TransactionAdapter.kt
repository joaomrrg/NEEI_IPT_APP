package pt.ipt.dam2023.neei_ipt.ui.adapter

import android.Manifest
import android.content.Context
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import com.vmadalin.easypermissions.EasyPermissions
import com.vmadalin.easypermissions.dialogs.SettingsDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pt.ipt.dam2023.neei_ipt.R
import pt.ipt.dam2023.neei_ipt.model.Document
import pt.ipt.dam2023.neei_ipt.model.Transaction
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Scanner

class TransactionAdapter(context: Context, resource: Int, objects: List<Transaction>) :

    ArrayAdapter<Transaction>(context, resource, objects){
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val itemView = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.item_transaction, parent, false)

        // Obtem o objeto (neste caso 1 transação) dada a sua posição
        val transaction = getItem(position)

        // Ponteiros de elementos da View
        val descriptionTextView = itemView.findViewById<TextView>(R.id.transactionDescription)
        val dateTextView = itemView.findViewById<TextView>(R.id.transactionDate)
        val valueTextView = itemView.findViewById<TextView>(R.id.transactionValue)
        // Manipulação da informação a mostrar
        descriptionTextView.text = transaction?.description
        // Formata a data recebida em String
        val dateFormatter = SimpleDateFormat("dd/MM/yyyy")
        val formattedDate = dateFormatter.format(transaction?.date)
        dateTextView.text = formattedDate
        // Formata o valor recebido em String
        valueTextView.text = transaction?.value.toString()+"€"
        return itemView
    }
}