package com.example.test

import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd
import com.magicbid.app.AdListnerInterface
import com.magicbid.app.MagicBidSdk
import com.magicbid.app.TemplateView
import java.net.SocketException


class MainActivity : AppCompatActivity() {

    private lateinit var magicBidSdk: MagicBidSdk
    lateinit var bannerad: LinearLayout
    lateinit var inline: LinearLayout
    lateinit var openapp: TextView
    lateinit var templateView: TemplateView
    var rewardedInterstitialAd: RewardedInterstitialAd? = null
    var ip : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)








        bannerad = findViewById(R.id.bannerad)
        templateView = findViewById(R.id.template)
        inline = findViewById(R.id.inbannerad)



        magicBidSdk = MagicBidSdk(this)
        magicBidSdk.loadAd(MagicBidSdk.AdType.INTERSTITIAL,object :AdListnerInterface{

        })
//        magicBidSdk.loadAd(MagicBidSdk.AdType.REWARDED,object :AdListnerInterface{
//
//        })




//
//        magicBidSdk.adaptiveBanner(this,bannerad)
//        magicBidSdk.inlineBanner(this,inline)
//        magicBidSdk.showNativeAds(this,templateView)


//        magicBidSdk.showinterStitalad( object : AdListnerInterface {
//            override fun onAdClicked() {
//                super.onAdClicked()
//            }
//
//            override fun onAdClosed() {
//                super.onAdClosed()
//            }
//
//            override fun onAdFailedToLoad(var1: LoadAdError) {
//                super.onAdFailedToLoad(var1)
//            }
//
//            override fun onAdImpression() {
//                super.onAdImpression()
//            }
//
//            override fun onAdLoaded(boolean: Boolean) {
//                super.onAdLoaded(boolean)
//            }
//
//            override fun onAdDismissedFullScreenContent() {
//                super.onAdDismissedFullScreenContent()
//            }
//
//            override fun onAdFailedToShowFullScreenContent(var1: AdError) {
//                super.onAdFailedToShowFullScreenContent(var1)
//            }
//
//            override fun onAdShowedFullScreenContent() {
//                super.onAdShowedFullScreenContent()
//            }
//        })
//
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
//            magicBidSdk.showInterstitialAds()
//        }




    }




}