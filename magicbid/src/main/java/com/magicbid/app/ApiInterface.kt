package com.magicbid.app

import retrofit2.Response
import retrofit2.http.GET

interface ApiInterface {


    @GET("/api/getAds/TTS123456")
    suspend fun getApptomative(

    ): Response<MagicbidResponse>

}