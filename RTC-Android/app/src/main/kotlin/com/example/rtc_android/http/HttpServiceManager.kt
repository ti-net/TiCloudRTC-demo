package com.example.rtc_android.http

import com.tinet.ticloudrtc.BuildConfig
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 *  http 服务管理
 */
internal object HttpServiceManager {
    /** 使用 accessToken 鉴权的 http 服务 */
    val tiCloudHttpService by lazy{
        Retrofit.Builder()
            .baseUrl(BuildConfig.DEFAULT_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(LoginInterface::class.java)
    }

}