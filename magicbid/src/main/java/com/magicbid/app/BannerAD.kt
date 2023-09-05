package com.magicbid.app

import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.mylibrary.databinding.BannerAdBinding
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds

class BannerAD : AppCompatActivity() {
    private lateinit var binding: BannerAdBinding
    private lateinit var adView: AdView
    private var initialLayoutComplete = false
    var result: List<Adscode>? = null
    var maxCpm = 0
    private val adSize: AdSize
        get() {
            val display = windowManager.defaultDisplay
            val outMetrics = DisplayMetrics()
            display.getMetrics(outMetrics)
            val density = outMetrics.density
            var adWidthPixels = binding.adViewContainer.width.toFloat()
            if (adWidthPixels == 0f) {
                adWidthPixels = outMetrics.widthPixels.toFloat()
            }
            val adWidth = (adWidthPixels / density).toInt()
            return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = BannerAdBinding.inflate(layoutInflater)
        setContentView(binding.root)
        MobileAds.initialize(this) {}
        adView = AdView(this)
        binding.adViewContainer.addView(adView)
        binding.adViewContainer.viewTreeObserver.addOnGlobalLayoutListener {
            if (!initialLayoutComplete) {
                initialLayoutComplete = true
                //  loadBanner()

                //Getting Data from SP and storing in result
                result = Prefs.getResponseAll(applicationContext)

                //Sorting Decending order 5,4,3, by cpm

                if (result != null) {
                    for (ads in result!!) {
                        try {
                            if (ads.ads_type == 2) {

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


            }
        }
    }

    private fun loadAd(adscode: String) {
        adView.adUnitId = adscode
        adView.setAdSize(adSize)
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
        // onBackPressed()


    }
}


