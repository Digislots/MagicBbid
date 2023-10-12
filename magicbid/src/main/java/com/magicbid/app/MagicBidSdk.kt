package com.magicbid.app
import android.app.Activity
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.WifiManager
import android.os.Build
import android.text.format.Formatter
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
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
    val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
    val ipAddress: String = Formatter.formatIpAddress(wifiManager.connectionInfo.ipAddress)

    var mInterstitialAd: InterstitialAd? = null

    fun adaptiveBanner(activity: Activity, linearLayout: LinearLayout) {
        if (result != null) {
            try {
                val adsList = result.filter { it.ads_type == 1 }
                Log.d("adlist", adsList.toString())
                sortedAdsList = adsList.sortedByDescending { it.cpm }.toMutableList()
                Log.d("sortedAdsList", sortedAdsList.toString())
                if (sortedAdsList.isNotEmpty()) {
                    loadAdd(activity, linearLayout, sortedAdsList[currentAddPosition].adscode)
                }
            } catch (e: Exception) {
                Log.d("dvbvb", e.toString())
            }
        }
    }
    fun loadAdd(activity: Activity, linearLayout: LinearLayout, adId: String) {
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
                postData(adId)
            }
            override fun onAdFailedToLoad(adError: LoadAdError) {
                Log.d("banner_ad", adError.message)
                Log.d("banner_ad", sortedAdsList[currentAddPosition].cpm.toString())
                Log.d("banner_ad", sortedAdsList[currentAddPosition].adscode)
                if (adError.code == 3) {
                    currentAddPosition++
                    loadAdd(activity, linearLayout, sortedAdsList[currentAddPosition].adscode)
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



    fun inlineBanner(activity: Activity, linearLayout: LinearLayout) {
        if (result != null) {
            try {
                val adsList = result.filter { it.ads_type == 1 }
                Log.d("adlist", adsList.toString())
                sortedAdsList = adsList.sortedByDescending { it.cpm }.toMutableList()
                Log.d("sortedAdsList", sortedAdsList.toString())
                if (sortedAdsList.isNotEmpty()) {
                    inlineloadAdd(activity, linearLayout, sortedAdsList[currentAddPosition].adscode)
                }
            } catch (e: Exception) {
                Log.d("dvbvb", e.toString())
            }
        }
    }

    private fun inlineloadAdd(activity: Activity, linearLayout: LinearLayout, adId: String) {
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
                postData(adId)
            }
            override fun onAdFailedToLoad(adError: LoadAdError) {
                Log.d("banner_ad", adError.message)
                Log.d("banner_ad", sortedAdsList[currentAddPosition].cpm.toString())
                Log.d("banner_ad", sortedAdsList[currentAddPosition].adscode)
                if (adError.code == 3) {
                    currentAddPosition++
                    loadAdd(activity, linearLayout, sortedAdsList[currentAddPosition].adscode)
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


    fun showinterStitalad(listnerInterface: AdListnerInterface) {
        if (result != null) {
            val adsList = result.filter { it.ads_type == 3 }
            Log.d("adlist", adsList.toString())
            sortedAdsList = adsList.sortedByDescending { it.cpm }.toMutableList()
            Log.d("sortedAdsList", sortedAdsList.toString())

            if (sortedAdsList.isNotEmpty()) {

                loadinterstitalad(sortedAdsList[currentAddPosition].adscode,listnerInterface)
            }
        }
    }






    private fun loadinterstitalad(adscode: String, listnerInterface: AdListnerInterface) {



        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(context, adscode, adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                mInterstitialAd = null
                Log.d("InterstitialAd", adError.message)
                Log.d("InterstitialAd", sortedAdsList[currentAddPosition].cpm.toString())
                Log.d("InterstitialAd", sortedAdsList[currentAddPosition].adscode)
                listnerInterface.onAdFailedToLoad(adError)
                if (adError.code == 3) {
                    currentAddPosition++
                    loadinterstitalad(sortedAdsList[currentAddPosition].adscode, listnerInterface)
                }
            }


            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                //Log.d(TAG, 'Ad was loaded.')
                mInterstitialAd = interstitialAd

                if (mInterstitialAd != null) {
                  //  mInterstitialAd?.show(context as Activity)
                    Log.d("InterstitialAd", sortedAdsList[currentAddPosition].cpm.toString())
                    Log.d("InterstitialAd", sortedAdsList[currentAddPosition].adscode)
                    postData(adscode)
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


//            override fun onAdLoaded(interstitialAd: InterstitialAd) {
//                mInterstitialAd = interstitialAd
//
//                if (mInterstitialAd != null) {
//                    mInterstitialAd?.show(context as Activity)
//                    Log.d("InterstitialAd", sortedAdsList[currentAddPosition].cpm.toString())
//                    Log.d("InterstitialAd", sortedAdsList[currentAddPosition].adscode)
//                    postData(adscode)
//                }
//
//                listnerInterface.onAdLoaded(boolean = true)
//            }
        })



    }

    fun adIsLoading():Boolean{
        return mInterstitialAd!=null
    }

    fun showInterstitialAds(){
        if (mInterstitialAd!=null){
            mInterstitialAd!!.show(context as Activity)
        }
    }
    fun showNativeAds(context: Context, view: TemplateView) {
        if (result != null) {
            val adsList = result.filter { it.ads_type == 4 }
            Log.d("adlist", adsList.toString())
            sortedAdsList = adsList.sortedByDescending { it.cpm }.toMutableList()
            Log.d("sortedAdsList", sortedAdsList.toString())

            if (sortedAdsList.isNotEmpty()) {
                loadnativead(context, view, sortedAdsList[currentAddPosition].adscode)
            }
        }
    }
    private fun loadnativead(context: Context, view: TemplateView, adscode: String) {
        val adLoader: AdLoader = AdLoader.Builder(this.context, adscode).forNativeAd {
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
                    loadnativead(context, view, adscode)
                }
            }
        }).build()
        adLoader.loadAd(AdRequest.Builder().build())
    }
    fun sowAdrewarded() {
        if (result != null) {
            val adsList = result.filter { it.ads_type == 5 }
            Log.d("adlist", adsList.toString())
            sortedAdsList = adsList.sortedByDescending { it.cpm }.toMutableList()
            Log.d("sortedAdsList", sortedAdsList.toString())
            if (sortedAdsList.isNotEmpty()) {
                loadrewarded(sortedAdsList[currentAddPosition].adscode)
            }
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
            rewardedInterstitialAd?.show(context as Activity) {
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
        val ai: ApplicationInfo = context.packageManager.getApplicationInfo(
            context.packageName,
            PackageManager.GET_META_DATA
        )
        val app_id = ai.metaData["com.google.android.gms.ads.APPLICATION_ID"]


        if (checkForInternet(context)) {

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
    private fun checkForInternet(context: Context): Boolean {
        val connectivityManager =  context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
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