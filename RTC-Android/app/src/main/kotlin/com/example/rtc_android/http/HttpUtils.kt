package com.example.rtc_android.http

import com.example.rtc_android.bean.BaseResult
import com.google.gson.reflect.TypeToken
import retrofit2.Response
import com.tinet.ticloudrtc.utils.TLogUtils
import com.example.rtc_android.utils.jsonStringToBeanWithType
import retrofit2.Call
import retrofit2.Callback

internal object HttpUtils {

    private const val LOG_TAG = "TiCloudRtc http"

    inline fun <reified T : Any?> Call<T>.executeWithLog(): Response<T> {
        this.logCallDetail()
        return this.execute()
    }

    inline fun <reified T:Any?> Call<T>.enqueueWithLog(callback: Callback<T>){
        this.logCallDetail()
        this.enqueue(callback)
    }

    private inline fun <reified T> Call<T>.logCallDetail(){
        TLogUtils.i(
            LOG_TAG, """
                请求地址:   ${this.request().url()}
                method:    ${this.request().method()}
            """.trimIndent()
        )
        println(
            """
                请求地址:   ${this.request().url()}
                method:    ${this.request().method()}
            """.trimIndent()
        )
    }

    inline fun <reified T : Any?> Response<BaseResult<T>>.parseHttpResult(): BaseResult<T> =
        synchronized(this) {
            var body: BaseResult<T>

            try {
                if (this.isSuccessful) {
                    body = this.body()!!
                    body.isSuccessful = true
                } else {
                    body = (this.errorBody()?.string() ?: "{}")
                        .jsonStringToBeanWithType(object : TypeToken<BaseResult<T>>() {})
                }
            } catch (e: Exception) {
                body = BaseResult(
                    requestUniqueId = "",
                    result = null,
                    code = -1,
                    message = "",
                    isSuccessful = false
                )
            }

            return body.also {
                TLogUtils.i(LOG_TAG, "请求结果: $body")
                println("请求结果: $body")
            }
        }
}