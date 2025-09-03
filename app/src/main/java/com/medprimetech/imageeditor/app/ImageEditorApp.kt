package com.medprimetech.imageeditor.app

import android.app.Application
import com.medprimetech.imageeditor.app.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class ImageEditorApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@ImageEditorApp)
            modules(appModule)
        }
    }
}