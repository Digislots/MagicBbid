package com.magicbid.app

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.mylibrary.databinding.RewardAdBinding
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback

class RewardedAD : AppCompatActivity() {
    private lateinit var binding: RewardAdBinding
    private var rewardedAd: RewardedAd? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = RewardAdBinding.inflate(layoutInflater)
        setContentView(binding.root)

        MobileAds.initialize(this) {}
        getInformation()

    }

    private fun getInformation() {

        val result = Prefs.getResponseAll(applicationContext)
        var maxCpm = 0
        var maxCpmAdscode = ""

        if (result != null) {

            for (ads in result) {
                try {

                    if (ads.ads_type == 5) {
                        if (ads.cpm > maxCpm) {
                            maxCpm = ads.cpm.toInt()
                            maxCpmAdscode = ads.adscode

                            var adRequest = AdRequest.Builder().build()
                            RewardedAd.load(this@RewardedAD,
                                maxCpmAdscode,
                                adRequest,
                                object : RewardedAdLoadCallback() {
                                    override fun onAdFailedToLoad(adError: LoadAdError) {
                                        rewardedAd = null
                                    }

                                    override fun onAdLoaded(ad: RewardedAd) {
                                        rewardedAd = ad
                                    }
                                })
                        }
                    }
                } catch (e: Exception) {
                    Log.d("dvbvb", e.toString())
                }


            }


        }


    }

}