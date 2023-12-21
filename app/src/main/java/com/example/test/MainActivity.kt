package com.example.test

import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.LoadAdError
import com.magicbid.app.AdListnerInterface
import com.magicbid.app.MagicBidSdk



@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {

    private lateinit var magicBidSdk: MagicBidSdk
    private lateinit var layout: LinearLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        layout = findViewById(R.id.banner)

        magicBidSdk = MagicBidSdk(this)

    //    magicBidSdk.inlineBannerAD(this,layout)

        magicBidSdk.showinterStitalad(object : AdListnerInterface {
            override fun onAdClicked() {
                Log.d("magicBid","onAdClicked")

            }

            override fun onAdClosed() {
                Log.d("magicBid","onAdClosed")
            }

            override fun onAdFailedToLoad(var1: LoadAdError) {
                Log.d("magicBid","onAdFailedToLoad")
            }

            override fun onAdImpression() {
                Log.d("magicBid","onAdImpression")
            }

            override fun onAdLoaded(boolean: Boolean) {
                Log.d("magicBid","onAdLoaded")
            }

            override fun onAdDismissedFullScreenContent() {
                Log.d("magicBid","onAdDismissedFullScreenContent")
            }

            override fun onAdFailedToShowFullScreenContent(var1: AdError) {
                Log.d("magicBid","onAdFailedToShowFullScreenContent")
            }

            override fun onAdShowedFullScreenContent() {
                Log.d("magicBid","onAdShowedFullScreenContent")
            }

            override fun onApiFailed() {
                Log.d("magicBid","onApiFailed")
            }


        })
//        if (magicBidSdk.setAutoAdCACHEING()) {
//            magicBidSdk.showInterstitialAds()
//        }else{
//            Log.d("magicBid","***custom text here")
//        }


    }
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (magicBidSdk.setAutoAdCacheing()) {
            magicBidSdk.showInterstitialAds()
            super.onBackPressed()
        }else{
            Log.d("magicBid","magicBid")
        }
    }



}