package com.magicbid.app

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Response
import java.net.Inet4Address
import java.net.NetworkInterface


import java.text.SimpleDateFormat
import java.util.Date


private const val LOG_TAG = "MyApplication"

/** Application class that initializes, loads and show ads when activities change states. */
open class App : Application(), Application.ActivityLifecycleCallbacks, LifecycleObserver {
    private var isLoadingAd: Boolean = false
    private lateinit var appOpenAdManager: AppOpenAdManager
    private var currentActivity: Activity? = null
    var currentAddPosition = 0
    private var appOpenAd: AppOpenAd? = null
    private var loadTime: Long = 0
    var ipAddress= "0.0.0.0"
    override fun onCreate() {
        super.onCreate()
        registerActivityLifecycleCallbacks(this)



        MobileAds.initialize(this) {}
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        appOpenAdManager = AppOpenAdManager()


        val ai: ApplicationInfo = applicationContext.packageManager
            .getApplicationInfo(applicationContext.packageName, PackageManager.GET_META_DATA)
        val value = ai.metaData["token"]

        if (checkForInternet(this)) {

            ApiUtilities.getApiInterface()!!.getApptomative(value)
                .enqueue(object : retrofit2.Callback<MagicbidResponse> {
                    override fun onResponse(
                        call: Call<MagicbidResponse>,
                        response: Response<MagicbidResponse>
                    ) {
                       // val result = response.body()?.adscode

                        try {
                            val  appid = response.body()?.appdetails
                            appid?.app_id?.let {
                                Prefs.setAppId(applicationContext, it)
                            } ?: run {

                            }

                            response.body()?.adscode?.let { result ->
                                Prefs.setResponseAll(applicationContext, result)
                            } ?: run {
                                // Handle the case when response.body() or adscode is null
                            }

                        }catch (e:Exception){
                            Log.d("Exception",e.toString())

                        }



                        //Prefs.setAppId(applicationContext,appid!!.app_id)


                       // Log.d("resultData", result.toString())

                      // Prefs.setResponseAll(applicationContext, result)



                    }

                    override fun onFailure(call: Call<MagicbidResponse>, t: Throwable) {
                        Log.d("resultData", t.toString())

                    }

                })
//


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

    /** LifecycleObserver method that shows the app open ad when the app moves to foreground. */
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onMoveToForeground() {
        // Show the ad (if available) when the app moves to foreground.
        currentActivity?.let { appOpenAdManager.showAdIfAvailable(it) }
    }

    /** ActivityLifecycleCallback methods. */
    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}

    override fun onActivityStarted(activity: Activity) {
        // An ad activity is started when an ad is showing, which could be AdActivity class from Google
        // SDK or another activity class implemented by a third party mediation partner. Updating the
        // currentActivity only when an ad is not showing will ensure it is not an ad activity, but the
        // one that shows the ad.
        if (!appOpenAdManager.isShowingAd) {
            currentActivity = activity
        }
    }

    override fun onActivityResumed(activity: Activity) {}

    override fun onActivityPaused(activity: Activity) {}

    override fun onActivityStopped(activity: Activity) {}

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

    override fun onActivityDestroyed(activity: Activity) {}

    /**
     * Shows an app open ad.
     *
     * @param activity the activity that shows the app open ad
     * @param onShowAdCompleteListener the listener to be notified when an app open ad is complete
     */

    fun showAdIfAvailable(activity: Activity, onShowAdCompleteListener: OnShowAdCompleteListener) {
        // We wrap the showAdIfAvailable to enforce that other classes only interact with MyApplication
        // class.
        appOpenAdManager.showAdIfAvailable(activity, onShowAdCompleteListener)
    }

    /**
     * Interface definition for a callback to be invoked when an app open ad is complete (i.e.
     * dismissed or fails to show).
     */
    interface OnShowAdCompleteListener {
        fun onShowAdComplete()
    }

    /** Inner class that loads and shows app open ads. */
    public inner class AppOpenAdManager {


        var isShowingAd = false

        /** Keep track of the time an app open ad is loaded to ensure you don't show an expired ad. */


        /**
         * Load an ad.
         *
         * @param context the context of the activity that loads the ad
         */
        fun loadAd(context: Context) {
            // Do not load ad if there is an unused ad or one is already loading.
            if (isLoadingAd || isAdAvailable()) {
                return
            }

            isLoadingAd = true


            val result = Prefs.getResponseAll(applicationContext)


            if (result != null) {

                val adsList = result.filter { it.ads_type == 2 }
                Log.d("opena_pp", adsList.toString())
                val sortedAdsList = adsList.sortedByDescending { it.cpm }
                Log.d("opena_pp", sortedAdsList.toString())


                if (sortedAdsList.isNotEmpty()) {
                    loadopenad(context, sortedAdsList[currentAddPosition].adscode, sortedAdsList,sortedAdsList[currentAddPosition].ads_id)
                }


            }


        }

        /** Check if ad was loaded more than n hours ago. */
        private fun wasLoadTimeLessThanNHoursAgo(numHours: Long): Boolean {
            val dateDifference: Long = Date().time - loadTime
            val numMilliSecondsPerHour: Long = 3600000
            return dateDifference < numMilliSecondsPerHour * numHours
        }

        /** Check if ad exists and can be shown. */
        private fun isAdAvailable(): Boolean {
            // Ad references in the app open beta will time out after four hours, but this time limit
            // may change in future beta versions. For details, see:
            // https://support.google.com/admob/answer/9341964?hl=en
            return appOpenAd != null && wasLoadTimeLessThanNHoursAgo(4)
        }

        /**
         * Show the ad if one isn't already showing.
         *
         * @param activity the activity that shows the app open ad
         */
        fun showAdIfAvailable(activity: Activity) {
            showAdIfAvailable(activity, object : OnShowAdCompleteListener {
                override fun onShowAdComplete() {
                    // Empty because the user will go back to the activity that shows the ad.
                }
            }
            )
        }

        /**
         * Show the ad if one isn't already showing.
         *
         * @param activity the activity that shows the app open ad
         * @param onShowAdCompleteListener the listener to be notified when an app open ad is complete
         */
        fun showAdIfAvailable(
            activity: Activity,
            onShowAdCompleteListener: OnShowAdCompleteListener
        ) {
            // If the app open ad is already showing, do not show the ad again.
            if (isShowingAd) {
                Log.d(LOG_TAG, "The app open ad is already showing.")
                return
            }

            // If the app open ad is not available yet, invoke the callback then load the ad.
            if (!isAdAvailable()) {
                Log.d(LOG_TAG, "The app open ad is not ready yet.")
                onShowAdCompleteListener.onShowAdComplete()
                loadAd(activity)
                return
            }

            Log.d(LOG_TAG, "Will show ad.")

            appOpenAd!!.setFullScreenContentCallback(
                object : FullScreenContentCallback() {
                    /** Called when full screen content is dismissed. */
                    override fun onAdDismissedFullScreenContent() {
                        // Set the reference to null so isAdAvailable() returns false.
                        appOpenAd = null
                        isShowingAd = false
                        Log.d(LOG_TAG, "onAdDismissedFullScreenContent.")


                        onShowAdCompleteListener.onShowAdComplete()
                        loadAd(activity)
                    }

                    /** Called when fullscreen content failed to show. */
                    override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                        appOpenAd = null
                        isShowingAd = false
                        Log.d(LOG_TAG, "onAdFailedToShowFullScreenContent: " + adError.message)

                        onShowAdCompleteListener.onShowAdComplete()
                        loadAd(activity)
                    }

                    /** Called when fullscreen content is shown. */
                    override fun onAdShowedFullScreenContent() {
                        Log.d(LOG_TAG, "onAdShowedFullScreenContent.")
                    }
                }
            )
            isShowingAd = true
            appOpenAd!!.show(activity)
        }
    }

    fun loadopenad(context: Context, adscode: String, sortedAdsList: List<Adscode>, adsId: Int) {


        isLoadingAd = true
        val request = AdRequest.Builder().build()
        AppOpenAd.load(context, adscode, request,
            AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT, object : AppOpenAd.AppOpenAdLoadCallback() {

                override fun onAdLoaded(ad: AppOpenAd) {
                    appOpenAd = ad
                    isLoadingAd = false
                    loadTime = Date().time
                    Log.d(LOG_TAG, "onAdLoaded.")
                    Log.d("opena_pp", sortedAdsList[currentAddPosition].cpm.toString())
                    Log.d("opena_pp", sortedAdsList[currentAddPosition].adscode)

                    val formatter = SimpleDateFormat("yyyy-MM-dd")
                    val date = Date()
                    val currentdate = formatter.format(date)


                    val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager

                        //Formatter.formatIpAddress(wifiManager.connectionInfo.ipAddress)






//                    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE)
//
//
//                    if (connectivityManager is ConnectivityManager) {
//                        var link: LinkProperties =  connectivityManager.getLinkProperties(connectivityManager.activeNetwork) as LinkProperties
////            Log.e("Network", link.linkAddresses.toString())
////            Log.e("Network", link.linkAddresses[1].address.hostAddress)
//                        link?.let { linkProp ->
//                            for (linkAddress in linkProp.linkAddresses) {
//                                val inetAddress = linkAddress.address
//                                if (inetAddress is Inet4Address
//                                    && !inetAddress.isLoopbackAddress()
//                                    && inetAddress.isSiteLocalAddress()
//                                ) {
//                                    Log.e("Network",inetAddress.getHostAddress())
//                                    ipAddress =  inetAddress.getHostAddress()
//                                }
//                            }
//                        }
//                    }

                    try {
                        val networkInterfaces = NetworkInterface.getNetworkInterfaces()
                        while (networkInterfaces.hasMoreElements()) {
                            val networkInterface = networkInterfaces.nextElement()
                            val inetAddresses = networkInterface.inetAddresses
                            while (inetAddresses.hasMoreElements()) {
                                val inetAddress = inetAddresses.nextElement()
                                if (!inetAddress.isLoopbackAddress && inetAddress is Inet4Address) {
                                    ipAddress =  inetAddress.hostAddress

                                }
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }



                    if (checkForInternet(context)) {
                        val app_id = Prefs.getAppId(context)
                        ApiUtilities.getApiInterface()!!
                            .postData(ipAddress, app_id , adsId, currentdate)
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
//                                withContext(Dispatchers.Main) {
//                                    try {
//                                        res.body().toString()
//                                    } catch (e: Exception) {
//                                        Log.d("dvbvb", e.toString())
//                                    }
//                                }

                    }


                }


                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    isLoadingAd = false
                    Log.d("opena_pp", loadAdError.message)
                    Log.d("opena_pp", sortedAdsList[currentAddPosition].cpm.toString())
                    Log.d("opena_pp", sortedAdsList[currentAddPosition].adscode)
                    Log.d(LOG_TAG, "onAdFailedToLoad: " + loadAdError.message)
                    if (loadAdError.code == 3) {

                       // currentAddPosition++
                        if (sortedAdsList.size-1 > currentAddPosition){
                            currentAddPosition++
                        }else{
                            currentAddPosition = 0
                        }
                       // loadopenad(context, adscode, sortedAdsList)
                        loadopenad(
                            context,
                            sortedAdsList[currentAddPosition].adscode,
                            sortedAdsList,
                            sortedAdsList[currentAddPosition].ads_id
                        )


                        // Remove the current ad from the list and continue with the next highest CPM ad.

                    } else {
                        // Ad failed to load for another reason, handle it as needed.
                    }
                }
            }
        )

    }
}