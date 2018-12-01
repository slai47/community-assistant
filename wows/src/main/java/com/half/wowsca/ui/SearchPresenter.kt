package com.half.wowsca.ui

import android.content.Context
import com.half.wowsca.CAApp
import com.half.wowsca.backend.services.CaptainService
import com.half.wowsca.interfaces.SearchInterface
import com.half.wowsca.model.Captain
import com.half.wowsca.model.result.SearchResults
import com.half.wowsca.model.retrofit.ApiResponse
import com.half.wowsca.model.retrofit.SearchCaptain
import org.greenrobot.eventbus.EventBus
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.ArrayList

class SearchPresenter(val context : Context, val view : SearchInterface) {

    private lateinit var responseCall : Call<ApiResponse<List<SearchCaptain>>>

    fun search(search : String) {
        val s = CAApp.getServerType(context)
        val service = CaptainService.build(s.suffix)

        responseCall = service.getCaptains(search, s.appId)

        responseCall.enqueue(object : Callback<ApiResponse<List<SearchCaptain>>> {
            override fun onResponse(call: Call<ApiResponse<List<SearchCaptain>>>, response: Response<ApiResponse<List<SearchCaptain>>>) {
                if(!call.isCanceled) {
                    val apiResponse = response.body()
                    val results = SearchResults()
                    if (apiResponse != null && apiResponse.data != null) {
                        val data = apiResponse.data
                        val captains = ArrayList<Captain>()
                        for (sc in data) {
                            val c = sc.toCaptain()
                            c.server = CAApp.getServerType(context)
                            captains.add(c)
                        }
                        results.captains = captains
                    }
                    view.onReceive(results)
                }
            }

            override fun onFailure(call: Call<ApiResponse<List<SearchCaptain>>>, t: Throwable) {
                EventBus.getDefault().post(SearchResults())
            }
        })
    }

    fun dispose() {
        responseCall?.cancel()
    }
}