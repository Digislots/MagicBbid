package com.magicbid.app

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.net.wifi.WifiManager
import android.os.Bundle
import android.text.format.Formatter
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date


private const val LOG_TAG = "MyApplication"

/** Application class that initializes, loads and show ads when activities change states. */
class App : Application(), Application.ActivityLifecycleCallbacks, LifecycleObserver {
    private var isLoadingAd: Boolean = false
    private lateinit var appOpenAdManager: AppOpenAdManager
    private var currentActivity: Activity? = null
    var currentAddPosition = 0
    private var appOpenAd: AppOpenAd? = null
    private var loadTime: Long = 0

    override fun onCreate() {
        super.onCreate()
        registerActivityLifecycleCallbacks(this)



        MobileAds.initialize(this) {}
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        appOpenAdManager = AppOpenAdManager()


        val ai: ApplicationInfo = applicationContext.packageManager
            .getApplicationInfo(applicationContext.packageName, PackageManager.GET_META_DATA)
        val value = ai.metaData["token"]

        CoroutineScope(Dispatchers.IO).launch {


            try {
                CoroutineScope(Dispatchers.IO).launch {
                    val res = ApiUtilities.getApiInterface()!!.getApptomative(value)
                    withContext(Dispatchers.Main) {
                        try {
                            val result = res.body()?.adscode
                            Log.d("resultData", result.toString())
                            Prefs.setResponseAll(applicationContext, result)


                        } catch (e: Exception) {


                        }
                    }
                }
            } catch (e: Exception) {


            }


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
                    loadopenad(context, sortedAdsList[currentAddPosition].adscode, sortedAdsList)
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

    fun loadopenad(context: Context, adscode: String, sortedAdsList: List<Adscode>) {


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
                    val ipAddress: String =
                        Formatter.formatIpAddress(wifiManager.connectionInfo.ipAddress)

                    val ai: ApplicationInfo = context.packageManager
                        .getApplicationInfo(context.packageName, PackageManager.GET_META_DATA)
                    val app_id = ai.metaData["com.google.android.gms.ads.APPLICATION_ID"]

                    try {
                        CoroutineScope(Dispatchers.IO).launch {
                            val res = ApiUtilities.getApiInterface()!!
                                .postData(ipAddress, app_id, adscode, currentdate)
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


                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    isLoadingAd = false
                    Log.d("opena_pp", loadAdError.message)
                    Log.d("opena_pp", sortedAdsList[currentAddPosition].cpm.toString())
                    Log.d("opena_pp", sortedAdsList[currentAddPosition].adscode)
                    Log.d(LOG_TAG, "onAdFailedToLoad: " + loadAdError.message)
                    if (loadAdError.code == 3) {

                        currentAddPosition++
                        loadopenad(context, adscode, sortedAdsList)


                        // Remove the current ad from the list and continue with the next highest CPM ad.

                    } else {
                        // Ad failed to load for another reason, handle it as needed.
                    }
                }
            }
        )

    }
}