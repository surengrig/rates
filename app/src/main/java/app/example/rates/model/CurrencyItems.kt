package app.example.rates.model

import androidx.databinding.ObservableField
import java.math.BigDecimal


data class CurrencyItem(
    val id: Int,
    val currencyCode: String,
    val currencyName: String,
    val flagResource: Int,
    val amount: ObservableField<BigDecimal>,
    val isFocusable: Boolean = false
)