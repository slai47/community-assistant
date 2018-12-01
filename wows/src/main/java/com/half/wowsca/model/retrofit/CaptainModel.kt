package com.half.wowsca.model.retrofit

import com.half.wowsca.model.Captain

data class SearchCaptain(val account_id : Long, val nickname : String) {

    fun toCaptain() : Captain {
        val captain = Captain()
        captain.name = nickname
        captain.id = account_id
        return captain
    }
}
