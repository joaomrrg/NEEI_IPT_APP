package pt.ipt.dam2023.neei_ipt.model

import com.google.gson.annotations.SerializedName
import java.util.Date

data class Note(
    @SerializedName("id") val id: Int?,
    @SerializedName("title") val title: String?,
    @SerializedName("subject") val subject: String?,
    @SerializedName("file") val file: String?,
    @SerializedName("author") val author: String?,
    @SerializedName("createdAt") val createdAt: Date?,
)