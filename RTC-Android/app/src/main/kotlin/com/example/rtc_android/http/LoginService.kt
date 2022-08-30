package com.example.rtc_android.http

import com.example.rtc_android.bean.BaseResult
import com.example.rtc_android.bean.LoginParams
import com.example.rtc_android.bean.LoginResult
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

internal interface LoginInterface {
    @POST("/interface/demo/login")
    fun login(@Body loginParams: LoginParams): Call<BaseResult<LoginResult>>
}

