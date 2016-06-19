package org.misumirize.speechtoamazon

import android.app.Application
import android.content.Intent
import android.content.IntentFilter

class MyApplication : Application() {

    override fun onCreate() {
        val intentFilter = IntentFilter()
        intentFilter.addAction(Intent.ACTION_SCREEN_ON)
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF)
        registerReceiver(ScreenReceiver(), intentFilter)

        val i = Intent(this, AccelerometerService::class.java)
        startService(i)
    }
}

