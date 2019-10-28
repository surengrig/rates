package app.example.rates

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import app.example.rates.helpers.CurrencyExtras
import app.example.rates.helpers.Result
import app.example.rates.model.CurrencyRates
import app.example.rates.repository.RatesRepository
import app.example.rates.ui.main.MainViewModel
import app.example.rates.ui.main.RatesViewStateType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.greaterThanOrEqualTo
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.stubbing.OngoingStubbing
import java.math.BigDecimal

// TODO add more tests

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest {
    @Suppress("unused")
    @get:Rule
    val instantExecutor = InstantTaskExecutorRule()

    private val testDispatcher = TestCoroutineDispatcher()

    private lateinit var subjectViewModel: MainViewModel

    @Mock
    private lateinit var mockCurrencyExtras: CurrencyExtras
    private val mockRepository = MockRepository()

    inner class MockRepository : RatesRepository {

        override suspend fun fetchRatesList(base: String): LiveData<Result<CurrencyRates>> {
            ratesList.postValue(
                Result.Success(
                    CurrencyRates(
                        base = "eur",
                        date = "10.19.19",
                        ratesMap = mapOf(
                            "USD" to BigDecimal("1.5"),
                            "AMD" to BigDecimal("0.5")
                        )
                    )
                )
            )
            return ratesList
        }

        override val ratesList: MutableLiveData<Result<CurrencyRates>> = MutableLiveData()
    }


    @ExperimentalCoroutinesApi
    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        MockitoAnnotations.initMocks(this)
        whenEver(mockCurrencyExtras.getFlagResourceFrom(ArgumentMatchers.anyString())).thenReturn(0)
        whenEver(mockCurrencyExtras.getNameFrom(ArgumentMatchers.anyString())).thenReturn("")
        subjectViewModel = MainViewModel(
            mockRepository,
            mockCurrencyExtras
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        subjectViewModel.cancelFetch()
    }

    /**
     * ViewModel fetches new rates every 1000 msc
     */
    @Test
    fun fetchRepeatTest() = testDispatcher.runBlockingTest {
        val viewStateObserver = LoggingObserver<RatesViewStateType>()

        subjectViewModel.viewState.observeForever(viewStateObserver)

        subjectViewModel.fetchRates()
        advanceTimeBy(50000)
        subjectViewModel.cancelFetch()

        val callCount = viewStateObserver.updatedCount

        assertThat(callCount, greaterThanOrEqualTo(50))
        assertThat(callCount, greaterThanOrEqualTo(53))
        subjectViewModel.cancelFetch()
    }

    /**
     * cancelFetch stops subsequent requests
     */
    @Test
    fun cancelFetchTest() = testDispatcher.runBlockingTest {
        val viewStateObserver = LoggingObserver<RatesViewStateType>()

        subjectViewModel.viewState.observeForever(viewStateObserver)

        subjectViewModel.fetchRates()
        advanceTimeBy(50000)
        subjectViewModel.cancelFetch()

        val oldCallCount = viewStateObserver.updatedCount

        advanceTimeBy(50000)
        val callCount = viewStateObserver.updatedCount

        assertThat(oldCallCount, equalTo(callCount))
    }


    private fun <T> whenEver(methodCall: T): OngoingStubbing<T> {
        return `when`(methodCall)
    }


    /**
     * Observer logs any values it receives
     */
    private class LoggingObserver<T> : Observer<T> {
        val value: T?
            get() = allValues.last()
        val allValues = ArrayList<T?>()
        val updatedCount
            get() = allValues.size

        override fun onChanged(t: T?) {
            allValues.add(t)
        }
    }
}

