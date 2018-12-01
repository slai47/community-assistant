package com.half.wowsca.model.retrofit

import com.squareup.moshi.JsonClass

data class ApiResponse<T>(val status : String, val meta : Meta, val data : T)

data class Meta(val count : Integer, val page_total : Integer, val total : Integer, val limit : Integer, val page : Integer)
