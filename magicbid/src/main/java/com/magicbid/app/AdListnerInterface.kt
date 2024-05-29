package com.magicbid.app

import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd

interface AdListnerInterface {


    fun onAdClicked()
    fun onAdFailedToLoad(var1: String)
    fun onAdImpression()
    fun onAdLoaded(boolean: Boolean)

    fun onAdDismissedFullScreenContent()

    fun onAdFailedToShowFullScreenContent(var1: String)

    fun onAdShowedFullScreenContent()

}