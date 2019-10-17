package app.example.rates.ui.main

import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.DiffUtil
import app.example.rates.R
import app.example.rates.model.CurrencyItem
import app.example.rates.ui.adapters.BindableAdapter
import app.example.rates.ui.adapters.BindingRecyclerAdapter
import app.example.rates.ui.common.ViewState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

class RatesListAdapter(
    clickHandler: ClickHandler,
    viewModel: ViewModel
) : BindingRecyclerAdapter<CurrencyItem, ClickHandler>(clickHandler, viewModel),
    BindableAdapter<ViewState<List<CurrencyItem>>>, CoroutineScope {
    private val job: Job = Job()

    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main


    private val data: ArrayList<CurrencyItem> = ArrayList()

    override fun setViewState(viewState: ViewState<List<CurrencyItem>>) {
        if (viewState is ViewState.Success) {
            val diffResult = DiffUtil.calculateDiff(DiffCallback(viewState.data, data))
            diffResult.dispatchUpdatesTo(this@RatesListAdapter)
            data.clear()
            data.addAll(viewState.data)
        }
    }

    override fun getItemForPosition(position: Int) = data[position]

    override fun getLayoutIdForPosition(position: Int) = R.layout.rates_item_view

    override fun getItemCount() = data.size

    override fun getItemId(position: Int) = data[position].id.toLong()

    class DiffCallback(
        private val newList: List<CurrencyItem>,
        private val oldList: List<CurrencyItem>
    ) : DiffUtil.Callback() {
        override fun getOldListSize() = oldList.size

        override fun getNewListSize() = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
            oldList[oldItemPosition].id == newList[newItemPosition].id

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].amount.get() == newList[newItemPosition].amount.get() &&
                    oldList[oldItemPosition].isFocusable == newList[newItemPosition].isFocusable
        }

        override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int) = true
    }
}

interface ClickHandler {
    fun onItemClick(currencyItem: CurrencyItem)
}