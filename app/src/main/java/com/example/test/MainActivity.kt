package com.example.test

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.admanager.AdManagerAdView
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

            }

            override fun onAdFailedToLoad(var1: String) {
                Toast.makeText(this@MainActivity, "ads failed", Toast.LENGTH_SHORT).show()

            }





            override fun onAdImpression() {
                Log.e("Interstitial___", "onAdImpression")
            }

            override fun onAdLoaded(boolean: Boolean) {
                //magicBidSdk.showInterstitialAds()
                Toast.makeText(this@MainActivity, "ads loaded", Toast.LENGTH_SHORT).show()

                Log.e("Interstitial___", "onAdLoaded")
            }

            override fun onAdDismissedFullScreenContent() {
                Toast.makeText(this@MainActivity, "dismissed", Toast.LENGTH_SHORT).show()
                finish()
            }

            override fun onAdFailedToShowFullScreenContent(var1: String) {

            }




            override fun onAdShowedFullScreenContent() {

            }

        })


//
        val parentView = banner.parent as? ViewGroup
////
////// Check if parentView is not null and is a ViewGroup
        parentView?.let {
            // Check if the parent view doesn't already contain a banner ad
            if (!isAdAlreadyLoaded(it)) {
                // Call the mediation function from magicBidSdk
                magicBidSdk.allForBannerAd(it, banner,object : OnInitializationCallback {
                    override fun onLoadedBannerAd(adManagerAdView: AdManagerAdView) {
                        Log.e("bannerAd___", "onLoadedBannerAd")
                    }

                    override fun onFailad(adError: LoadAdError) {
                        Log.e("bannerAd___", "onFailad")
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

    override fun onBackPressed() {

        if (magicBidSdk.adIsLoaded()){
            val builder = AlertDialog.Builder(this@MainActivity)
            builder.setTitle("Alert")

            builder.setMessage("Are you sure want to exit from this page")
                .setCancelable(true)
                .setNegativeButton("No") { dialog: DialogInterface, id: Int -> dialog.cancel() }
                .setPositiveButton("Yes"){ dialog: DialogInterface, id: Int -> magicBidSdk.showInterstitialAds() }
            /* builder.setOnDismissListener {
                 if (sharedPreference.getInt(this@SmartTools, "samrt_demo_shown") === 0) {
                     demoDialog(this@SmartTools)
                     sharedPreference.putInt(this@SmartTools, "samrt_demo_shown", 1)
                 }
             }*/
            builder.show()
            //Toast.makeText(this@MainActivity, "ads Loaded", Toast.LENGTH_SHORT).show()
            //magicBidSdk.showInterstitialAds()
        }else{
            super.onBackPressed()
        }




    }
}