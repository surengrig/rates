package app.example.rates.model


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CurrencyRates(
    @Json(name = "base")
    val base: String,
    @Json(name = "date")
    val date: String,
    @Json(name = "rates")
    val ratesMap: Map<String, Double>
)