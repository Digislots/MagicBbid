package com.magicbid.app

import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError

interface OnInitializationCallback {
    fun onLoadedBannerAd(adView: AdView)
    fun onFailad(adError: LoadAdError)
}
