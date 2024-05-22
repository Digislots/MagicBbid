package com.example.test
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.admanager.AdManagerAdView
import com.google.android.gms.ads.nativead.NativeAd
import com.magicbid.app.AdListnerInterface

import com.magicbid.app.MagicBidSdk
import com.magicbid.app.OnInitializationCallback
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
        // magicBidSdk.forAllNativeAd()
           magicBidSdk.forAllInterstitial(object : AdListnerInterface{
              override fun onAdClicked() {
                  TODO("Not yet implemented")
              }

              override fun onAdFailedToLoad(var1: LoadAdError) {
                  TODO("Not yet implemented")
              }

              override fun onAdImpression() {
                  TODO("Not yet implemented")
              }

              override fun onAdLoaded(boolean: Boolean) {
                  TODO("Not yet implemented")
              }

              override fun onAdDismissedFullScreenContent() {
                  TODO("Not yet implemented")
              }

              override fun onAdFailedToShowFullScreenContent(var1: AdError) {
                  TODO("Not yet implemented")
              }

              override fun onAdShowedFullScreenContent() {
                  TODO("Not yet implemented")
              }

          })


        val parentView = banner.parent as? ViewGroup

// Check if parentView is not null and is a ViewGroup
        parentView?.let {
            // Check if the parent view doesn't already contain a banner ad
            if (!isAdAlreadyLoaded(it)) {
                // Call the mediation function from magicBidSdk
                magicBidSdk.allForBannerAd(it, banner,object :OnInitializationCallback{
                    override fun onLoadedBannerAd(adManagerAdView: AdManagerAdView) {
                        TODO("Not yet implemented")
                    }

                    override fun onFailad(adError: LoadAdError) {
                        TODO("Not yet implemented")
                    }

                })
            }
        } ?: run {
            // Handle case when parentView is null or not a ViewGroup
            Log.e("YourTag", "Parent view not found or is not a ViewGroup")
        }

    }

    private fun isAdAlreadyLoaded(parentView: ViewGroup): Boolean {
        for (i in 0 until parentView.childCount) {
            val child = parentView.getChildAt(i)
            if (child is AdView) {
                // An ad is already loaded in the parent view
                return true
            }
        }
        // No ad found in the parent view
        return false
    }
}