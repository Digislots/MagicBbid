package com.magicbid.app

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd
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
    private val result = Prefs.getResponseAll(context)
    private var currentAddPosition = 0
    private var isOpen = false
    private var rewardedInterstitialAd: RewardedInterstitialAd? = null
    private var adView: AdView? = null
    @SuppressLint("SimpleDateFormat")
    private val formatter = SimpleDateFormat("yyyy-MM-dd")
    private val date = Date()
    private val currentdate = formatter.format(date)
    private var adidinterstital: Int = 0
    private var magic :Boolean = false


    private var ipAddress ="0.0.0.0"
    private lateinit var listnerInterface:AdListnerInterface
    private lateinit var onInitializationCallback: OnInitializationCallback
    private var mInterstitialAd: InterstitialAd? = null

    init {
        ApiSingleton.initialize(context)
    }


    fun adaptiveBannerAD(activity: Activity, linearLayout: LinearLayout,onInitializationCallback1: OnInitializationCallback) {
        if (result != null) {
            onInitializationCallback = onInitializationCallback1
            try {
                val adsList = result.filter { it.ads_type == 1 }
                sortedAdsList = adsList.sortedByDescending { it.cpm }.toMutableList()
                if (sortedAdsList.isNotEmpty()) {
                    loadadaptiveBannerAdd(activity, linearLayout, sortedAdsList[currentAddPosition].adscode,sortedAdsList[currentAddPosition].ads_id,onInitializationCallback)
                }
            } catch (e: Exception) {
                Log.d("magick bidSDK",e.toString())
            }
        }
    }

    private fun loadadaptiveBannerAdd(activity: Activity, linearLayout: LinearLayout, adId: String, adsId: Int,onInitializationCallback1: OnInitializationCallback) {
        adView = AdView(activity)
        adView!!.adUnitId = adId
        linearLayout.removeAllViews()
        linearLayout.addView(adView)
        val adSize = getAdSizeaptiveBannerAdd(activity, linearLayout)
        adView!!.setAdSize(adSize)
        val adRequest = AdRequest.Builder().build()
        adView!!.loadAd(adRequest)
        adView!!.adListener = object : AdListener() {
            override fun onAdLoaded() {

                onInitializationCallback1.onLoadedBannerAd(adView!!)

                postData(adsId)
                val handler = Handler(Looper.getMainLooper())
                handler.postDelayed({
                    // Refresh the ad after 5 seconds

                    adaptiveBannerAD(activity, linearLayout,onInitializationCallback1)

                }, 15000) // 5000 milliseconds = 5 seconds

            }

            override fun onAdFailedToLoad(adError: LoadAdError) {
                if (adError.code == 3) {
                    onInitializationCallback1.onFailad(adError)
                    if (sortedAdsList.size-1 > currentAddPosition){
                        currentAddPosition++
                        loadadaptiveBannerAdd(
                            activity,
                            linearLayout,
                            sortedAdsList[currentAddPosition].adscode,
                            sortedAdsList[currentAddPosition].ads_id,onInitializationCallback1
                        )
                    }

                }
            }
        }
    }



    private fun getAdSizeaptiveBannerAdd(activity: Activity, linearLayout: LinearLayout): AdSize {
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





    fun showNativeAds(context: Context, view: TemplateView,listnerInterface1: AdListnerInterface) {
        if (result != null) {
            listnerInterface = listnerInterface1
            val adsList = result.filter { it.ads_type == 4 }
            sortedAdsList = adsList.sortedByDescending { it.cpm }.toMutableList()

            if (sortedAdsList.isNotEmpty()) {
                loadnativead(context, view, sortedAdsList[currentAddPosition].adscode,sortedAdsList[currentAddPosition].ads_id,listnerInterface)
            }
        }
    }

    private fun loadnativead(context: Context, view: TemplateView, adscode: String, adsId: Int,listnerInterface1: AdListnerInterface) {
        val adLoader: AdLoader = AdLoader.Builder(this.context, adscode).forNativeAd {
//                val styles =
//                    NativeTemplateStyle.Builder().withMainBackgroundColor(context.resources.getColor(R.color.white)).build()
//                val template: TemplateView = findViewById(R.id.my_template)
//                view.setStyles(styles)
            view.setNativeAd(it)
            view.visibility = View.VISIBLE
            postData(adsId)
            listnerInterface1.onAdLoaded(it)
            val handler = Handler(Looper.getMainLooper())
            handler.postDelayed({
                // Refresh the ad after 5 seconds

                showNativeAds(context, view,listnerInterface1)

            }, 15000)
        }.withAdListener(object : AdListener() {
            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                if (loadAdError.code == 3) {
                    listnerInterface1.onAdFailedToLoad(loadAdError)

                    if (sortedAdsList.size-1 > currentAddPosition){
                        currentAddPosition++
                        loadnativead(
                            context,
                            view,
                            sortedAdsList[currentAddPosition].adscode,
                            sortedAdsList[currentAddPosition].ads_id,listnerInterface1
                        )
                    }


                    //loadnativead(context, view, adscode)


                }
            }


        }).build()
        adLoader.loadAd(AdRequest.Builder().build())
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
                        ipAddress =  inetAddress.hostAddress!!

                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }


        if (checkForInternet(context)) {
            val appId = Prefs.getAppId(context)


            ApiUtilities.getApiInterface()
                .postData(ipAddress, appId, adsId , currentdate)
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