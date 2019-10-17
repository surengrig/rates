package app.example.rates.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import app.example.rates.databinding.FragmentMainBinding
import app.example.rates.R
import app.example.rates.model.CurrencyItem
import app.example.rates.ui.common.ViewState
import com.google.android.material.snackbar.Snackbar
import dagger.android.support.AndroidSupportInjection
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext


class MainFragment : DaggerFragment(), CoroutineScope {
    @Inject
    lateinit var viewModel: MainViewModel
    private var snackBar: Snackbar? = null

    private lateinit var job: Job
    private lateinit var binding: FragmentMainBinding

    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidSupportInjection.inject(this)
        job = Job()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false)
        binding.lifecycleOwner = this

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupObservers()

        val layoutManager = LinearLayoutManager(context)

        recyclerView.layoutManager = layoutManager
        recyclerView.hasFixedSize()
        val adapter = RatesListAdapter(
            object : ClickHandler {
                override fun onItemClick(currencyItem: CurrencyItem) {
                    viewModel.baseLiveData.value = currencyItem
                    recyclerView.scrollToPosition(0)
                }
            },
            viewModel
        )
        adapter.setHasStableIds(true)
        recyclerView.adapter = adapter

        binding.viewModel = viewModel
    }

    override fun onStop() {
        viewModel.cancelFetch()
        super.onStop()
    }

    override fun onStart() {
        super.onStart()
        viewModel.fetchRates()
    }

    private fun setupObservers() {
        viewModel.viewState.observe(this) { viewState ->
            when (viewState) {
                is ViewState.Failure -> handleError(viewState.error)
                is ViewState.Success -> handleSuccess(viewState.data)
            }
        }
    }

    private fun handleSuccess(data: List<CurrencyItem>) {
        snackBar?.dismiss()
        snackBar = null
    }

    private fun handleError(error: Throwable?) {
        view?.let {
            if (snackBar == null) {
                snackBar = Snackbar.make(
                    it,
                    getString(R.string.error),
                    Snackbar.LENGTH_INDEFINITE
                ).apply {
                    show()
                }
            }
        }
    }

}