package com.magicbid.app

import android.os.Bundle
import android.os.CountDownTimer
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.mylibrary.R

class OpenApp : AppCompatActivity() {

    private var secondsRemaining: Long = 0L
    lateinit var next : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        next = findViewById(R.id.next)
//        next.setOnClickListener{
//            val intent = Intent(this, MainActivity::class.java)
//            startActivity(intent)
//
//        }


        // Create a timer so the SplashActivity will be displayed for a fixed amount of time.
        createTimer(5)
    }

    /**
     * Create the countdown timer, which counts down to zero and show the app open ad.
     *
     * @param seconds the number of seconds that the timer counts down from
     */
    private fun createTimer(seconds: Long) {
        val counterTextView: TextView = findViewById(R.id.timer)
        val countDownTimer: CountDownTimer = object : CountDownTimer(seconds * 1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                secondsRemaining = millisUntilFinished / 1000 + 1
                counterTextView.text = "App is done loading in: $secondsRemaining"
            }

            override fun onFinish() {
                secondsRemaining = 0
                counterTextView.text = "Done."

                val application = application as? App

                // If the application is not an instance of MyApplication, log an error message and
                // start the MainActivity without showing the app open ad.
                if (application == null) {

                    return
                }

                // Show the app open ad.
                application.showAdIfAvailable(
                    this@OpenApp,
                    object : App.OnShowAdCompleteListener {
                        override fun onShowAdComplete() {
                            //startMainActivity()
                        }


                    })
            }
        }
        countDownTimer.start()
    }
//    private fun startMainActivity() {
//        val intent = Intent(this, MainActivity::class.java)
//        startActivity(intent)
//    }

}