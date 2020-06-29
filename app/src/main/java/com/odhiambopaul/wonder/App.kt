package com.odhiambopaul.wonder

import android.app.Application
import com.odhiambopaul.wonder.di.component.ApplicationComponent
import com.odhiambopaul.wonder.di.component.DaggerApplicationComponent
import com.odhiambopaul.wonder.di.module.ApiModule
import com.odhiambopaul.wonder.di.module.DataBaseModule

class App : Application() {
    lateinit var applicationComponent: ApplicationComponent

    companion object {
        lateinit var instance: App
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        applicationComponent = DaggerApplicationComponent
            .builder()
            .apiModule(ApiModule())
            .dataBaseModule(DataBaseModule(this))
            .build()
    }

}