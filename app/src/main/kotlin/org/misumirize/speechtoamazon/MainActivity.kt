package org.misumirize.speechtoamazon

import android.app.KeyguardManager
import android.content.Intent
import android.os.Bundle
import android.os.PowerManager
import android.speech.RecognizerIntent
import android.support.v7.app.AppCompatActivity
import android.widget.ArrayAdapter
import android.widget.ListView

class MainActivity : AppCompatActivity() {

    var adapter: ArrayAdapter<String>? = null
    var keyguardLock: KeyguardManager.KeyguardLock? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, android.R.id.text1)
        val wordList = findViewById(R.id.word_list) as ListView
        wordList.adapter = adapter

    }

    override fun onStart() {
        super.onStart()
        val keyguardManager = getSystemService(KEYGUARD_SERVICE) as KeyguardManager
        keyguardLock = keyguardManager.newKeyguardLock(resources.getString(R.string.app_name))
        keyguardLock!!.disableKeyguard()
        keyguardManager.exitKeyguardSecurely {
            val powerManager = getSystemService(POWER_SERVICE) as PowerManager
            val wakeLock = powerManager.newWakeLock(
                    PowerManager.FULL_WAKE_LOCK
                            or PowerManager.ACQUIRE_CAUSES_WAKEUP
                            or PowerManager.ON_AFTER_RELEASE,
                    resources.getString(R.string.app_name))
            wakeLock.acquire(1000)
        }

        val i = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        startActivityForResult(i, 0)
    }

    override fun onPause() {
        super.onPause()
        keyguardLock!!.reenableKeyguard()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                adapter?.clear()
                val results = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                adapter?.addAll(results)
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}

