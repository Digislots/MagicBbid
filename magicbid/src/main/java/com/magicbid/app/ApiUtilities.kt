package com.magicbid.app

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

object ApiUtilities {

    fun getInstance(): Retrofit? {

        return try {


            val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
                @Throws(CertificateException::class)
                override fun checkClientTrusted(
                    chain: Array<X509Certificate>, authType: String
                ) {
                }

                @Throws(CertificateException::class)
                override fun checkServerTrusted(
                    chain: Array<X509Certificate>, authType: String
                ) {
                }

                override fun getAcceptedIssuers(): Array<X509Certificate> {
                    return arrayOf()
                }
            })
            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustAllCerts, SecureRandom())

            // Create an ssl socket factory with our all-trusting manager

            // Create an ssl socket factory with our all-trusting manager
            val sslSocketFactory: SSLSocketFactory = sslContext.socketFactory

            val httpClient = OkHttpClient.Builder()




            httpClient.sslSocketFactory(sslSocketFactory, trustAllCerts.get(0) as X509TrustManager)
            httpClient.hostnameVerifier(HostnameVerifier { hostname, session -> true })

            httpClient.addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })

            Retrofit.Builder().baseUrl("https://admin.digislots.in")
                .addConverterFactory(GsonConverterFactory.create()).client(httpClient.build())
                .build()


        } catch (e: Exception) {
            null
        }

    }

    fun getApiInterface(): ApiInterface {
        return getInstance()!!.create(ApiInterface::class.java)
    }

}