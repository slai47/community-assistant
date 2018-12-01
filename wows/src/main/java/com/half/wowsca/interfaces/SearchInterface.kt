package com.half.wowsca.interfaces

import com.half.wowsca.model.result.SearchResults

interface SearchInterface {

    fun onReceive(event : SearchResults)

}