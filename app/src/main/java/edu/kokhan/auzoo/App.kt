package edu.kokhan.auzoo

import android.app.Application
import edu.kokhan.auzoo.di.appModule
import edu.kokhan.auzoo.di.fragmentModule
import edu.kokhan.auzoo.di.viewModels
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            modules(listOf(appModule, fragmentModule, viewModels))
        }
    }
}