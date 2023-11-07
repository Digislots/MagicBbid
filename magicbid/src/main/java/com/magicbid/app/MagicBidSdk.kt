package com.magicbid.app

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

import android.os.Build
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import com.google.android.gms.ads.AdError
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
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Response
import java.net.Inet4Address
import java.net.NetworkInterface
import java.text.SimpleDateFormat
import java.util.Date

@Suppress("DEPRECATION")
class MagicBidSdk(private var context: Context) {
    private lateinit var sortedAdsList: MutableList<Adscode>
    val result = Prefs.getResponseAll(context)
    var currentAddPosition = 0
    var isOpen = false
    var rewardedInterstitialAd: RewardedInterstitialAd? = null
    private var adView: AdView? = null
    val formatter = SimpleDateFormat("yyyy-MM-dd")
    val date = Date()
    val currentdate = formatter.format(date)
    private lateinit var listnerInterface: AdListnerInterface



    init {
        ApiSingleton.initialize(context)

    }

    enum class AdType {

        ADAPTIVEBANNER,
        INLINEBANNER,
        INTERSTITIAL,
        NATIVE,
        REWARDED

    }

    fun loadAd(adType: AdType,listnerInterface: AdListnerInterface) {
        when (adType) {
            AdType.ADAPTIVEBANNER -> Unit
            AdType.INLINEBANNER -> Unit

            AdType.INTERSTITIAL ->  showInterstitialAd(listnerInterface)
            AdType.NATIVE -> Unit

            AdType.REWARDED -> showAdRewarded(listnerInterface)
        }

    }

    init {

    }


    var ipAddress = "0.0.0.0"
    //= Formatter.formatIpAddress(wifiManager.connectionInfo.ipAddress)


    var mInterstitialAd: InterstitialAd? = null

    private fun adaptiveBanner(activity: Activity, linearLayout: LinearLayout) {
        if (result != null) {
            try {
                val adsList = result.filter { it.ads_type == 1 }
                Log.d("adlist", adsList.toString())
                sortedAdsList = adsList.sortedByDescending { it.cpm }.toMutableList()
                Log.d("sortedAdsList", sortedAdsList.toString())
                if (sortedAdsList.isNotEmpty()) {
                    loadAdd(
                        activity,
                        linearLayout,
                        sortedAdsList[currentAddPosition].adscode,
                        sortedAdsList[currentAddPosition].ads_id
                    )
                }
            } catch (e: Exception) {
                Log.d("dvbvb", e.toString())
            }
        }
    }

    fun loadAdd(activity: Activity, linearLayout: LinearLayout, adId: String, adsId: Int) {
        Log.d("currentposition", "currentAddPosition : $currentAddPosition")
        adView = AdView(activity)
        adView!!.adUnitId = adId
        Log.d("adidddd", adId)
        linearLayout.removeAllViews()
        linearLayout.addView(adView)
        val adSize = getAdSize(activity, linearLayout)
        adView!!.setAdSize(adSize)
        val adRequest = AdRequest.Builder().build()
        adView!!.loadAd(adRequest)
        adView!!.adListener = object : AdListener() {
            override fun onAdLoaded() {
                postData(adsId)
            }

            override fun onAdFailedToLoad(adError: LoadAdError) {
                Log.d("banner_ad", adError.message)
                Log.d("banner_ad", sortedAdsList[currentAddPosition].cpm.toString())
                Log.d("banner_ad", sortedAdsList[currentAddPosition].adscode)
                //postData(adId)
                if (adError.code == 3) {
                    //postData(adId)

                    if (sortedAdsList.size - 1 > currentAddPosition) {
                        currentAddPosition++
                        loadAdd(
                            activity,
                            linearLayout,
                            sortedAdsList[currentAddPosition].adscode,
                            sortedAdsList[currentAddPosition].ads_id
                        )
                    }

                }
            }

            override fun onAdClicked() {

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


    private fun inlineBanner(activity: Activity, linearLayout: LinearLayout) {
        if (result != null) {
            try {
                val adsList = result.filter { it.ads_type == 1 }
                Log.d("adlist", adsList.toString())
                sortedAdsList = adsList.sortedByDescending { it.cpm }.toMutableList()
                Log.d("sortedAdsList", sortedAdsList.toString())
                if (sortedAdsList.isNotEmpty()) {
                    inlineloadAdd(
                        activity,
                        linearLayout,
                        sortedAdsList[currentAddPosition].adscode,
                        sortedAdsList[currentAddPosition].ads_id
                    )
                }
            } catch (e: Exception) {
                Log.d("dvbvb", e.toString())
            }
        }
    }

    private fun inlineloadAdd(
        activity: Activity,
        linearLayout: LinearLayout,
        adId: String,
        adsId: Int
    ) {
        Log.d("currentposition", "currentAddPosition : $currentAddPosition")
        adView = AdView(activity)
        adView!!.adUnitId = adId
        Log.d("adidddd", adId)
        linearLayout.removeAllViews()
        linearLayout.addView(adView)
        val adSize = inlinegetAdSize(activity, linearLayout)
        adView!!.setAdSize(adSize)
        val adRequest = AdRequest.Builder().build()
        adView!!.loadAd(adRequest)
        adView!!.adListener = object : AdListener() {
            override fun onAdLoaded() {
                postData(adsId)
            }

            override fun onAdFailedToLoad(adError: LoadAdError) {
                Log.d("banner_ad", adError.message)
                Log.d("banner_ad", sortedAdsList[currentAddPosition].cpm.toString())
                Log.d("banner_ad", sortedAdsList[currentAddPosition].adscode)
                if (adError.code == 3) {
                    //    currentAddPosition++

                    if (sortedAdsList.size - 1 > currentAddPosition) {
                        currentAddPosition++
                        inlineloadAdd(
                            activity,
                            linearLayout,
                            sortedAdsList[currentAddPosition].adscode,
                            sortedAdsList[currentAddPosition].ads_id
                        )

                    }


                }
            }

            override fun onAdClicked() {

            }
        }
    }

    fun inlinegetAdSize(activity: Activity, linearLayout: LinearLayout): AdSize {
        val display = activity.windowManager.defaultDisplay
        val outMetrics = DisplayMetrics()
        display.getMetrics(outMetrics)
        val density = outMetrics.density
        var adWidthPixels = linearLayout.width.toFloat()
        if (adWidthPixels == 0f) {
            adWidthPixels = outMetrics.widthPixels.toFloat()
        }
        val adWidth = (adWidthPixels / density).toInt()
        return AdSize.getCurrentOrientationInlineAdaptiveBannerAdSize(activity, adWidth)
    }


    private fun showInterstitialAd(listenerInterface1:AdListnerInterface) {
        listnerInterface = listenerInterface1
        if (result != null) {
            val adsList = result.filter { it.ads_type == 3 }
            Log.d("adlist", adsList.toString())
            sortedAdsList = adsList.sortedByDescending { it.cpm }.toMutableList()
            Log.d("sortedAdsList", sortedAdsList.toString())

            if (sortedAdsList.isNotEmpty()) {

                loadinterstitalad(
                    sortedAdsList[currentAddPosition].adscode,
                    listnerInterface,
                    sortedAdsList[currentAddPosition].ads_id
                )
            }
        }
    }


    private fun loadinterstitalad(
        adscode: String,
        listnerInterface: AdListnerInterface,
        adsId: Int
    ) {


        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(context, adscode, adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                mInterstitialAd = null
                Log.d("InterstitialAd", adError.message)
                Log.d("InterstitialAd", sortedAdsList[currentAddPosition].cpm.toString())
                Log.d("InterstitialAd", sortedAdsList[currentAddPosition].adscode)
                listnerInterface.onAdFailedToLoad(adError)
                if (adError.code == 3) {
                    // currentAddPosition++

                    if (sortedAdsList.size - 1 > currentAddPosition) {
                        currentAddPosition++
                        loadinterstitalad(
                            sortedAdsList[currentAddPosition].adscode,
                            listnerInterface,
                            sortedAdsList[currentAddPosition].ads_id
                        )
                    }


                }
            }


            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                //Log.d(TAG, 'Ad was loaded.')
                mInterstitialAd = interstitialAd

                if (mInterstitialAd != null) {
                      mInterstitialAd?.show(context as Activity)
                    Log.d("InterstitialAd", sortedAdsList[currentAddPosition].cpm.toString())
                    Log.d("InterstitialAd", sortedAdsList[currentAddPosition].adscode)
                    postData(adsId)
                }

                mInterstitialAd?.fullScreenContentCallback =
                    object : FullScreenContentCallback() {
                        override fun onAdClicked() {
                            // Called when a click is recorded for an ad.
                            //Log.d(TAG, "Ad was clicked.")
                            listnerInterface.onAdClicked()
                        }

                        override fun onAdDismissedFullScreenContent() {
                            // Called when ad is dismissed.
                            //Log.d(TAG, "Ad dismissed fullscreen content.")
                            mInterstitialAd = null
                            listnerInterface.onAdDismissedFullScreenContent()

                        }

                        override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                            // Called when ad fails to show.
                            //Log.e(TAG, "Ad failed to show fullscreen content.")
                            mInterstitialAd = null
                            listnerInterface.onAdFailedToShowFullScreenContent(adError)
                        }

                        override fun onAdImpression() {
                            // Called when an impression is recorded for an ad.
                            //Log.d(TAG, "Ad recorded an impression.")
                            listnerInterface.onAdImpression()
                        }

                        override fun onAdShowedFullScreenContent() {
                            // Called when ad is shown.
                            //Log.d(TAG, "Ad showed fullscreen content.")
                            listnerInterface.onAdShowedFullScreenContent()
                        }
                    }


                listnerInterface.onAdLoaded(boolean = true)

            }

        })


    }

    fun adIsLoading(): Boolean {
        return mInterstitialAd != null
    }

    fun showInterstitialAds() {
        if (mInterstitialAd != null) {
            mInterstitialAd!!.show(context as Activity)
        }
    }

    private fun showNativeAds(context: Context, view: TemplateView) {
        if (result != null) {
            val adsList = result.filter { it.ads_type == 4 }
            Log.d("adlist", adsList.toString())
            sortedAdsList = adsList.sortedByDescending { it.cpm }.toMutableList()
            Log.d("sortedAdsList", sortedAdsList.toString())

            if (sortedAdsList.isNotEmpty()) {
                loadnativead(
                    context,
                    view,
                    sortedAdsList[currentAddPosition].adscode,
                    sortedAdsList[currentAddPosition].ads_id
                )
            }
        }
    }

    private fun loadnativead(context: Context, view: TemplateView, adscode: String, adsId: Int) {
        val adLoader: AdLoader = AdLoader.Builder(this.context, adscode).forNativeAd {
//                val styles =
//                    NativeTemplateStyle.Builder().withMainBackgroundColor(context.resources.getColor(R.color.white)).build()
//                val template: TemplateView = findViewById(R.id.my_template)
//                view.setStyles(styles)
            view.setNativeAd(it)
            view.visibility = View.VISIBLE
            postData(adsId)
        }.withAdListener(object : AdListener() {
            override fun onAdFailedToLoad(loadAdError: LoadAdError) {

                Log.d("loadnativead", sortedAdsList[currentAddPosition].cpm.toString())
                Log.d("loadnativead", sortedAdsList[currentAddPosition].adscode)
                if (loadAdError.code == 3) {

                    if (sortedAdsList.size - 1 > currentAddPosition) {
                        currentAddPosition++
                        loadnativead(
                            context,
                            view,
                            sortedAdsList[currentAddPosition].adscode,
                            sortedAdsList[currentAddPosition].ads_id
                        )
                    }


                    //loadnativead(context, view, adscode)


                }
            }
        }).build()
        adLoader.loadAd(AdRequest.Builder().build())
    }

    private fun showAdRewarded(listenerInterface1:AdListnerInterface) {
        listnerInterface = listenerInterface1
        if (result != null) {
            val adsList = result.filter { it.ads_type == 5 }
            Log.d("adlist", adsList.toString())
            sortedAdsList = adsList.sortedByDescending { it.cpm }.toMutableList()
            Log.d("sortedAdsList", sortedAdsList.toString())
            if (sortedAdsList.isNotEmpty()) {
                loadrewarded(
                    sortedAdsList[currentAddPosition].adscode,
                    listnerInterface,
                    sortedAdsList[currentAddPosition].ads_id
                )
            }
        }
    }

    private fun loadrewarded(adscode: String,listnerInterface: AdListnerInterface, adsId: Int,) {
        RewardedInterstitialAd.load(context,
            adscode,
            AdManagerAdRequest.Builder().build(),
            object : RewardedInterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedInterstitialAd) {
                    rewardedInterstitialAd = ad
                    listnerInterface.onAdLoaded(boolean = true)
                    showRewardedAds(adsId)
                }

                override fun onAdFailedToLoad(adError: LoadAdError) {
                    rewardedInterstitialAd = null
                    listnerInterface.onAdFailedToLoad(adError)
                    if (adError.code == 3) {
                        //currentAddPosition++
                        if (sortedAdsList.size - 1 > currentAddPosition) {
                            currentAddPosition++
                            loadrewarded(
                                sortedAdsList[currentAddPosition].adscode,
                                listnerInterface,
                                sortedAdsList[currentAddPosition].ads_id
                            )
                        }
                    }
                }
            })
    }

    private fun showRewardedAds(adsId: Int) {
        if (rewardedInterstitialAd != null) {
            rewardedInterstitialAd?.show(context as Activity) {
                isOpen = true
            }
            postData(adsId)
            rewardedInterstitialAd!!.fullScreenContentCallback =
                object : FullScreenContentCallback() {
                    override fun onAdDismissedFullScreenContent() {

                    }
                }
        }
    }


    private fun postData(adsId: Int) {


        try {
            val networkInterfaces = NetworkInterface.getNetworkInterfaces()
            while (networkInterfaces.hasMoreElements()) {
                val networkInterface = networkInterfaces.nextElement()
                val inetAddresses = networkInterface.inetAddresses
                while (inetAddresses.hasMoreElements()) {
                    val inetAddress = inetAddresses.nextElement()
                    if (!inetAddress.isLoopbackAddress && inetAddress is Inet4Address) {
                        ipAddress = inetAddress.hostAddress

                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }


        if (checkForInternet(context).not()) {
            Log.d("Internet", "No internet connection")
            return
        }

            val app_id = Prefs.getAppId(context)


            ApiUtilities.getApiInterface()!!
                .postData(ipAddress.toString(), app_id, adsId, currentdate)
                .enqueue(object : retrofit2.Callback<JsonObject> {
                    override fun onResponse(
                        call: Call<JsonObject>,
                        response: Response<JsonObject>
                    ) {

                        response.body().toString()

                    }

                    override fun onFailure(
                        call: Call<JsonObject>,
                        t: Throwable
                    ) {

                    }

                })




    }

    private fun checkForInternet(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
            return when {
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                else -> false
            }
        } else {

            @Suppress("DEPRECATION") val networkInfo =
                connectivityManager.activeNetworkInfo ?: return false
            @Suppress("DEPRECATION")
            return networkInfo.isConnected
        }
    }


}