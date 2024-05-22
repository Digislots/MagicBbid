package com.magicbid.app

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
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
import com.google.android.gms.ads.admanager.AdManagerAdView
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback
import com.inmobi.ads.AdMetaInfo
import com.inmobi.ads.InMobiAdRequestStatus
import com.inmobi.ads.InMobiBanner
import com.inmobi.ads.InMobiInterstitial
import com.inmobi.ads.InMobiNative
import com.inmobi.ads.listeners.BannerAdEventListener
import com.inmobi.ads.listeners.InterstitialAdEventListener
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
    private var magic: Boolean = false
    var adFailedCount = 0
    private lateinit var onInitializationCallback: OnInitializationCallback
    private var adManagerAdView: AdManagerAdView? = null
    private lateinit var interstitialAd: InMobiInterstitial
    private var ipAddress = "0.0.0.0"
    private lateinit var listnerInterface: AdListnerInterface
    var mInterstitialAd: InterstitialAd? = null
    private lateinit var inMobiNative: InMobiNative
    init {
        ApiSingleton.initialize(context)
        ApiSingleton.initInMobi(context)
    }


    fun allForBannerAd(adContainer: ViewGroup, parentView: ViewGroup, onInitializationCallback1: OnInitializationCallback) {

        onInitializationCallback = onInitializationCallback1
        // 1714265823419
        val bannerAdView = InMobiBanner(context, 1717574358127L)
        // bannerAdView = InMobiBanner(context, 1714265823419)
        bannerAdView.layoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            150
        )

        adContainer.addView(bannerAdView)
        bannerAdView.setBannerSize(320, 50)
        bannerAdView.load()
        // Set up banner ad event listener
        bannerAdView.setListener(object : BannerAdEventListener() {
            override fun onAdFetchFailed(
                inMobiBanner: InMobiBanner,
                inMobiAdRequestStatus: InMobiAdRequestStatus
            ) {
                super.onAdFetchFailed(inMobiBanner, inMobiAdRequestStatus)
                Log.d("aaaaaaa", "onAdFetchFailed")
            }

            override fun onAdDisplayed(inMobiBanner: InMobiBanner) {
                super.onAdDisplayed(inMobiBanner)
                Log.d("aaaaaaa", "onAdDisplayed")
            }

            override fun onAdDismissed(inMobiBanner: InMobiBanner) {
                super.onAdDismissed(inMobiBanner)
                Log.d("aaaaaaa", "onAdDismissed")
            }

            override fun onUserLeftApplication(inMobiBanner: InMobiBanner) {
            }

            override fun onRewardsUnlocked(inMobiBanner: InMobiBanner, map: Map<Any, Any>) {
                super.onRewardsUnlocked(inMobiBanner, map)
            }

            override fun onAdFetchSuccessful(inMobiBanner: InMobiBanner, adMetaInfo: AdMetaInfo) {
            }

            @Deprecated("Deprecated in Java")
            override fun onAdLoadSucceeded(inMobiBanner: InMobiBanner) {
                super.onAdLoadSucceeded(inMobiBanner)
                Log.d("aaaaaaa", "onAdLoadSucceeded")
            }

            override fun onAdLoadSucceeded(inMobiBanner: InMobiBanner, adMetaInfo: AdMetaInfo) {
                super.onAdLoadSucceeded(inMobiBanner, adMetaInfo)
                Log.d("aaaaaaa", "onAdLoadSucceeded")
            }

            override fun onAdLoadFailed(
                inMobiBanner: InMobiBanner,
                inMobiAdRequestStatus: InMobiAdRequestStatus
            ) {
                super.onAdLoadFailed(inMobiBanner, inMobiAdRequestStatus)
                Log.e("aaaaaaa", "Ad load failed: " + inMobiAdRequestStatus.message)
                Log.d("aaaaaaa", "onAdLoadFailed")

                val layout = LinearLayout(context)
                layout.orientation = LinearLayout.VERTICAL
                val layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                val adSize = AdSize(AdSize.FULL_WIDTH, 50)
                fun addAdViewToLayout(adView: AdManagerAdView) {
                    adView.layoutParams = layoutParams
                    layout.addView(adView)
                    layout.requestLayout()
                    Log.d("aaaaaaa", "yyyyyy")
                }

                fun handleAdLoadingFailure(adError: LoadAdError) {
                    Log.d("aaaaaaa", "xxxxxx")
                }


                adaptiveBannerAD(context, adSize, object : OnInitializationCallback {
                    override fun onLoadedBannerAd(adManagerAdView: AdManagerAdView) {
                        // Add the ad view to the layout when the ad is loaded
                        addAdViewToLayout(adManagerAdView)
                    }

                    override fun onFailad(adError: LoadAdError) {
                        // Handle ad loading failure
                        handleAdLoadingFailure(adError)
                    }
                })
                parentView.addView(layout)


            }

            override fun onAdClicked(inMobiBanner: InMobiBanner, map: Map<Any, Any>) {
                super.onAdClicked(inMobiBanner, map)
            }


            override fun onAdImpression(inMobiBanner: InMobiBanner) {
                super.onAdImpression(inMobiBanner)
                Log.d("aaaaaaa", "onAdImpression")


            }
        })
    }

    fun adaptiveBannerAD(
        activity: Context,
        adSize: AdSize,
        onInitializationCallback1: OnInitializationCallback
    ) {
        if (result != null) {
            onInitializationCallback = onInitializationCallback1
            try {
                val adsList = result.filter { it.ads_type == 1 }
                sortedAdsList = adsList.sortedByDescending { it.cpm }.toMutableList()
                if (sortedAdsList.isNotEmpty()) {
                    loadadaptiveBannerAdd(
                        activity,
                        adSize,
                        sortedAdsList[currentAddPosition].adscode,
                        sortedAdsList[currentAddPosition].ads_id,
                        onInitializationCallback
                    )
                }
            } catch (e: Exception) {
                Log.d("magick bidSDK", e.toString())
            }
        }
    }

    private fun loadadaptiveBannerAdd(
        activity: Context,
        adSize: AdSize,
        adId: String,
        adsId: Int,
        onInitializationCallback1: OnInitializationCallback
    ) {

        adManagerAdView = AdManagerAdView(activity)
        adManagerAdView!!.adUnitId = adId
        adManagerAdView!!.setAdSize(adSize)

        // adManagerAdView!!.setVideoOptions(videoOptions)
        val adRequest = AdManagerAdRequest.Builder().build()
        adManagerAdView!!.loadAd(adRequest)
        //adManagerAdView!!.loadAd(AdManagerAdRequest.Builder().build())

        adManagerAdView!!.adListener = object : AdListener() {
            override fun onAdLoaded() {

                onInitializationCallback1.onLoadedBannerAd(adManagerAdView!!)



            }

            override fun onAdFailedToLoad(adError: LoadAdError) {
                if (adError.code == 3) {
                    onInitializationCallback1.onFailad(adError)
                    if (sortedAdsList.size - 1 > currentAddPosition) {
                        currentAddPosition++
                        loadadaptiveBannerAdd(
                            activity,
                            adSize,
                            sortedAdsList[currentAddPosition].adscode,
                            sortedAdsList[currentAddPosition].ads_id, onInitializationCallback1
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


    fun forAllInterstitial(listnerInterface1: AdListnerInterface) {
        this.listnerInterface = listnerInterface1

        interstitialAd =
            InMobiInterstitial(context, 1715196644321L, object : InterstitialAdEventListener() {
                override fun onAdLoadSucceeded(ad: InMobiInterstitial) {
                    Log.d("InMobiAd", "Ad load succeeded")
                    // Show the ad
                    if (ad.isReady) {
                        ad.show()
                    }
                }

                override fun onAdLoadFailed(ad: InMobiInterstitial, status: InMobiAdRequestStatus) {
                    Log.d("InMobiAd", "onAdLoadFailed")
                    showinterStitalad(object :AdListnerInterface{
                        override fun onAdClicked() {
                            TODO("Not yet implemented")
                        }

                        override fun onAdFailedToLoad(var1: LoadAdError) {
                            TODO("Not yet implemented")
                        }

                        override fun onAdImpression() {
                            TODO("Not yet implemented")
                        }

                        override fun onAdLoaded(boolean: Boolean) {
                            TODO("Not yet implemented")
                        }

                        override fun onAdDismissedFullScreenContent() {
                            TODO("Not yet implemented")
                        }

                        override fun onAdFailedToShowFullScreenContent(var1: AdError) {
                            TODO("Not yet implemented")
                        }

                        override fun onAdShowedFullScreenContent() {
                            TODO("Not yet implemented")
                        }

                    })

                }

                override fun onAdDismissed(ad: InMobiInterstitial) {

                }

                override fun onAdDisplayed(ad: InMobiInterstitial) {

                }


                override fun onUserLeftApplication(ad: InMobiInterstitial) {

                }

                override fun onAdImpression(ad: InMobiInterstitial) {
                    Log.d("InMobiAd", "Ad impression")


                }

                override fun onAdWillDisplay(ad: InMobiInterstitial) {
                    Log.d("InMobiAd", "Ad will display")
                }
            })

        // Load the interstitial ad
        interstitialAd.load()
    }


    private fun showinterStitalad(listnerInterface1: AdListnerInterface) {
        this.listnerInterface = listnerInterface1
        if (result != null) {
            val adsList = result.filter { it.ads_type == 3 }
            sortedAdsList = adsList.sortedByDescending { it.cpm }.toMutableList()

            if (sortedAdsList.isNotEmpty()) {

                loadinterstitalad(
                    sortedAdsList[currentAddPosition].adscode,
                    this.listnerInterface,
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
        InterstitialAd.load(
            context,
            adscode,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    mInterstitialAd = null
                    listnerInterface.onAdFailedToLoad(adError)
                    if (adError.code == 3) {
                        // currentAddPosition++
                        adFailedCount++
                        Log.d("adFailedCount", "Ad loading failed $adFailedCount times")
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

//                    if (mInterstitialAd != null) {
//                        mInterstitialAd?.show(context as Activity)
//
//                    }
                    adidinterstital = adsId

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

        }
    }


//
    fun showNativeAds(context: Context, view: TemplateView) {
        if (result != null) {
            val adsList = result.filter { it.ads_type == 4 }
            Log.d("adlist", adsList.toString())
            sortedAdsList = adsList.sortedByDescending { it.cpm }.toMutableList()
            Log.d("sortedAdsList", sortedAdsList.toString())

            if (sortedAdsList.isNotEmpty()) {
                loadnativead(context, view, sortedAdsList[currentAddPosition].adscode,sortedAdsList[currentAddPosition].ads_id)
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

        }.withAdListener(object : AdListener() {
            override fun onAdFailedToLoad(loadAdError: LoadAdError) {

                Log.d("loadnativead", sortedAdsList[currentAddPosition].cpm.toString())
                Log.d("loadnativead", sortedAdsList[currentAddPosition].adscode)
                if (loadAdError.code == 3) {

                    if (sortedAdsList.size-1 > currentAddPosition){
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


    fun sowAdrewarded() {
        if (result != null) {
            val adsList = result.filter { it.ads_type == 5 }
            sortedAdsList = adsList.sortedByDescending { it.cpm }.toMutableList()
            if (sortedAdsList.isNotEmpty()) {
                loadrewarded(
                    sortedAdsList[currentAddPosition].adscode,
                    sortedAdsList[currentAddPosition].ads_id
                )
            }
        }
    }

    private fun loadrewarded(adscode: String, adsId: Int) {
        RewardedInterstitialAd.load(context,
            adscode,
            AdManagerAdRequest.Builder().build(),
            object : RewardedInterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedInterstitialAd) {
                    rewardedInterstitialAd = ad
                    showRewardedAds(adsId)
                }

                override fun onAdFailedToLoad(adError: LoadAdError) {
                    rewardedInterstitialAd = null
                    if (adError.code == 3) {
                        adFailedCount++
                        Log.d("adFailedCount", "Ad loading failed $adFailedCount times")
                        //currentAddPosition++
                        if (sortedAdsList.size - 1 > currentAddPosition) {
                            currentAddPosition++
                            loadrewarded(
                                sortedAdsList[currentAddPosition].adscode,
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
            rewardedInterstitialAd!!.fullScreenContentCallback =
                object : FullScreenContentCallback() {
                    override fun onAdDismissedFullScreenContent() {

                    }
                }
        }
    }





}