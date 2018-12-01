package com.half.wowsca.backend.services

import com.half.wowsca.CAApp
import com.half.wowsca.model.encyclopedia.holders.CaptainSkillHolder
import com.half.wowsca.model.encyclopedia.items.AchievementInfo
import com.half.wowsca.model.encyclopedia.items.EquipmentInfo
import com.half.wowsca.model.encyclopedia.items.ShipInfo
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface EncyclopediaService {

    // Still need to change the model classes to accept the calls

    @GET("encyclopedia/ships/")
    fun getShips(@Query("application_id") appId : String, @Query("application_id") page : String, @Query("language") language : String) : Call<List<ShipInfo>>

    @GET("encyclopedia/achievements/")
    fun getAchievements(@Query("application_id") appId : String, @Query("language") language : String) : Call<List<AchievementInfo>>

    @GET("encyclopedia/consumables/?type=Modernization")
    fun getConsumables(@Query("application_id") appId : String, @Query("language") language : String) : Call<List<EquipmentInfo>>

    @GET("encyclopedia/crewskills/")
    fun getCrewSkills(@Query("application_id") appId : String, @Query("language") language : String) : Call<List<CaptainSkillHolder>>

    @GET("encyclopedia/consumables/?type=Flags")
    fun getFlags(@Query("application_id") appId : String, @Query("language") language : String) : Call<List<EquipmentInfo>>

    companion object {
        fun build() : EncyclopediaService {
            val retrofit = Retrofit.Builder()
                    .baseUrl("{${CAApp.WOWS_API_SITE_ADDRESS}}/wows/")
                    .addConverterFactory(MoshiConverterFactory.create())
                    .build()
            return retrofit.create(EncyclopediaService::class.java!!)
        }
    }
}