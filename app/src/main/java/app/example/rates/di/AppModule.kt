package app.example.rates.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import app.example.rates.BuildConfig
import app.example.rates.repository.RatesRepository
import app.example.rates.repository.RatesRepositoryImpl
import app.example.rates.service.ApiService
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import javax.inject.Provider
import javax.inject.Singleton

@Module
open class AppModule {

    private val baseUrl = "https://revolut.duckdns.org"

    @Provides
    @Singleton
    open fun provideOkHttpClient(): OkHttpClient {

        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY

        return OkHttpClient.Builder().apply {
            if (BuildConfig.DEBUG) {
                this.addInterceptor(logging)
            }
        }.build()
    }

    @Provides
    @Singleton
    open fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(
                MoshiConverterFactory.create(
                    Moshi.Builder()
                        .build()
                )
            )
            .build()

    @Provides
    @Singleton
    open fun provideApiService(retrofit: Retrofit): ApiService =
        retrofit.create()

    @Provides
    @Singleton
    open fun provideRepository(apiService: ApiService): RatesRepository =
        RatesRepositoryImpl(apiService)


    @Provides
    fun provideViewModelFactory(
        providers: Map<Class<out ViewModel>, @JvmSuppressWildcards Provider<ViewModel>>
    ): ViewModelProvider.Factory = AppViewModelFactory(providers)
}