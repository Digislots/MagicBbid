package com.magicbid.app

data class MagicbidResponse(
    val adscode: List<Adscode>,
    val appdetails: Appdetails,
    val publisherid: List<Publisherid>
)
