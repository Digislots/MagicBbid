package com.magicbid.app

import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query


interface ApiInterface {
    @GET("/api/getAds/{token}")
    fun getApptomative(@Path("token") token: Any?): Call<MagicbidResponse>

    @POST("/api/sdk-stats")
     fun postData(
        @Query("ip") ip: String?,
        @Query("app_id") app_id: Any?,
        @Query("ads_id") ads_id: Int,
        @Query("date") date: String
    ): Call<JsonObject>



}