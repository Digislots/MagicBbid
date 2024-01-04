package com.example.test
import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import com.magicbid.app.AdListnerInterface

import com.magicbid.app.MagicBidSdk
import com.magicbid.app.TemplateView


class MainActivity : AppCompatActivity() {
    private lateinit var banner: LinearLayout
    private lateinit var magicBidSdk: MagicBidSdk
    private lateinit var templateView: TemplateView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        banner = findViewById(R.id.baner)
        templateView = findViewById(R.id.template)
        magicBidSdk = MagicBidSdk(this)

//        magicBidSdk.adaptiveBannerAD(this,banner,object : OnInitializationCallback{
//            override fun onLoadedBannerAd(adView: AdView) {
//
//            }
//
//            override fun onFailad(adError: LoadAdError) {
//
//            }
//
//
//        })

        magicBidSdk.showNativeAds(this,templateView,object :AdListnerInterface{
            override fun onAdLoaded(it: NativeAd) {


            }

            override fun onAdFailedToLoad(var1: LoadAdError) {

            }


        })

    }

}