package pt.ipt.dam2023.neei_ipt.retrofit.service

import pt.ipt.dam2023.neei_ipt.model.AuthRequest
import pt.ipt.dam2023.neei_ipt.model.AuthResponse
import pt.ipt.dam2023.neei_ipt.model.Calendar
import pt.ipt.dam2023.neei_ipt.model.CalendarWithColor
import pt.ipt.dam2023.neei_ipt.model.RegisterRequest
import pt.ipt.dam2023.neei_ipt.model.User
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface APIService {
        /**
         * Recebe a lista de todos os utilizadores do sistema
         */
        @GET("users")
        fun listUsers(): Call<List<User>>

        /**
         * Recebe a lista de todos os eventos do calendário do sistema
         */
        @GET("calendar")
        fun listEvents(): Call<List<CalendarWithColor>>

        /**
         * Permite a autenticação de um utilizador no sistema
         */
        @POST("auth/")
        fun authenticate(@Body request: AuthRequest): Call<AuthResponse>

        /**
         * Permite o registo de um utilizador
         */
        @POST("users/")
        fun register(@Body request: RegisterRequest): Call<Void>

}