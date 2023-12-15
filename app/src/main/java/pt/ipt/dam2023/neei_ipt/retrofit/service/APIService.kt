package pt.ipt.dam2023.neei_ipt.retrofit.service

import pt.ipt.dam2023.neei_ipt.model.AuthRequest
import pt.ipt.dam2023.neei_ipt.model.AuthResponse
import pt.ipt.dam2023.neei_ipt.model.RegisterRequest
import pt.ipt.dam2023.neei_ipt.model.User
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface APIService {
        /**
         * Recebe a lista de todos os utilizadores do sistema
         */
        @GET("users")
        fun list(): Call<List<User>>

        /**
         * Permite a autenticação de um utilizador no sistema
         */
        @POST("auth/")
        fun authenticate(@Body request: AuthRequest): Call<AuthResponse>

        @POST("users/")
        fun register(@Body request: RegisterRequest): Call<Void>

}