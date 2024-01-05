package pt.ipt.dam2023.neei_ipt.model

import com.google.gson.annotations.SerializedName
import java.util.Date

/**
 *  Dados pessoais de um utilizador
 */
data class Person (
    val id: Int?,
    val name: String?,
    val surname: String?,
    val birthDate: Date?,
    val gender: String?,
    val linkedIn: String?,
    val github: String?,
    val image: String?,
    val numAluno: String?,
    val groupId: Int?,
    val userId: Int?
)

data class updatePersonRequest(
    @SerializedName("username") val username: String?,
    @SerializedName("name") val name: String?,
    @SerializedName("surname") val surname: String?,
    @SerializedName("birthDate") val birthDate: String?,
    @SerializedName("gender") val gender: String?,
    @SerializedName("linkedIn") val linkedIn: String?,
    @SerializedName("image") var image: String?,
    @SerializedName("github") val github: String?,
    @SerializedName("numAluno") val numAluno: String?
)