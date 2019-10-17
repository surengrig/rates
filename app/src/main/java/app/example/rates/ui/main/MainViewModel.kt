package app.example.rates.ui.main

import androidx.databinding.ObservableDouble
import androidx.lifecycle.*
import app.example.rates.helpers.CurrencyExtras
import app.example.rates.helpers.Result
import app.example.rates.helpers.repeatDelayed
import app.example.rates.model.CurrencyItem
import app.example.rates.model.CurrencyRates
import app.example.rates.repository.RatesRepository
import app.example.rates.ui.common.ViewState
import kotlinx.coroutines.Job
import java.util.*
import kotlin.collections.ArrayList

typealias RatesViewStateType = ViewState<List<CurrencyItem>>

class MainViewModel constructor(
    private val ratesRepository: RatesRepository,
    private val currencyExtras: CurrencyExtras
) : ViewModel() {

    val baseLiveData = MutableLiveData<CurrencyItem>()
    var baseAmountLiveData = MutableLiveData<Double>(1.0)

    val viewState: LiveData<RatesViewStateType> = liveData {
        emit(ViewState.Loading(emptyList()))
        val ratesList = ratesList()
        emitSource(ratesList)
    }

    private var fetchJob: Job? = null

    private var currencies: LinkedList<CurrencyItem> = LinkedList()

    private var currencyRates: CurrencyRates? = null

    private var base = "EUR"
    private val baseAmount
        get() = baseAmountLiveData.value ?: 1.0

    /**
     * Cancel continues fetching
     */
    fun cancelFetch() {
        fetchJob?.cancel()
    }

    /**
     * Fetches currencyRates for currency [baseLiveData] every 1000 msec
     */
    fun fetchRates() {
        fetchJob = viewModelScope.repeatDelayed(1000) {
            ratesRepository.fetchRatesList(baseLiveData.value?.currencyCode ?: base)
        }
    }

    /**
     * Returns a [LiveData] of [RatesViewStateType], handles source of from repository, amount
     * changes and base currency changes
     */
    private fun ratesList(): LiveData<RatesViewStateType> {
        return MediatorLiveData<RatesViewStateType>().apply {
            addSource(ratesRepository.ratesList.map {
                when (it) {
                    is Result.Success -> {
                        currencyRates = it.data
                        val list = getCurrencyItemsList(base)
                        ViewState.Success(list)
                    }
                    is Result.Failure -> ViewState.Failure<List<CurrencyItem>>(error = it.error)
                }
            }) {
                value = it
            }
            addSource(baseLiveData) {
                base = it.currencyCode
                val list = moveItemToTop(it)
                value = ViewState.Success(list)
            }
            addSource(baseAmountLiveData) {
                getCurrencyItemsList(base)
                value = ViewState.Success(getCurrencyItemsList(base))
            }
        }
    }

    /**
     * Returns list of [CurrencyItem] for [newBaseCode]
     */
    private fun getCurrencyItemsList(newBaseCode: String): List<CurrencyItem> {
        currencyRates?.let { rates ->

            val newBaseRates = getNewRates(newBaseCode, rates)

            if (currencies.isEmpty()) {
                var id = 0

                val base = rates.base
                currencies.add(
                    CurrencyItem(
                        id++,
                        base,
                        currencyExtras.getNameFrom(base),
                        currencyExtras.getFlagResourceFrom(base),
                        ObservableDouble(
                            baseAmount
                        ),
                        true
                    )
                )

                newBaseRates.ratesMap.forEach {
                    currencies.add(
                        CurrencyItem(
                            id,
                            it.key,
                            currencyExtras.getNameFrom(it.key),
                            currencyExtras.getFlagResourceFrom(it.key),
                            ObservableDouble(it.value * baseAmount)
                        )
                    )
                    id++
                }

            } else {
                val list = LinkedList<CurrencyItem>()

                currencies.mapTo(list) {
                    val rate = newBaseRates.ratesMap[it.currencyCode] ?: 1.0
                    it.copy(
                        amount = ObservableDouble(rate * baseAmount)
                    )
                }
                currencies = list
            }

        }
        return currencies
    }

    /**
     * Based on [currencyRates], calculates and returns new [CurrencyRates] with [newBase]
     */
    private fun getNewRates(
        newBase: String,
        currencyRates: CurrencyRates
    ): CurrencyRates {
        if (newBase == currencyRates.base) return currencyRates

        val newRates = ArrayList<Pair<String, Double>>()

        currencyRates.ratesMap.forEach {
            if (it.key != newBase) {
                newRates.add(it.key to it.value / (currencyRates.ratesMap[newBase] ?: 1.0))
            }
        }
        newRates.add(currencyRates.base to 1.0 / (currencyRates.ratesMap[newBase] ?: 1.0))

        return currencyRates.copy(base = newBase, ratesMap = newRates.toMap())
    }

    /**
     * Moves [item] to start of the list
     */
    private fun moveItemToTop(item: CurrencyItem): List<CurrencyItem> {
        val second = currencies.removeFirst()

        second?.let {
            currencies.addFirst(second.copy(isFocusable = false))
        }

        val isRemoved = currencies.remove(item)
        if (isRemoved) {
            currencies.addFirst(item.copy(isFocusable = true))
        }

        baseAmountLiveData.value = currencies.first.amount.get()

        return currencies
    }
}


