package com.example.gameoflife

import android.app.Application
import com.example.gameoflife.koin.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import timber.log.Timber

class MainApplication: Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@MainApplication)
            modules(appModule)
        }
        val timberTree = Timber.DebugTree()
        Timber.plant(timberTree)
    }
}