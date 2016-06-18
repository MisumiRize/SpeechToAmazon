package org.misumirize.speechtoamazon

import android.app.Notification
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import rx.Subscription
import java.util.concurrent.TimeUnit

class AccelerometerService : Service() {

    var subscription: Subscription? = null

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when(intent?.action) {
            Intent.ACTION_SCREEN_ON ->
                if (subscription != null && !subscription!!.isUnsubscribed) {
                    subscription!!.unsubscribe()
                }
            Intent.ACTION_SCREEN_OFF ->
                subscription = AccelerometerObservable.create(this)
                        .map { it.values!!.get(2) }
                        .scan(emptyList<Float>()) { arr, z -> listOfNotNull(z, arr.firstOrNull()) }
                        .skip(2)
                        .map { it[0] - it[1] }
                        .filter { Math.abs(it) >= 5 }
                        .debounce(500, TimeUnit.MILLISECONDS)
                        .subscribe {
                            Log.d(this.javaClass.name, it.toString())
                            val i = Intent(this, MainActivity::class.java)
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(i)
                        }
        }
        startForeground(0, Notification())
        return START_STICKY
    }
}
