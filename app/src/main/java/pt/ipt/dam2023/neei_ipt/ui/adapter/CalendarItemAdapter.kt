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

        val calendar = getItem(position)

        val titleTextView = itemView.findViewById<TextView>(R.id.documentTitle)
        val descriptionTextView = itemView.findViewById<TextView>(R.id.documentDescription)
        val dateTextView = itemView.findViewById<TextView>(R.id.documentDate)
        val lineColor = itemView.findViewById<ImageView>(R.id.underlineBackgroundImage)

        titleTextView.text = calendar?.title
        descriptionTextView.text = calendar?.description
        val dateFormatter = SimpleDateFormat("dd/MM/yyyy")
        val formattedDate = dateFormatter.format(calendar?.initialDate)
        dateTextView.text = formattedDate

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



        //PERMISSION request constant, assign any value
        val STORAGE_PERMISSION_CODE = 100
        val TAG = "PERMISSION_TAG"

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

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }


}