package com.example.test
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import com.magicbid.app.AdListnerInterface

import com.magicbid.app.MagicBidSdk
import com.magicbid.app.OnInitializationCallback
import com.magicbid.app.TemplateView


class MainActivity : AppCompatActivity() {
    private lateinit var banner: LinearLayout
    private lateinit var magicBidSdk: MagicBidSdk
    private lateinit var templateView: TemplateView
    private lateinit var adSize: AdSize
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        banner = findViewById(R.id.baner)
        templateView = findViewById(R.id.template)
        magicBidSdk = MagicBidSdk(this)
        adSize = AdSize(300,500)


        magicBidSdk.adaptiveBannerAD(this,adSize,object : OnInitializationCallback {
            override fun onLoadedBannerAd(adView: AdView) {
                banner.addView(adView)


            }

            override fun onFailad(adError: LoadAdError) {

            }


        })

//        magicBidSdk.showNativeAds(this,object :AdListnerInterface{
//            override fun onAdLoaded(it: NativeAd) {
//
//                templateView.setNativeAd(it)
//                templateView.visibility = View.VISIBLE
//
//            }
//
//            override fun onAdFailedToLoad(var1: LoadAdError) {
//
//            }
//
//
//        })

    }

}