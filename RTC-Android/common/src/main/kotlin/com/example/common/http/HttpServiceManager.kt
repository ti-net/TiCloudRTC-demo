package com.example.common.http

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 *  http 服务管理
 */
object HttpServiceManager {

    var url: String = ""
        set(value) {
            field = value
            initTiCloudHttpService()
        }


    /** 使用 accessToken 鉴权的 http 服务 */
    lateinit var tiCloudHttpService: LoginInterface

    private fun initTiCloudHttpService() {
        tiCloudHttpService = Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(LoginInterface::class.java)
    }

}