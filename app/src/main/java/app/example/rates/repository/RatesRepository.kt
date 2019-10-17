package app.example.rates.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import app.example.rates.helpers.Result
import app.example.rates.helpers.getResult
import app.example.rates.model.CurrencyRates
import app.example.rates.service.ApiService


interface RatesRepository {
    suspend fun fetchRatesList(base: String): LiveData<Result<CurrencyRates>>
    val ratesList: LiveData<Result<CurrencyRates>>
}

class RatesRepositoryImpl(private val apiService: ApiService) : RatesRepository {

    override val ratesList = MutableLiveData<Result<CurrencyRates>>()
    override suspend fun fetchRatesList(
        base: String
    ): MutableLiveData<Result<CurrencyRates>> {
        try {
            apiService.ratesList(base)
                .getResult()
                .let {
                    ratesList.postValue(it)
                }
        } catch (ex: Exception) {
            ratesList.postValue(Result.Failure(ex))
        }


        return ratesList
    }
}
