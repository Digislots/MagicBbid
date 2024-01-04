package com.magicbid.app

import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd

interface AdListnerInterface {

    fun onAdLoaded(it: NativeAd)
    fun onAdFailedToLoad(var1: LoadAdError)


}