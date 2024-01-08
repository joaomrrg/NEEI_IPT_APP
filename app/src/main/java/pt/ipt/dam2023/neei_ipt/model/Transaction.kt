package pt.ipt.dam2023.neei_ipt.model

import com.google.gson.annotations.SerializedName
import java.util.Date

/**
 * Descreve uma transação (Tesouraria/Conta NEEI)
 */
data class Transaction(
    @SerializedName("id") val id: Int?,
    @SerializedName("description") val description: String?,
    @SerializedName("date") val date: Date?,
    @SerializedName("value") val value: Float?
)

/**
 * API - Atributos que são requeridos num POST para adicionar uma transação
 */
data class TransactionRequest(
    @SerializedName("description") val description: String?,
    @SerializedName("date") val date: String?,
    @SerializedName("value") val value: Float?
)

/**
 * API - Atributo que recebe a soma de todas as transações
 */
data class TransactionBudget(
    @SerializedName("budget") val budget: Float?
)
