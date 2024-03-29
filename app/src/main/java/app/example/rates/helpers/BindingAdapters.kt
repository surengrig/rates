package app.example.rates.helpers


import android.widget.EditText
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseMethod
import androidx.recyclerview.widget.RecyclerView
import app.example.rates.model.CurrencyItem
import app.example.rates.ui.adapters.BindableAdapter
import app.example.rates.ui.main.MainViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.ParseException


@BindingAdapter("data")
fun <T> RecyclerView.setRecyclerViewData(items: T) {
    if (adapter is BindableAdapter<*>) {
        @Suppress("UNCHECKED_CAST")
        (adapter as BindableAdapter<T>).setViewState(items)
    }
}

@BindingAdapter("setImageResource")
fun ImageView.bindImageResource(resource: Int) {
    Glide.with(this.context)
        .load(resource)
        .apply(RequestOptions.circleCropTransform())
        .into(this)
}

@BindingAdapter("requestFocus")
fun requestFocus(editText: EditText, requestFocus: Boolean) {
    if (requestFocus) {
        editText.isFocusableInTouchMode = true
        editText.requestFocus()
    } else {
        editText.isFocusable = false
    }
}

class Converter {

    companion object {

        @JvmStatic
        @InverseMethod("amountToPrice")
        fun amountToString(
            view: EditText,
            viewModel: MainViewModel,
            item: CurrencyItem,
            value: BigDecimal
        ): String {
            val numberFormat = getNumberFormat()
            try {
                val inView = view.text.toString()
                if (value.compareTo(BigDecimal.ZERO) == 0) return ""

                val parsed = numberFormat.parseObject(inView) as BigDecimal

                if (parsed.compareTo(value) == 0) {
                    return inView
                }
            } catch (e: ParseException) {
            }

            return numberFormat.format(value)
        }

        @JvmStatic
        fun amountToPrice(
            view: EditText,
            viewModel: MainViewModel,
            item: CurrencyItem,
            value: String
        ): BigDecimal {
            val numberFormat = getNumberFormat()
            val result = try {
                numberFormat.parseObject(value) as BigDecimal
            } catch (e: ParseException) {
                BigDecimal.ZERO
            }

            if (item.isFocusable) {
                viewModel.baseAmountLiveData.value = result
            }

            return result

        }

        private fun getNumberFormat(): NumberFormat = DecimalFormat.getInstance().also {
            (it as DecimalFormat).isParseBigDecimal = true
        }
    }
}