package com.example.test

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.magicbid.app.App
import com.magicbid.app.App.OnShowAdCompleteListener
import com.magicbid.app.Interstital
import com.magicbid.app.NewAct
import com.magicbid.app.SplashActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<TextView>(R.id.text5).setOnClickListener(View.OnClickListener {
            startActivity(Intent(this, SplashActivity::class.java))
            /*   val app=App()
                app.showAdIfAvailable(this, object : OnShowAdCompleteListener {
                    override fun onShowAdComplete() {
                        Log.e("my app", "OnShowAdCompleteListener")
                    }
                })
    */
            Toast.makeText(this, "test", Toast.LENGTH_LONG).show()
        })
        findViewById<TextView>(R.id.textnext).setOnClickListener(View.OnClickListener {
            startActivity(Intent(this, Interstital::class.java))
            /*   val app=App()
                app.showAdIfAvailable(this, object : OnShowAdCompleteListener {
                    override fun onShowAdComplete() {
                        Log.e("my app", "OnShowAdCompleteListener")
                    }
                })
    */
            Toast.makeText(this, "test", Toast.LENGTH_LONG).show()
        })
    }
}