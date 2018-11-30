package com.half.wowsca.backend.services

import com.half.wowsca.CAApp
import com.half.wowsca.CAApp.WOWS_API_SITE_ADDRESS
import com.half.wowsca.model.Achievement
import com.half.wowsca.model.Captain
import com.half.wowsca.model.Ship
import com.half.wowsca.model.backend.Clan
import com.half.wowsca.model.ranked.RankedInfo
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.Retrofit



interface CaptainService {

    // Next is redo the model stucture to match these calls

    @GET("account/list/?application_id={appId}&search={key}")
    fun getCaptains(@Path("key") searchKey : String, @Path("appId") appID : String) : Call<List<Captain>>

    @GET("account/info/?application_id={appId}&account_id={id}")
    fun getCaptain(@Path("id") id : Long, @Path("appId") appId : String) : Call<Captain>

    @GET("ships/stats/?application_id={appId}&account_id={id}&extra=club,pve,pvp_div2,pvp_div3,pvp_solo")
    fun getCaptainShips(@Path("id") id : Long, @Path("appId") appId : String) : Call<List<Ship>>

    @GET("account/achievements/?application_id={appId}&account_id={id}")
    fun getCaptainAchievements(@Path("id") id : Long, @Path("appId") appId : String) : Call<List<Achievement>>

    @GET("seasons/accountinfo/?application_id={appId}&account_id={id}")
    fun getCaptainRankedInfo(@Path("id") id : Long, @Path("appId") appId : String) : Call<List<RankedInfo>>

    @GET("seasons/shipstats/?application_id={appId}&account_id={id}")
    fun getCaptainRankedShipStats(@Path("id") id : Long, @Path("appId") appId : String) : Call<List<Ship>>

    @GET("clans/accountinfo/?application_id={appId}&account_id={id}")
    fun getCaptainClanInfo(@Path("id") id : Long, @Path("appId") appId : String) : Call<Clan>

    companion object {
        fun build() : CaptainService {
            val retrofit = Retrofit.Builder()
                    .baseUrl("{$WOWS_API_SITE_ADDRESS}/wows/")
                    .build()
            return retrofit.create(CaptainService::class.java!!)
        }
    }
}