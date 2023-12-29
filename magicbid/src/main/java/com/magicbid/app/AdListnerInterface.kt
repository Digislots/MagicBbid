package com.magicbid.app

import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.LoadAdError

interface AdListnerInterface {

    fun onAdClicked(){}
    fun onAdFailedToLoad(var1: LoadAdError){}
    fun onAdImpression(){}
    fun onAdLoaded(boolean: Boolean){}

    fun onAdDismissedFullScreenContent(){}

    fun onAdFailedToShowFullScreenContent(var1: AdError){}

    fun onAdShowedFullScreenContent(){}

}