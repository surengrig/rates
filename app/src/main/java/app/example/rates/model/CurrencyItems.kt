package app.example.rates.model

import androidx.databinding.ObservableDouble


data class CurrencyItem(
    val id: Int,
    val currencyCode: String,
    val currencyName: String,
    val flagResource: Int,
    val amount: ObservableDouble,
    val isFocusable: Boolean = false
)