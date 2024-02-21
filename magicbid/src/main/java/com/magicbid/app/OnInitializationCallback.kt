package com.magicbid.app

import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.admanager.AdManagerAdView

interface OnInitializationCallback {
    fun onLoadedBannerAd(adManagerAdView: AdManagerAdView)
    fun onFailad(adError: LoadAdError)
}
