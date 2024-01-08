package pt.ipt.dam2023.neei_ipt.model

import com.google.gson.annotations.SerializedName
import java.util.Date

/**
 * Descreve um Grupo (LEI, TESP, MEEI, etc)
 */
data class Group(
    @SerializedName("id") val id: Int?,
    @SerializedName("description") val description: String?,
    @SerializedName("color") val color: String?
)