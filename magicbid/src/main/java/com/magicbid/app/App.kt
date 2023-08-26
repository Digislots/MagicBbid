package com.magicbid.app

import android.app.Application
import android.util.Log
import android.app.Activity

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.multidex.MultiDexApplication
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
import java.util.Date


private const val LOG_TAG = "MyApplication"

/** Application class that initializes, loads and show ads when activities change states. */
class  App: Application(), Application.ActivityLifecycleCallbacks, LifecycleObserver {

    private lateinit var appOpenAdManager: AppOpenAdManager
    private var currentActivity: Activity? = null

    override fun onCreate() {
        super.onCreate()
        registerActivityLifecycleCallbacks(this)

        Log.d(LOG_TAG, "Google Mobile Ads SDK Version: " + MobileAds.getVersion())

        MobileAds.initialize(this) {}
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        appOpenAdManager = AppOpenAdManager()



        CoroutineScope(Dispatchers.IO).launch {


            try {
                CoroutineScope(Dispatchers.IO).launch {
                    val res = ApiUtilities.getApiInterface()!!.getApptomative()
                    withContext(Dispatchers.Main) {
                        try {
                            val result = res.body()?.adscode

                            SharedPrefs.setResponseAll(applicationContext, result)


                            var maxCpm = 0
                            val pubappid = res.body()?.publisherid
                            var appIid = ""

//                            if (pubappid != null) {
//                                for (adscode in pubappid) {
//                                    appIid = adscode.app_code
//
//                                    Constant.AppId = appIid
//
//
//                                    try {
//                                        val applicationInfo = packageManager.getApplicationInfo(
//                                            packageName, PackageManager.GET_META_DATA)
//                                        applicationInfo.metaData.putString(
//                                            "com.google.android.gms.ads.APPLICATION_ID",
//                                            appIid)
//                                        Log.d("dvbvb", appIid)
//
//                                    } catch (e: PackageManager.NameNotFoundException) {
//                                        e.printStackTrace()
//                                    }
//                                }
//
//
//
//                            }


                        } catch (e: Exception) {
                            Log.d("dvbvb", e.toString())

                        }
                    }
                }
            } catch (e: Exception) {
                Log.d("dvbvb", e.toString())
                //hideLoader()
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
    private inner class AppOpenAdManager {

        private var appOpenAd: AppOpenAd? = null
        private var isLoadingAd = false
        var isShowingAd = false

        /** Keep track of the time an app open ad is loaded to ensure you don't show an expired ad. */
        private var loadTime: Long = 0

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



            val result =  SharedPrefs.getResponseAll(applicationContext)
            var maxCpm = 0
            var maxCpmAdscode =""

            if (result != null) {

                for (ads in result) {
                    if (ads.ads_type == 2) {
                        if (ads.cpm > maxCpm) {
                            maxCpm = ads.cpm.toInt()
                            maxCpmAdscode = ads.adscode

                            isLoadingAd = true
                            val request = AdRequest.Builder().build()
                            AppOpenAd.load(context, maxCpmAdscode, request,
                                AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT, object : AppOpenAd.AppOpenAdLoadCallback() {

                                    override fun onAdLoaded(ad: AppOpenAd) {
                                        appOpenAd = ad
                                        isLoadingAd = false
                                        loadTime = Date().time
                                        Log.d(LOG_TAG, "onAdLoaded.")
                                        Toast.makeText(context, "onAdLoaded", Toast.LENGTH_SHORT).show()
                                    }


                                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                                        isLoadingAd = false
                                        Log.d(LOG_TAG, "onAdFailedToLoad: " + loadAdError.message)
                                        Toast.makeText(context, "onAdFailedToLoad", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            )

                        }
                    }
                }
            }





//            val request = AdRequest.Builder().build()
//            AppOpenAd.load(context, AD_UNIT_ID, request, AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT, object : AppOpenAd.AppOpenAdLoadCallback() {
//
//                    override fun onAdLoaded(ad: AppOpenAd) {
//                        appOpenAd = ad
//                        isLoadingAd = false
//                        loadTime = Date().time
//                        Log.d(LOG_TAG, "onAdLoaded.")
//                        Toast.makeText(context, "onAdLoaded", Toast.LENGTH_SHORT).show()
//                    }
//
//                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
//                        isLoadingAd = false
//                        Log.d(LOG_TAG, "onAdFailedToLoad: " + loadAdError.message)
//                        Toast.makeText(context, "onAdFailedToLoad", Toast.LENGTH_SHORT).show()
//                    }
//                }
//            )
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
            showAdIfAvailable(
                activity,
                object : OnShowAdCompleteListener {
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
        fun showAdIfAvailable(activity: Activity, onShowAdCompleteListener: OnShowAdCompleteListener) {
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
                        Toast.makeText(activity, "onAdDismissedFullScreenContent", Toast.LENGTH_SHORT).show()

                        onShowAdCompleteListener.onShowAdComplete()
                        loadAd(activity)
                    }

                    /** Called when fullscreen content failed to show. */
                    override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                        appOpenAd = null
                        isShowingAd = false
                        Log.d(LOG_TAG, "onAdFailedToShowFullScreenContent: " + adError.message)
                        Toast.makeText(activity, "onAdFailedToShowFullScreenContent", Toast.LENGTH_SHORT).show()

                        onShowAdCompleteListener.onShowAdComplete()
                        loadAd(activity)
                    }

                    /** Called when fullscreen content is shown. */
                    override fun onAdShowedFullScreenContent() {
                        Log.d(LOG_TAG, "onAdShowedFullScreenContent.")
                        Toast.makeText(activity, "onAdShowedFullScreenContent", Toast.LENGTH_SHORT).show()
                    }
                }
            )
            isShowingAd = true
            appOpenAd!!.show(activity)
        }
    }
}