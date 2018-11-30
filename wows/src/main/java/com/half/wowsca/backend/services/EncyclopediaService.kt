package com.half.wowsca.backend.services

import com.half.wowsca.CAApp
import com.half.wowsca.model.encyclopedia.holders.CaptainSkillHolder
import com.half.wowsca.model.encyclopedia.items.AchievementInfo
import com.half.wowsca.model.encyclopedia.items.EquipmentInfo
import com.half.wowsca.model.encyclopedia.items.ShipInfo
import com.squareup.moshi.Moshi
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Path

interface EncyclopediaService {

    // Still need to change the model classes to accept the calls

    @GET("encyclopedia/ships/?application_id={appId}&page_no={page}&language={language}")
    fun getShips(@Path("appId") appId : String, @Path("page") page : String, @Path("language") language : String) : Call<List<ShipInfo>>

    @GET("encyclopedia/achievements/?application_id={appId}&language={language}")
    fun getAchievements(@Path("appId") appId : String, @Path("language") language : String) : Call<List<AchievementInfo>>

    @GET("encyclopedia/consumables/?application_id={appId}&language={language}&type=Modernization")
    fun getConsumables(@Path("appId") appId : String, @Path("language") language : String) : Call<List<EquipmentInfo>>

    @GET("encyclopedia/crewskills/?application_id={appId}&language={language}")
    fun getCrewSkills(@Path("appId") appId : String, @Path("language") language : String) : Call<List<CaptainSkillHolder>>

    @GET("encyclopedia/consumables/?application_id={appId}&language={language}&type=Flags")
    fun getFlags(@Path("appId") appId : String, @Path("language") language : String) : Call<List<EquipmentInfo>>

    companion object {
        fun build() : EncyclopediaService {
            val retrofit = Retrofit.Builder()
                    .baseUrl("{${CAApp.WOWS_API_SITE_ADDRESS}}/wows/")
                    .build()
            return retrofit.create(EncyclopediaService::class.java!!)
        }
    }
}