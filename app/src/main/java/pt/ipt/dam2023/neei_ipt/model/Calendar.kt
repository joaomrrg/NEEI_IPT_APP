package pt.ipt.dam2023.neei_ipt.model

import com.google.gson.annotations.SerializedName
import java.util.Date

/**
 * Descreve um evento no calendário da aplicação
 */
data class Calendar (
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("initialDate") val initialDate: Date,
    @SerializedName("endDate") val endDate: Date?,
    @SerializedName("groupId") val groupId: Int
)

/**
 * API - Atributos recebidos sobre um evento no calendario
 */
data class CalendarWithColor(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("initialDate") val initialDate: Date,
    @SerializedName("endDate") val endDate: Date?,
    @SerializedName("groupId") val groupId: Int,
    @SerializedName("color") val color: String?
)

/**
 * API - Atributos que são requeridos num POST para adicionar um evento no calendario
 */
data class CalendarRequest (
    @SerializedName("description") val description: String?,
    @SerializedName("initialDate") val initialDate: String?,
    @SerializedName("endDate") val endDate: Date?,
    @SerializedName("groupId") val groupId: Int?
)

data class CalendarResponse (
    @SerializedName("message") val message: String?,
    @SerializedName("code") val code: Int?
)