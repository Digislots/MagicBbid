package com.example.test

import android.app.Activity
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.mylibrary.databinding.ActivityMaBinding
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback
import com.magicbid.app.AdListnerInterface
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


        magicBidSdk.showinterStitalad( object : AdListnerInterface {

            override fun onAdClosed() {
                super.onAdClosed()
            }

            override fun onAdFailedToLoad(var1: LoadAdError) {
                super.onAdFailedToLoad(var1)
            }

            override fun onAdImpression() {
                super.onAdImpression()
            }

            override fun onAdLoaded(boolean: Boolean) {
                super.onAdLoaded(boolean)
                Toast.makeText(this@MainActivity,"ads loaded", Toast.LENGTH_SHORT).show()
            }




            override fun onAdDismissedFullScreenContent() {
                super.onAdDismissedFullScreenContent()
                Toast.makeText(this@MainActivity,"onAdDismissedFullScreenContent", Toast.LENGTH_SHORT).show()
                finish()
            }

            override fun onAdFailedToShowFullScreenContent(var1: AdError) {
                super.onAdFailedToShowFullScreenContent(var1)
            }

            override fun onAdShowedFullScreenContent() {
                super.onAdShowedFullScreenContent()
                Toast.makeText(this@MainActivity,"onAdShowedFullScreenContent", Toast.LENGTH_SHORT).show()
            }

        })

//        magicBidSdk.showinterStitalad(object : AdListnerInterface{
//
//            override fun onAdClosed() {
//                super.onAdClosed()
//            }
//
//
//        })
//
//        if (magicBidSdk.adIsLoading()) {
//            magicBidSdk.showInterstitialAdsLocal()
//        }




    }
    override fun onBackPressed() {
        if (magicBidSdk.adIsLoading()) {
            magicBidSdk.showInterstitialAds()
//        }
            super.onBackPressed()
        }
    }



}