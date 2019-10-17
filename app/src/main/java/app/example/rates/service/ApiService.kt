package app.example.rates.service

import app.example.rates.model.CurrencyRates
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query


interface ApiService {

    @GET("/latest")
    suspend fun ratesList(
        @Query("base") base: String
    ): Response<CurrencyRates>

}
