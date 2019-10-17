package app.example.rates.di

import app.example.rates.RatesApp
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AndroidSupportInjectionModule::class,
        AppModule::class,
        MainModule::class
    ]
)

interface AppComponent : AndroidInjector<RatesApp> {
    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: RatesApp): Builder

        fun build(): AppComponent
    }
}
