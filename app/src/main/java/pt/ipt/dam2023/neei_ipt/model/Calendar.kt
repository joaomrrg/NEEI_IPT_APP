package pt.ipt.dam2023.neei_ipt.model

import com.google.gson.annotations.SerializedName
import java.util.Date

data class Calendar (
    @SerializedName("id") val id: Int,
    @SerializedName("description") val description: String,
    @SerializedName("initialDate") val initialDate: Date,
    @SerializedName("endDate") val endDate: Date?,
    @SerializedName("groupId") val groupId: Int
)

data class CalendarWithColor(
    @SerializedName("id") val id: Int,
    @SerializedName("description") val description: String,
    @SerializedName("initialDate") val initialDate: Date,
    @SerializedName("endDate") val endDate: Date?,
    @SerializedName("groupId") val groupId: Int,
    @SerializedName("color") val color: String?
)