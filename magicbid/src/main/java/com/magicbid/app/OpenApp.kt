package com.magicbid.app

import android.os.Bundle
import android.os.CountDownTimer
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.mylibrary.R

class OpenApp : AppCompatActivity() {

    private var secondsRemaining: Long = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_open_)

        val application = application as? App ?: return

        application.showAdIfAvailable(
            this@OpenApp,
            object : App.OnShowAdCompleteListener {
                override fun onShowAdComplete() {
                    //startMainActivity()
                }
            })

    }
}