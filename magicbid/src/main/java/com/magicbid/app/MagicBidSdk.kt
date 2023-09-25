package com.magicbid.app


import android.app.Activity
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.net.wifi.WifiManager
import android.text.format.Formatter
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import com.google.android.gms.ads.AdListener
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


class MagicBidSdk(private var context: Context) {
    private lateinit var sortedAdsList: MutableList<Adscode>
    val result = Prefs.getResponseAll(context)
    var currentAddPosition= 0
    var maxCpmAdscode = ""

    var isOpen = false
    var rewardedInterstitialAd: RewardedInterstitialAd? = null

    private var adView: AdView? = null

    val formatter = SimpleDateFormat("yyyy-MM-dd")
    val date = Date()
    val currentdate = formatter.format(date)


    val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
    val ipAddress: String = Formatter.formatIpAddress(wifiManager.connectionInfo.ipAddress)



    fun adaptiveBanner(activity: Activity, linearLayout: LinearLayout) {
        if (result != null) {

            try {
                val adsList = result.filter { it.ads_type == 1 }
                Log.d("adlist", adsList.toString())
                sortedAdsList = adsList.sortedByDescending { it.cpm }.toMutableList()
                Log.d("sortedAdsList", sortedAdsList.toString())

                if (sortedAdsList.isNotEmpty()){
                    loadAdd(activity, linearLayout, sortedAdsList[currentAddPosition].adscode)

                }




            } catch (e: Exception) {
                Log.d("dvbvb", e.toString())
            }


        }

    }


    fun loadAdd(activity: Activity, linearLayout: LinearLayout, adId: String) {
        Log.d("currentposition","currentAddPosition : $currentAddPosition")
        adView = AdView(activity)
        adView!!.adUnitId = adId
        Log.d("adidddd",adId)
        linearLayout.removeAllViews()
        linearLayout.addView(adView)
        val adSize = getAdSize(activity, linearLayout)
        adView!!.setAdSize(adSize)
        val adRequest = AdRequest.Builder().build()
        adView!!.loadAd(adRequest)

        adView!!.adListener = object : AdListener() {
            override fun onAdLoaded() {
                postData(adId)
                // Ad loaded successfully
            }

            override fun onAdFailedToLoad(adError: LoadAdError) {
                Log.d("banner_ad", adError.message)
                Log.d("banner_ad", sortedAdsList[currentAddPosition].cpm.toString())
                Log.d("banner_ad", sortedAdsList[currentAddPosition].adscode)

                if (adError.code == 3) {

                    currentAddPosition++
                    loadAdd(activity, linearLayout, sortedAdsList[currentAddPosition].adscode)


                    // Remove the current ad from the list and continue with the next highest CPM ad.

                } else {
                    // Ad failed to load for another reason, handle it as needed.
                }
            }

            override fun onAdClicked() {
                // Ad clicked by the user
            }
        }
    }


    fun getAdSize(activity: Activity, linearLayout: LinearLayout): AdSize {
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


            val adsList = result.filter { it.ads_type == 1 }
            Log.d("adlist", adsList.toString())
            sortedAdsList = adsList.sortedByDescending { it.cpm }.toMutableList()
            Log.d("sortedAdsList", sortedAdsList.toString())
            loadinterstitalad(sortedAdsList[currentAddPosition].adscode)

        }


    }

    private fun loadinterstitalad(adscode: String) {
        var mInterstitialAd: InterstitialAd? = null
        val adRequest = AdRequest.Builder().build()

        InterstitialAd.load(
            context,adscode,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {

                    mInterstitialAd = null

                    Log.d("InterstitialAd", adError.message)
                    Log.d("InterstitialAd", sortedAdsList[currentAddPosition].cpm.toString())
                    Log.d("InterstitialAd", sortedAdsList[currentAddPosition].adscode)

                    // "No fill.",
                    if (adError.code == 3) {
                        currentAddPosition++
                        loadinterstitalad(sortedAdsList[currentAddPosition].adscode)

                    }


                }


                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    mInterstitialAd = interstitialAd
                    if (mInterstitialAd != null) {
                        mInterstitialAd?.show(context as Activity)

                        Log.d("InterstitialAd", sortedAdsList[currentAddPosition].cpm.toString())
                        Log.d("InterstitialAd", sortedAdsList[currentAddPosition].adscode)
                        postData(adscode)

                    }
                }
            })
    }


    // nativead() this method will be show native ad


    fun showNativeAds(context: Context, view: TemplateView) {
        if (result != null) {


            val adsList = result.filter { it.ads_type == 1 }
            Log.d("adlist", adsList.toString())
            sortedAdsList = adsList.sortedByDescending { it.cpm }.toMutableList()
            Log.d("sortedAdsList", sortedAdsList.toString())
            loadnativead(context,view,sortedAdsList[currentAddPosition].adscode)



        }
    }

    private fun loadnativead(context: Context, view: TemplateView, adscode: String) {

        val adLoader: AdLoader = AdLoader.Builder(
            this.context, adscode
        )
            .forNativeAd {
//                val styles =
//                    NativeTemplateStyle.Builder().withMainBackgroundColor(context.resources.getColor(R.color.white)).build()
//                val template: TemplateView = findViewById(R.id.my_template)
//                view.setStyles(styles)
                view.setNativeAd(it)
                view.visibility = View.VISIBLE
                postData(adscode)
            }.withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(loadAdError: LoadAdError) {

                    if (loadAdError.code == 3) {
                        currentAddPosition++
                        loadnativead(context,view,adscode)

                    }


                    // Handle the case where no ad was loaded
                    // You can update your UI or take any necessary actions here.
                }
            })
            .build()

        adLoader.loadAd(AdRequest.Builder().build())



    }


    // nativead()close


    //this method will be show rewarded video ad


    fun sowAdrewarded() {


        if (result != null) {
            val adsList = result.filter { it.ads_type == 1 }
            Log.d("adlist", adsList.toString())
            sortedAdsList = adsList.sortedByDescending { it.cpm }.toMutableList()
            Log.d("sortedAdsList", sortedAdsList.toString())



            if (sortedAdsList.isNotEmpty()) {
                loadrewarded(sortedAdsList[currentAddPosition].adscode)
             }






            //loadAd(maxCpmAdscode)




        }
    }

    private fun loadrewarded(adscode: String) {
        RewardedInterstitialAd.load(context,
            adscode,
            AdManagerAdRequest.Builder().build(),
            object : RewardedInterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedInterstitialAd) {
                    rewardedInterstitialAd = ad
                    showRewardedAds(adscode)

                }

                override fun onAdFailedToLoad(adError: LoadAdError) {
                    rewardedInterstitialAd = null

                    if (adError.code == 3) {
                        currentAddPosition++
                        loadrewarded(adscode)

                    }


                }
            })
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


}
