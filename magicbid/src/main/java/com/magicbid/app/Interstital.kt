package com.magicbid.app

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mylibrary.databinding.CardViewDesignBinding
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

class Interstital : AppCompatActivity() {

    private lateinit var binding: CardViewDesignBinding
    var result: List<Adscode>? = null

    var maxCpm = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = CardViewDesignBinding.inflate(layoutInflater)
        setContentView(binding.root)

        MobileAds.initialize(this) {}

        //Getting Data from SP and storing in result
        result = Prefs.getResponseAll(applicationContext)

        //Sorting Decending order 5,4,3, by cpm

        if (result != null) {
            for (ads in result!!) {
                try {
                    if (ads.ads_type == 3) {

//                        Collections.sort(result, Comparator<Adscode> { obj1, obj2 ->
//                            return@Comparator obj2.cpm.compareTo(obj1.cpm)
//                        })

                        //get adscode by position
                        loadAd(result!![maxCpm].adscode)
                    }
                } catch (e: Exception) {
                    Log.d("dvbvb", e.toString())
                }
            }
        }


        //getInformation()
//
//        binding.next.setOnClickListener {
//            startActivity(Intent(this, NativeActivity::class.java))
//
//        }
    }

    private fun loadAd(adscode: String) {

        var mInterstitialAd: InterstitialAd? = null

        var adRequest = AdRequest.Builder().build()

        //Toast.makeText(this@Interstital, "Ads loading ...", Toast.LENGTH_SHORT).show()
        InterstitialAd.load(
            this@Interstital,
            adscode,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {

                    mInterstitialAd = null
                    if (adError.message == "No fill."){

                        //IF NO add increase maxCpm by 1 or ++1
                        maxCpm++

                        //calling same function with updated adscode
                        result?.get(maxCpm)?.let { loadAd(it.adscode) }
                    }
                    //  Toast.makeText(this@Interstital, "ads not loaded", Toast.LENGTH_SHORT).show()
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {

                    mInterstitialAd = interstitialAd
                    if (mInterstitialAd != null) {
                        mInterstitialAd?.show(this@Interstital)


                        onBackPressed()

                    } else {
                        Log.d("TAG", "The interstitial ad wasn't ready yet.")
                    }
                }
            })
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


                        }
                    }


                }
            } catch (e: Exception) {
                Log.d("dvbvb", e.toString())
            }
        }


    }
}

