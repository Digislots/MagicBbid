package com.magicbid.app

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.mylibrary.databinding.CardViewDesignBinding
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

class Interstital : AppCompatActivity() {

    private lateinit var binding: CardViewDesignBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = CardViewDesignBinding.inflate(layoutInflater)
        setContentView(binding.root)

        MobileAds.initialize(this) {}


        getInformation()
//
//        binding.next.setOnClickListener {
//            startActivity(Intent(this, NativeActivity::class.java))
//
//        }
    }

    private fun getInformation() {


        val result = Prefs.getResponseAll(applicationContext)
        var maxCpm = 0
        var maxCpmAdscode = ""
        if (result != null) {
            try {
                for (ads in result) {
                    if (ads.ads_type == 3) {
                        if (ads.cpm > maxCpm) {
                            maxCpm = ads.cpm.toInt()
                            maxCpmAdscode = ads.adscode

                            var mInterstitialAd: InterstitialAd? = null

                            var adRequest = AdRequest.Builder().build()

                            InterstitialAd.load(
                                this@Interstital,
                                maxCpmAdscode,
                                adRequest,
                                object : InterstitialAdLoadCallback() {
                                    override fun onAdFailedToLoad(adError: LoadAdError) {

                                        mInterstitialAd = null
                                    }

                                    override fun onAdLoaded(interstitialAd: InterstitialAd) {

                                        mInterstitialAd = interstitialAd
                                        if (mInterstitialAd != null) {
                                            mInterstitialAd?.show(this@Interstital)
                                        } else {
                                            Log.d("TAG", "The interstitial ad wasn't ready yet.")
                                        }
                                    }
                                })

                        }
                    }


                }
            } catch (e: Exception) {
                Log.d("dvbvb", e.toString())
            }
        }


    }
}

