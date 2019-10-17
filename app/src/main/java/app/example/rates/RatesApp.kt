package app.example.rates

import app.example.rates.di.DaggerAppComponent
import com.blongho.country_data.World
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication
import timber.log.Timber
import timber.log.Timber.DebugTree


class RatesApp : DaggerApplication() {

    init {
        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        }

    }

    override fun onCreate() {
        super.onCreate()
        World.init(this)
    }
    override fun applicationInjector(): AndroidInjector<out DaggerApplication> = DaggerAppComponent
        .builder()
        .application(this)
        .build()
}