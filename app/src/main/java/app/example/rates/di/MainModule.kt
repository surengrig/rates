package app.example.rates.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import app.example.rates.helpers.CurrencyExtras
import app.example.rates.repository.RatesRepository
import app.example.rates.ui.main.MainFragment
import app.example.rates.ui.main.MainViewModel
import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module(includes = [MainModule.ProvideViewModel::class])
abstract class MainModule {

    @ContributesAndroidInjector(modules = [InjectViewModel::class])
    abstract fun bind(): MainFragment

    @Module
    class ProvideViewModel {
        @Provides
        @IntoMap
        @ViewModelKey(MainViewModel::class)
        fun provideMainViewModel(
            ratesRepository: RatesRepository,
            currencyExtras: CurrencyExtras
        ): ViewModel =
            MainViewModel(ratesRepository, currencyExtras)

        @Provides
        fun provideCurrencyExtras() = CurrencyExtras()
    }

    @Module
    class InjectViewModel {
        @Provides
        fun provideMainViewModel(
            factory: ViewModelProvider.Factory,
            target: MainFragment
        ) = ViewModelProvider(target, factory)[MainViewModel::class.java]
    }

}