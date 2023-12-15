package pt.ipt.dam2023.neei_ipt.retrofit

import pt.ipt.dam2023.neei_ipt.retrofit.service.APIService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitInitializer {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://neei.eu.pythonanywhere.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    fun APIService() = retrofit.create(APIService::class.java)
}