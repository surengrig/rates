package app.example.rates.model

import androidx.databinding.ObservableDouble


data class CurrencyItem(
    val id: Int,
    val currencyCode: String,
    val currencyName: String,
    val flagResource: Int,
    val amount: ObservableDouble,
    val isFocusable: Boolean = false
) {
    override fun equals(other: Any?): Boolean {
        if (other == null || other !is CurrencyItem) return false
        return id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}


