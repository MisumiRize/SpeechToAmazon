package org.misumirize.speechtoamazon

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Looper
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.subscriptions.Subscriptions

class AccelerometerObservable {

    companion object {

        fun create(context: Context): Observable<SensorEvent> {
            val manager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
            val sensor = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

            return Observable.create {
                val listener = object : SensorEventListener {
                    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                    }

                    override fun onSensorChanged(event: SensorEvent?) {
                        if (event != null) {
                            it.onNext(event)
                        }
                    }
                }

                manager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_NORMAL)
                it.add(Subscriptions.create {
                    if (Looper.getMainLooper() == Looper.myLooper()) {
                        manager.unregisterListener(listener)
                    } else {
                        val worker = AndroidSchedulers.mainThread().createWorker()
                        worker.schedule {
                            manager.unregisterListener(listener)
                            worker.unsubscribe()
                        }
                    }
                })
            }
        }
    }
}

