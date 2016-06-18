package org.misumirize.speechtoamazon

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class ScreenReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context != null) {
            val i = Intent(context, AccelerometerService::class.java)
            i.action = intent?.action
            context.startService(i)
        }
    }
}

