@file:Suppress("DEPRECATION")

package com.magicbid.app
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import com.google.android.gms.ads.MobileAds
import retrofit2.Call
import retrofit2.Response

object ApiSingleton {

    fun initialize(context: Context ) {
        MobileAds.initialize(context) { initializationStatus ->
            val statusMap =
                initializationStatus.adapterStatusMap
            makeApiCall(context)
            for (adapterClass in statusMap.keys) {
                val status = statusMap[adapterClass]
                Log.d("MyApp", String.format(
                    "Adapter name: %s, Description: %s, Latency: %d",
                    adapterClass, status!!.description, status.latency))
            }

            // Start loading ads here...
        }
//        MobileAds.initialize(application) { initializationStatus ->
//            initializationStatus.adapterStatusMap.entries.forEach {
//                Log.d("status____", "${it.key}  ${it.value}")
//            }
//
//            if (initializationStatus.adapterStatusMap.isNotEmpty()) {
//                makeApiCall(application)
//                Log.e("AdsInitialization", "Mobile Ads SDK initialized successfully")
//            } else {
//                Log.e("AdsInitialization", "Mobile Ads SDK initialization failed")
//            }
//        }
    }

    private fun makeApiCall(context: Context) {
        val value: String?
        val ai: ApplicationInfo = context.packageManager
            .getApplicationInfo(context.packageName, PackageManager.GET_META_DATA)
        value = ai.metaData["token"].toString()

        if (checkForInternet(context).not()) {
            Log.d("Internet", "No internet connection")
            return
        }

        ApiUtilities.getApiInterface().getApptomative(value)
            .enqueue(object : retrofit2.Callback<MagicbidResponse> {
                override fun onResponse(
                    call: Call<MagicbidResponse>,
                    response: Response<MagicbidResponse>
                ) {
                    try {
                        val appId = response.body()?.appdetails
                        appId?.app_id?.let {
                            Prefs.setAppId(context, it)
                        }
                        response.body()?.adscode?.let { result ->
                            Prefs.setResponseAll(context, result)
                        }
                    } catch (e: Exception) {
                        Log.d("Exception", e.toString())
                    }
                }

                override fun onFailure(call: Call<MagicbidResponse>, t: Throwable) {
                    Log.d("resultData", t.toString())
                }
            })
    }

    @SuppressLint("ObsoleteSdkInt")
    private fun checkForInternet(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                    activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
        } else {
            @Suppress("DEPRECATION")
            val networkInfo = connectivityManager.activeNetworkInfo ?: return false
            @Suppress("DEPRECATION")
            networkInfo.isConnected
        }
    }
}

