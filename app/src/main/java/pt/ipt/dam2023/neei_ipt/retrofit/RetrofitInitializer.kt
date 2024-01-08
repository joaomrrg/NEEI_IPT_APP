package pt.ipt.dam2023.neei_ipt.retrofit

import pt.ipt.dam2023.neei_ipt.retrofit.service.APIService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitInitializer {
    // Criação de uma instância Retrofit com a URL base da API
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://neei.eu.pythonanywhere.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    // Função para obter uma instância da interface APIService
    fun APIService() = retrofit.create(APIService::class.java)
}