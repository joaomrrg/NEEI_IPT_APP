package pt.ipt.dam2023.neei_ipt.model

import com.google.gson.annotations.SerializedName
import java.util.Date

/**
 * Descreve um documento público do NEII
 */
data class Document(
    @SerializedName("id") val id: Int?,
    @SerializedName("title") val title: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("date") val date: Date?,
    @SerializedName("file") val file: String?,
    @SerializedName("schoolYearId") val schoolYearId: Int?,
    @SerializedName("createdAt") val createdAt: Date?,
    )

data class DocumentRequest(
    @SerializedName("title") val title: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("date") val date: String?,
    @SerializedName("file") val file: String?,
    @SerializedName("schoolYearId") val schoolYearId: Int?,
)