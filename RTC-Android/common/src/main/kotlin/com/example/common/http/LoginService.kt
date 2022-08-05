package com.example.common.http

import com.example.common.bean.BaseResult
import com.example.common.bean.LoginParams
import com.example.common.bean.LoginResult
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginInterface {
    @POST("/interface/demo/login")
    fun login(@Body loginParams: LoginParams): Call<BaseResult<LoginResult>>
}

