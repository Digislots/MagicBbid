package com.magicbid.app

data class MagicbidResponse(
    val adscode: List<Adscode>? = null,
    val appdetails: Appdetails? = null,
    val publisherid: List<Publisherid>? = null
)
