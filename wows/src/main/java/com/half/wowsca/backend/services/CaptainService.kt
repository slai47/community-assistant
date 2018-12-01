package com.half.wowsca.backend.services

import com.half.wowsca.CAApp.WOWS_API_SITE_ADDRESS
import com.half.wowsca.model.Achievement
import com.half.wowsca.model.Captain
import com.half.wowsca.model.Ship
import com.half.wowsca.model.backend.Clan
import com.half.wowsca.model.ranked.RankedInfo
import com.half.wowsca.model.retrofit.ApiResponse
import com.half.wowsca.model.retrofit.SearchCaptain
import com.utilities.search.Search
import io.reactivex.Observable
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Query
import java.util.*


interface CaptainService {

    // Next is redo the model stucture to match these calls

    @GET("account/list/")
    fun getCaptains(@Query("search") searchKey : String, @Query("application_id") appID : String) : Call<ApiResponse<List<SearchCaptain>>>

//    @GET("account/info/")
//    fun getCaptain(@Query("account_id") id : Long, @Query("application_id") appId : String) : Call<ApiResponse<Map<String,Captain>>>
//
//    @GET("ships/stats/?extra=club,pve,pvp_div2,pvp_div3,pvp_solo")
//    fun getCaptainShips(@Query("account_id") id : Long, @Query("application_id") appId : String) : Call<ApiResponse<List<Ship>>>
//
//    @GET("account/achievements/")
//    fun getCaptainAchievements(@Query("account_id") id : Long, @Query("application_id") Query : String) : Call<List<Achievement>>
//
//    @GET("seasons/accountinfo/")
//    fun getCaptainRankedInfo(@Query("account_id") id : Long, @Query("application_id") appId : String) : Call<List<RankedInfo>>
//
//    @GET("seasons/shipstats/")
//    fun getCaptainRankedShipStats(@Query("account_id") id : Long, @Query("application_id") appId : String) : Call<List<Ship>>
//
//    @GET("clans/accountinfo/")
//    fun getCaptainClanInfo(@Query("account_id") id : Long, @Query("application_id") appId : String) : Call<Clan>

    companion object {

        fun build(suffix : String) : CaptainService {
            val retrofit = Retrofit.Builder()
                    .baseUrl("$WOWS_API_SITE_ADDRESS$suffix/wows/")
                    .addConverterFactory(MoshiConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build()
            return retrofit.create(CaptainService::class.java!!)
        }
    }
}