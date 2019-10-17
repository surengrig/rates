package app.example.rates.helpers

import com.blongho.country_data.Currency
import com.blongho.country_data.World

class CurrencyExtras {
    private var currencyMap: Map<String, Currency> = World.getAllCurrencies().map {
        it.code to it
    }.toMap()

    fun getNameFrom(currencyCode: String) = currencyMap[currencyCode]?.name ?: ""
    fun getFlagResourceFrom(currencyCode: String) = currencyMap[currencyCode]?.flagResource ?: 0
}