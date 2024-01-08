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
import pt.ipt.dam2023.neei_ipt.model.CalendarWithColor
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.text.SimpleDateFormat
import java.util.Scanner

class CalendarItemAdapter(context: Context, resource: Int, objects: List<CalendarWithColor>) :

    ArrayAdapter<CalendarWithColor>(context, resource, objects) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val itemView = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.item_calendar, parent, false)

        // Obtem o objeto (neste caso 1 evento) dada a sua posição
        val calendar = getItem(position)

        // Ponteiros de elementos da View
        val titleTextView = itemView.findViewById<TextView>(R.id.documentTitle)
        val descriptionTextView = itemView.findViewById<TextView>(R.id.documentDescription)
        val dateTextView = itemView.findViewById<TextView>(R.id.documentDate)
        val hourTextView = itemView.findViewById<TextView>(R.id.eventHours)
        val lineColor = itemView.findViewById<ImageView>(R.id.underlineBackgroundImage)

        // Manipulação da informação a mostrar
        titleTextView.text = calendar?.title
        descriptionTextView.text = calendar?.description
        // Formata a data recebida em String
        val dateFormatter = SimpleDateFormat("dd/MM/yyyy")
        val formattedDate = dateFormatter.format(calendar?.initialDate)
        dateTextView.text = formattedDate

        // Formata a hora recebida em String
        val timeFormatter = SimpleDateFormat("HH:mm")
        val formattedTime = timeFormatter.format(calendar?.initialDate)
        // Verifica a hora, se for 00:00 significa que o evento é o dia todx
        if (formattedTime != "00:00"){
            hourTextView.text = formattedTime
        }else{
            hourTextView.text = "Todo o dia"
        }

        // Leitura da Internal Storage
        val directory: File = context.filesDir
        val file: File = File(directory, "dados.txt")
        try {
            val fi: FileInputStream = FileInputStream(file)
            val sc: Scanner = Scanner(fi)
            sc.nextLine()
            sc.nextLine()
            sc.nextLine()
            // Guarda a role do user
            val role =  sc.nextLine().toInt()
            sc.close()
            fi.close()
        } catch (e: FileNotFoundException) {
            Toast.makeText(context, "File not found", Toast.LENGTH_LONG).show()
        }
        // Escolhe a cor da linha através do id do grupo associado ao evento
        val lineColorResource = when (calendar?.groupId) {
            1 -> R.drawable.underline_g1
            2 -> R.drawable.underline_g2
            3 -> R.drawable.underline_g3
            4 -> R.drawable.underline_g4
            5 -> R.drawable.underline_g5
            6 -> R.drawable.underline_g6
            7 -> R.drawable.underline_g7
            8 -> R.drawable.underline_g8
            else -> R.drawable.underline
        }
        lineColor.setImageResource(lineColorResource)

        return itemView
    }
}