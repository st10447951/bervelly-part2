package com.beverlly.app

import android.app.Application
import com.beverlly.app.data.AuthRepository
import com.beverlly.app.util.NetworkObserver

class BeverllyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AuthRepository.init(this)
        NetworkObserver.start(this)
    }
}
