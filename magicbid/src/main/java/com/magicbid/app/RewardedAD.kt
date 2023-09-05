package com.magicbid.app




import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.mylibrary.databinding.RewardAdBinding
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import java.util.Collections



class RewardedAD : AppCompatActivity() {
    private lateinit var binding: RewardAdBinding
    private var rewardedAd: RewardedAd? = null
    var result: List<Adscode>? = null

    //private lateinit var result : ArrayList<Adscode> //Add POJO class type
   // var maxCpmAdscode = ""
    var maxCpm = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = RewardAdBinding.inflate(layoutInflater)
        setContentView(binding.root)

        MobileAds.initialize(this) {}


        //Getting Data from SP and storing in result
          result = Prefs.getResponseAll(applicationContext)

        //Sorting Decending order 5,4,3, by cpm

        if (result != null) {
            for (ads in result!!) {
                try {
                    if (ads.ads_type == 5) {

//                        Collections.sort(result, Comparator<Adscode> { obj1, obj2 ->
//                            return@Comparator obj2.cpm.compareTo(obj1.cpm)
//                        })

                        //get adscode by position
                        loadAd(result!!.get(maxCpm).adscode)
                    }
                } catch (e: Exception) {
                    Log.d("dvbvb", e.toString())
                }
            }
        }
    }


    //load add
    private fun loadAd(maxCpmAdscode : String){

        var adRequest = AdRequest.Builder().build()
        RewardedAd.load(this@RewardedAD,
            maxCpmAdscode,
            adRequest,
            object : RewardedAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    rewardedAd = null
                    if (adError.message == "No fill."){

                        //IF NO add increase maxCpm by 1 or ++1
                        maxCpm++

                        //calling same function with updated adscode
                        result?.get(maxCpm)?.let { loadAd(it.adscode) }
                    }
                }

                override fun onAdLoaded(ad: RewardedAd) {
                    rewardedAd = ad
                }
            })

    }
}
//
//class RewardedAD : AppCompatActivity() {
//    private lateinit var binding: RewardAdBinding
//    private var rewardedAd: RewardedAd? = null
////    private lateinit var result : ArrayList<Adscode> //Add POJO class type
////    var maxCpmAdscode = ""
////    var maxCpm = 0
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = RewardAdBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        MobileAds.initialize(this) {}
//
//        //Getting Data from SP and storing in result
//       // result = Prefs.getResponseAll(applicationContext) as ArrayList<Adscode>
//
//        //Sorting Decending order 5,4,3, by cpm
//
//
//        //get adscode by position
//
//
//
//
//       // loadAd(result.get(maxCpm).adscode)
//        getInformation()
//
//    }
//
//    private fun getInformation() {
//
//        val result = Prefs.getResponseAll(applicationContext)
//        var maxCpm = 0
//        var maxCpmAdscode = ""
//
//        if (result != null) {
//
//            for (ads in result) {
//                try {
//
//                    if (ads.ads_type == 5) {
//                        if (ads.cpm > maxCpm) {
//                            maxCpm = ads.cpm.toInt()
//                            maxCpmAdscode = ads.adscode
//
//                            Collections.sort(result, Comparator<Any?> { obj1, obj2 ->
//                                return@Comparator (obj2.cpm).compareTo(Integer.valueOf(obj1.cpm));
//                            })
//
//
//                            loadAd(result.get(maxCpm).adscode)
//
//                        }
//                    }
//                } catch (e: Exception) {
//                    Log.d("dvbvb", e.toString())
//                }
//
//
//            }
//
//
//        }
//
//
//    }
//    private fun loadAd(maxCpmAdscode: String){
//
//        var adRequest = AdRequest.Builder().build()
//        RewardedAd.load(this@RewardedAD,
//            maxCpmAdscode.toString(),
//            adRequest,
//            object : RewardedAdLoadCallback() {
//                override fun onAdFailedToLoad(adError: LoadAdError) {
//                    rewardedAd = null
//                    if (adError.message == "No fill."){
//
//                        //IF NO add increase maxCpm by 1 or ++1
//                        maxCpm++
//
//                        //calling same function with updated adscode
//
//                    }
//                }
//
//                override fun onAdLoaded(ad: RewardedAd) {
//                    rewardedAd = ad
//                }
//            })
//
//    }
//
//}