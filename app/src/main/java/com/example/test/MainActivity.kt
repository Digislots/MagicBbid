package com.example.test

import android.app.Activity
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.example.mylibrary.databinding.ActivityMaBinding
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback
import com.magicbid.app.App
import com.magicbid.app.MagicBidSdk
import com.magicbid.app.TemplateView

class MainActivity : AppCompatActivity() {

    private lateinit var magicBidSdk: MagicBidSdk
    lateinit var bannerad: LinearLayout
    lateinit var openapp: TextView
    lateinit var templateView: TemplateView
    var rewardedInterstitialAd: RewardedInterstitialAd? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bannerad = findViewById(R.id.bannerad)
        templateView = findViewById(R.id.template)
        magicBidSdk = MagicBidSdk(this)
        magicBidSdk.showinterStitalad()




    }


}