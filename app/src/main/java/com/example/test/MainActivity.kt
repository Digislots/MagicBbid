package com.example.test

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd
import com.magicbid.app.AdListnerInterface
import com.magicbid.app.MagicBidSdk
import com.magicbid.app.TemplateView
import java.net.SocketException


class MainActivity : AppCompatActivity() {

    private lateinit var magicBidSdk: MagicBidSdk
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        magicBidSdk = MagicBidSdk(this)
        magicBidSdk.loadinterStitalad(object : AdListnerInterface {
            override fun onAdClicked() {
                Log.d("magicbid","onAdClicked")

            }

            override fun onAdClosed() {
                Log.d("magicbid","onAdClosed")
            }

            override fun onAdFailedToLoad(var1: LoadAdError) {
                Log.d("magicbid","onAdFailedToLoad")
            }

            override fun onAdImpression() {
                Log.d("magicbid","onAdImpression")
            }

            override fun onAdLoaded(boolean: Boolean) {
                Log.d("magicbid","onAdLoaded")
            }

            override fun onAdDismissedFullScreenContent() {
                Log.d("magicbid","onAdDismissedFullScreenContent")
            }

            override fun onAdFailedToShowFullScreenContent(var1: AdError) {
                Log.d("magicbid","onAdFailedToShowFullScreenContent")
            }

            override fun onAdShowedFullScreenContent() {
                Log.d("magicbid","onAdShowedFullScreenContent")
            }

            override fun onApiFailed() {
                Log.d("magicbid","onApiFailed")
            }


        })


    }
    override fun onBackPressed() {
        if (magicBidSdk.setAutoAdCacheing()) {
            magicBidSdk.showInterstitialAds()
            super.onBackPressed()
        }else{
            Log.d("magicbid","vfdvf")
        }
    }



}