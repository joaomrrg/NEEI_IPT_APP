package pt.ipt.dam2023.neei_ipt.model

import com.google.gson.annotations.SerializedName
import java.util.Date

data class Transaction(
    @SerializedName("id") val id: Int?,
    @SerializedName("description") val description: String?,
    @SerializedName("date") val date: Date?,
    @SerializedName("value") val value: Float?
)

data class TransactionRequest(
    @SerializedName("description") val description: String?,
    @SerializedName("date") val date: String?,
    @SerializedName("value") val value: Float?
)

data class TransactionBudget(
    @SerializedName("budget") val budget: Float?
)
