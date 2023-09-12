package com.magicbid.app

import retrofit2.Response
import retrofit2.http.GET

interface ApiInterface {


    @GET("/api/getAds/TTS1234567")
    suspend fun getApptomative(

    ): Response<MagicbidResponse>

}