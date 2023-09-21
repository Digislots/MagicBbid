package com.magicbid.app


import android.app.Activity
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.net.wifi.WifiManager
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import android.text.format.Formatter


class MagicBidSdk(private var context: Context) {
    val result = Prefs.getResponseAll(context)
    var maxCpmAdscode = ""
    var maxCpm = 0

    var isOpen = false
    var rewardedInterstitialAd: RewardedInterstitialAd? = null
    var rewardedAd: RewardedAd? = null

    private var adView: AdView? = null

    val formatter = SimpleDateFormat("yyyy-MM-dd")
    val date = Date()
    val currentdate = formatter.format(date)


    val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
    val ipAddress: String = Formatter.formatIpAddress(wifiManager.connectionInfo.ipAddress)




//    Collections.sort(result, Comparator<Any?> { obj1, obj2 ->
//        return@Comparator Integer.valueOf(obj2.cpm).compareTo(Integer.valueOf(obj1.cpm));
//    })

    fun showOpenAD(activity: Activity, onShowAdCompleteListener: App.OnShowAdCompleteListener) {
        val app = activity.application as App
        app.showAdIfAvailable(activity, onShowAdCompleteListener)
    }


    fun adaptiveBanner(activity: Activity, linearLayout: LinearLayout) {
        if (result != null) {

//            Collections.sort(result, Comparator { obj1, obj2 ->
//                return@Comparator Integer.valueOf(obj2.cpm.toInt())
//                    .compareTo(Integer.valueOf(obj1.cpm.toInt()))
//            })


            try {

                for (ads in result) {
                    if (ads.ads_type == 1) {
                        if (ads.cpm > maxCpm) {
                            maxCpm = ads.cpm.toInt()
                            maxCpmAdscode = ads.adscode
                            adView = AdView(activity)
                            adView!!.adUnitId = maxCpmAdscode
                            linearLayout.removeAllViews()
                            linearLayout.addView(adView)
                            val adSize = getAdSize(activity, linearLayout)
                            adView!!.setAdSize(adSize)
                            val adRequest = AdRequest.Builder().build()
                            adView!!.loadAd(adRequest)

                            postData(maxCpmAdscode)
                        }
                    }
                }
            } catch (e: Exception) {
                Log.d("dvbvb", e.toString())
            }
        }

    }


    private fun getAdSize(activity: Activity, linearLayout: LinearLayout): AdSize {
        val display = activity.windowManager.defaultDisplay
        val outMetrics = DisplayMetrics()
        display.getMetrics(outMetrics)
        val density = outMetrics.density
        var adWidthPixels = linearLayout.width.toFloat()
        if (adWidthPixels == 0f) {
            adWidthPixels = outMetrics.widthPixels.toFloat()
        }
        val adWidth = (adWidthPixels / density).toInt()
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(activity, adWidth)
    }


    //this method will be show nterStital ad
    fun showinterStitalad() {

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
                                context,
                                maxCpmAdscode,
                                adRequest,
                                object : InterstitialAdLoadCallback() {
                                    override fun onAdFailedToLoad(adError: LoadAdError) {

                                        mInterstitialAd = null

                                        // "No fill.",
                                        if (adError.message.equals("No fill.")) {
                                            loadInterstitialWithDescendingCPM()
                                        }


                                    }


                                    override fun onAdLoaded(interstitialAd: InterstitialAd) {

                                        mInterstitialAd = interstitialAd
                                        if (mInterstitialAd != null) {
                                            mInterstitialAd?.show(context as Activity)
                                            postData(maxCpmAdscode)

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


    // nativead() this method will be show native ad


    fun showNativeAds(context: Context, view: TemplateView) {
        if (result != null) {
            for (ads in result) {
                try {
                    if (ads.ads_type == 4) {
                        if (ads.cpm > maxCpm) {
                            maxCpm = ads.cpm.toInt()
                            maxCpmAdscode = ads.adscode

                        }
                    }

                } catch (e: Exception) {
                    Log.d("dvbvb", e.toString())
                }


            }


            val adLoader: AdLoader = AdLoader.Builder(
                context, maxCpmAdscode)
                .forNativeAd {
//                val styles =
//                    NativeTemplateStyle.Builder().withMainBackgroundColor(context.resources.getColor(R.color.white)).build()
//                val template: TemplateView = findViewById(R.id.my_template)
//                view.setStyles(styles)
                    view.setNativeAd(it)
                    view.visibility = View.VISIBLE
                    postData(maxCpmAdscode)
                }
                .build()

            adLoader.loadAd(AdRequest.Builder().build())
        }
    }


    // nativead()close


    //this method will be show rewarded video ad


    fun loadAdrewarded() {


        var maxCpm = 0


        if (result != null) {

            for (ads in result) {
                try {
//"ca-app-pub-3940256099942544/5354046379"
                    if (ads.ads_type == 5) {
                        if (ads.cpm > maxCpm) {
                            maxCpm = ads.cpm.toInt()
                            maxCpmAdscode = ads.adscode
                            //loadAd(maxCpmAdscode)
                            RewardedInterstitialAd.load(context,
                                maxCpmAdscode,
                                AdManagerAdRequest.Builder().build(),
                                object : RewardedInterstitialAdLoadCallback() {
                                    override fun onAdLoaded(ad: RewardedInterstitialAd) {
                                        rewardedInterstitialAd = ad
                                        showRewardedAds(maxCpmAdscode)

                                    }

                                    override fun onAdFailedToLoad(adError: LoadAdError) {
                                        rewardedInterstitialAd = null

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

    private fun showRewardedAds(maxCpmAdscode: String) {


        if (rewardedInterstitialAd != null) {
            rewardedInterstitialAd?.show(
                context as Activity,
            ) {
                isOpen = true
            }
            postData(maxCpmAdscode)
            rewardedInterstitialAd!!.fullScreenContentCallback =
                object : FullScreenContentCallback() {
                    override fun onAdDismissedFullScreenContent() {


                    }


                }
        }

    }


    private fun postData(maxCpmAdscode: String) {
        val ai: ApplicationInfo = context.packageManager
            .getApplicationInfo(context.packageName, PackageManager.GET_META_DATA)
        val app_id = ai.metaData["com.google.android.gms.ads.APPLICATION_ID"]

        try {
            CoroutineScope(Dispatchers.IO).launch {
                val res = ApiUtilities.getApiInterface()!!
                    .postData(ipAddress, app_id, maxCpmAdscode, currentdate)
                withContext(Dispatchers.Main) {
                    try {
                        res.body().toString()
                    } catch (e: Exception) {
                        Log.d("dvbvb", e.toString())
                    }
                }
            }
        } catch (e: Exception) {
            Log.d("dvbvb", e.toString())

        }
    }

    fun loadInterstitialWithDescendingCPM() {
        // Sort the result list by CPM in descending order
        result?.sortedByDescending { it.cpm }
        // result.sortByDescending { it.cpm }
        try {
            if (result != null) {
                var mInterstitialAd: InterstitialAd? = null
                for (ads in result) {
                    if (ads.cpm > maxCpm && ads.adscode != maxCpmAdscode) {
                        maxCpm = ads.cpm.toInt()
                        maxCpmAdscode = ads.adscode

                        // Load the interstitial ad with the new maxCpmAdscode
                        val adRequest = AdRequest.Builder().build()

                        InterstitialAd.load(
                            context,
                            maxCpmAdscode,
                            adRequest,
                            object : InterstitialAdLoadCallback() {
                                override fun onAdFailedToLoad(adError: LoadAdError) {
                                    loadInterstitialWithDescendingCPM()

                                }

                                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                                    mInterstitialAd = interstitialAd
                                    if (mInterstitialAd != null) {
                                        mInterstitialAd?.show(context as Activity)
                                        postData(maxCpmAdscode)
                                    }
                                }
                            })
                        break // Exit the loop after loading the next ad
                    }
                }
            }
        } catch (e: Exception) {


        }
    }

}
