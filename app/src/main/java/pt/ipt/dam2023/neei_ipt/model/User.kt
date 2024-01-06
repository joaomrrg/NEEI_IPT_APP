package pt.ipt.dam2023.neei_ipt.model

import com.google.gson.annotations.SerializedName
import java.util.Date
import java.time.LocalDateTime

/**
 * Descreve um utilizador da aplicação
 */
data class User(
    @SerializedName("id") val id: Int?,
    @SerializedName("username") val username: String?,
    @SerializedName("email") val email: String?,
    @SerializedName("password") val password: String?,
    @SerializedName("role") val role: Int?,
    @SerializedName("person") val person: Person?,
    @SerializedName("verified") val verified: Boolean?,
    @SerializedName("createdAt") val createdAt: Date?
)

/**
 * API - Atributos requeridos para o POST da Autenticação de um Utilizador
 */
data class AuthRequest(
    @SerializedName("username") val username: String?,
    @SerializedName("password") val password: String?)

/**
 * API - Atributos que são recebidos do POST da Autenticação de um Utilizador
 */
data class AuthResponse(
    @SerializedName("id") val id: Int?,
    @SerializedName("name") val name: String?,
    @SerializedName("surname") val surname: String?,
    @SerializedName("role") val role: Int?,
    @SerializedName("roleDescription") val roleDescription: String?,
    @SerializedName("image") val image: String?,
    @SerializedName("code") var code: Int?,
    @SerializedName("errorMessage") var errorMessage: String?)

/**
 * API - Atributos requeridos para registar um utilizador (User and Person)
 */
data class RegisterRequest(
    @SerializedName("email") val email: String?,
    @SerializedName("username") val username: String?,
    @SerializedName("password") val password: String?,
    @SerializedName("role") val role: Int?,
    @SerializedName("name") val name: String?,
    @SerializedName("surname") val surname: String?
)

/**
 * API - Atributos requeridos para atualizar um cargo do utilizador
 */
data class UpdateRoleRequest(
    @SerializedName("id") val id: Int?,
    @SerializedName("role") val role: Int?
)
