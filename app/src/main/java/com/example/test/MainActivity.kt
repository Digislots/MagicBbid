package com.example.test

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.magicbid.app.BannerAD
import com.magicbid.app.Interstital
import com.magicbid.app.NativeAD
import com.magicbid.app.OpenApp
import com.magicbid.app.RewardedAD

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<TextView>(R.id.openapp).setOnClickListener{
            startActivity(Intent(this, OpenApp::class.java))


            // startActivity(Intent(this, Interstital::class.java))

        }

        findViewById<TextView>(R.id.banner).setOnClickListener{
            startActivity(Intent(this, BannerAD::class.java))


            // startActivity(Intent(this, Interstital::class.java))

        }

        findViewById<TextView>(R.id.interstital).setOnClickListener{
            startActivity(Intent(this, Interstital::class.java))


            // startActivity(Intent(this, Interstital::class.java))

        }

        findViewById<TextView>(R.id.nativead).setOnClickListener{
            startActivity(Intent(this, NativeAD::class.java))


            // startActivity(Intent(this, Interstital::class.java))

        }

        findViewById<TextView>(R.id.rewarded).setOnClickListener{
            startActivity(Intent(this, RewardedAD::class.java))


            // startActivity(Intent(this, Interstital::class.java))

        }
    }
}