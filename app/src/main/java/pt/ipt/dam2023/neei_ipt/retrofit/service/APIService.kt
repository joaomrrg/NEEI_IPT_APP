package pt.ipt.dam2023.neei_ipt.retrofit.service

import okhttp3.MultipartBody
import pt.ipt.dam2023.neei_ipt.model.AuthRequest
import pt.ipt.dam2023.neei_ipt.model.AuthResponse
import pt.ipt.dam2023.neei_ipt.model.CalendarRequest
import pt.ipt.dam2023.neei_ipt.model.CalendarResponse
import pt.ipt.dam2023.neei_ipt.model.CalendarWithColor
import pt.ipt.dam2023.neei_ipt.model.ChangePasswordRequest
import pt.ipt.dam2023.neei_ipt.model.Document
import pt.ipt.dam2023.neei_ipt.model.DocumentRequest
import pt.ipt.dam2023.neei_ipt.model.ResponseAPI
import pt.ipt.dam2023.neei_ipt.model.Group
import pt.ipt.dam2023.neei_ipt.model.Note
import pt.ipt.dam2023.neei_ipt.model.RecoverPasswordRequest
import pt.ipt.dam2023.neei_ipt.model.RegisterRequest
import pt.ipt.dam2023.neei_ipt.model.Transaction
import pt.ipt.dam2023.neei_ipt.model.TransactionBudget
import pt.ipt.dam2023.neei_ipt.model.TransactionRequest
import pt.ipt.dam2023.neei_ipt.model.UpdateRoleRequest
import pt.ipt.dam2023.neei_ipt.model.User
import pt.ipt.dam2023.neei_ipt.model.UpdatePersonRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface APIService {
        /**
         * Recebe a lista de todos os utilizadores do sistema
         */
        @GET("users")
        fun listUsers(): Call<List<User>>

        /**
         * Recebe a lista de todos os documentos público do NEEI
         */
        @GET("documents")
        fun listDocs(): Call<List<Document>>

        /**
         * Recebe a lista de todos os apontamentos disponibilizados pelo NEEI
         */
        @GET("notes")
        fun listNotes(): Call<List<Note>>

        /**
         * Recebe a lista de todos os eventos do calendário do sistema
         */
        @GET("calendar")
        fun listEvents(): Call<List<CalendarWithColor>>

        /**
         * Permite adicionar um novo evento ao calendário
         */
        @POST("calendar/")
        fun addEvent(@Body request: CalendarRequest): Call<CalendarResponse>

        /**
         * Permite a autenticação de um utilizador no sistema
         */
        @POST("auth/")
        fun authenticate(@Body request: AuthRequest): Call<AuthResponse>

        /**
         * Permite o registo de um utilizador
         */
        @POST("users/")
        fun register(@Body request: RegisterRequest): Call<ResponseAPI>

        /**
         * Recebe a lista de todos os grupos do sistema
         */
        @GET("groups")
        fun listGroups(): Call<List<Group>>

        /**
         * Recebe um objeto User que representa um utilizador, dado o seu id
         */
        @GET("users/{id}")
        fun getUserById(@Path("id") id: Int): Call<User>

        /**
         * Permite a adição de um documento a bd
         */
        @POST("documents/")
        fun addDocument(@Body request: DocumentRequest): Call<ResponseAPI>

        /**
         * Permite o update de um utilizador nop sistema
         */
        @POST("updateProfile/")
        fun updateProfile(@Body request: UpdatePersonRequest): Call<ResponseAPI>

        /**
         * Permite o update de um cargo de um utilizador no sistema
         */
        @POST("updateRole/")
        fun updateRole(@Body request: UpdateRoleRequest): Call<Void>

        /**
         * Permite o upload de um ficheiro para o servidor
         */
        @Multipart
        @POST("uploadFile/")
        fun uploadFile(@Part file: MultipartBody.Part): Call<ResponseAPI>

        @Multipart
        @POST("uploadImage/")
        fun uploadImage(@Part image: MultipartBody.Part): Call<Void>

        /**
         * Recebe a lista de todas as transações
         */
        @GET("transactions")
        fun listTransactions(): Call<List<Transaction>>

        /**
         * Recebe o valor da conta do NEEI
         */
        @GET("getBudget")
        fun getBudget(): Call<TransactionBudget>

        /**
         * Permite a adição de uma transação (movimento)
         */
        @POST("transactions/")
        fun addTransaction(@Body request: TransactionRequest): Call<ResponseAPI>

        /**
         * Permite remover um documento, dado o seu id
         */
        @POST("removeDocument/{id}")
        fun removeDocument(@Path("id") id: Int): Call<Void>

        /**
         * Permite remover um apontamento (note), dado o seu id
         */
        @POST("removeNote/{id}")
        fun removeApontamento(@Path("id") id: Int): Call<Void>

        /**
         * Permite a recuperação de password de um utilizador (envia email)
         */
        @POST("recoverPassword/")
        fun recoverPassword(@Body request: RecoverPasswordRequest): Call<Void>

        /**
         *  Permite a alteração de password de um utilizador
         */
        @POST("changePassword/")
        fun changePassword(@Body request: ChangePasswordRequest): Call<Void>
}