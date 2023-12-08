package com.magicbid.app

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Handler
import android.os.Looper
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

class MagicBidSdk(private var context: Context) {
    private lateinit var sortedAdsList: MutableList<Adscode>
    private val result = Prefs.getResponseAll(context)
    private var currentAddPosition = 0
    private var isOpen = false
    private var rewardedInterstitialAd: RewardedInterstitialAd? = null
    private var adView: AdView? = null
    private val formatter = SimpleDateFormat("yyyy-MM-dd")
    private val date = Date()
    private val currentdate = formatter.format(date)
    private var adidinterstital: Int = 0
    private var magic: Boolean = false

    private var ipAddress = "0.0.0.0"
    private lateinit var listnerInterface: AdListnerInterface
    private var mInterstitialAd: InterstitialAd? = null

    init {
        ApiSingleton.initialize(context)
    }

    fun loadinterStitalad(listnerInterface1: AdListnerInterface) {
        listnerInterface = listnerInterface1
        if (result != null) {
            val adsList = result.filter { it.ads_type == 3 }
            sortedAdsList = adsList.sortedByDescending { it.cpm }.toMutableList()
            if (sortedAdsList.isNotEmpty()) {
                loadinterstitalad(
                    sortedAdsList[currentAddPosition].adscode,
                    listnerInterface,
                    sortedAdsList[currentAddPosition].ads_id
                )
            }else{
                listnerInterface1.onApiFailed()
            }
        }else{
            listnerInterface1.onApiFailed()
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
                    } else {
                        if (!magic) {
                            magic = true
                            currentAddPosition = 0
                            loadinterstitalad(
                                sortedAdsList[currentAddPosition].adscode,
                                listnerInterface,
                                sortedAdsList[currentAddPosition].ads_id
                            )

                        }
                    }
                }
            }

            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                mInterstitialAd = interstitialAd
                if (mInterstitialAd != null) {
                    adidinterstital = adsId
                }
                mInterstitialAd?.fullScreenContentCallback =
                    object : FullScreenContentCallback() {
                        override fun onAdClicked() {
                            listnerInterface.onAdClicked()
                        }

                        override fun onAdDismissedFullScreenContent() {
                            mInterstitialAd = null
                            listnerInterface.onAdDismissedFullScreenContent()
                        }

                        override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                            mInterstitialAd = null
                            listnerInterface.onAdFailedToShowFullScreenContent(adError)
                        }

                        override fun onAdImpression() {
                            listnerInterface.onAdImpression()
                        }

                        override fun onAdShowedFullScreenContent() {
                            listnerInterface.onAdShowedFullScreenContent()
                        }
                    }
                listnerInterface.onAdLoaded(boolean = true)
            }
        })
    }

    fun setAutoAdCacheing(): Boolean {
        return mInterstitialAd != null
    }

    fun showInterstitialAds() {
        if (mInterstitialAd != null) {
            mInterstitialAd!!.show(context as Activity)
            postData(adidinterstital)
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
