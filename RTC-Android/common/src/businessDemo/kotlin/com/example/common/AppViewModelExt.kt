package com.example.common

import android.util.Log
import com.example.common.bean.BaseResult
import com.example.common.bean.LoginParams
import com.example.common.bean.LoginResult
import com.example.common.http.HttpServiceManager
import com.example.common.http.HttpUtils.enqueueWithLog
import com.example.common.http.HttpUtils.parseHttpResult
import com.tinet.ticloudrtc.CreateResultCallback
import com.tinet.ticloudrtc.TiCloudRTC
import com.tinet.ticloudrtc.bean.CreateClientOption
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap
import kotlin.math.log

internal fun loginExt(
    loginIntent: AppIntent.Login,
    loginParams: LoginParams,
    callerNumber: String,
    onUiStateUpdate: (uiState: AppUiState) -> Unit,
    onSaveLoginMessage: (loginIntent: AppIntent.Login) -> Unit,
    onRtcClientCreated: (rtcClient: TiCloudRTC) -> Unit
) {
    HttpServiceManager.tiCloudHttpService.login(loginParams).enqueueWithLog(object : Callback<BaseResult<LoginResult>> {
        override fun onResponse(
            call: Call<BaseResult<LoginResult>>,
            response: Response<BaseResult<LoginResult>>
        ) {
            Log.i(AppViewModel.LOG_TAG, "登录接口请求成功")
            Log.i(AppViewModel.LOG_TAG, "返回结果： ${response.body()}")
            val loginResult = response.parseHttpResult()
            if (loginResult.code == 200) {
                Log.i(AppViewModel.LOG_TAG, "获取初始化参数成功")

                var rtcEndpoint = ""
                var enterpriseId = 0
                var accessToken = ""

                onSaveLoginMessage(loginIntent)

                loginResult.result?.also {
                    rtcEndpoint = it.rtcEndpoint
                    enterpriseId = it.enterpriseId
                    accessToken = it.accessToken
                }

                // 创建 RTC 客户端
                TiCloudRTC.createClient(
                    context = loginIntent.context,
                    CreateClientOption(
                        rtcEndpoint = rtcEndpoint,
                        enterpriseId = enterpriseId.toString(),
                        userId = loginIntent.usernameOrUserId,
                        accessToken = accessToken,
                    ).also {
                        it.isDebug = true
                        it.callerNumber = callerNumber
                    },
                    resultCallback = object : CreateResultCallback {
                        override fun onFailed(errorCode: Int, errorMessage: String) {
                            onUiStateUpdate(AppUiState.LoginFailed(errorMessage))
                        }

                        override fun onSuccess(
                            rtcClient: TiCloudRTC,
                            fields: HashMap<String, String>
                        ) {
                            onRtcClientCreated(rtcClient)
                            onUiStateUpdate(AppUiState.LoginSuccess)
                        }
                    }
                )
            } else {
                Log.i(AppViewModel.LOG_TAG, "返回结果无效")
                onUiStateUpdate(AppUiState.LoginFailed(loginResult.message))
            }
        }

        override fun onFailure(call: Call<BaseResult<LoginResult>>, t: Throwable) {
            Log.i(AppViewModel.LOG_TAG, "登录接口请求失败")
            onUiStateUpdate(AppUiState.LoginFailed(t.message ?: "接口访问错误"))
        }

    })
}


internal fun onAccessTokenWillExpireExt(
    rtcClient: TiCloudRTC,
    accessToken: String,
    loginParams: LoginParams,
    onUiStateUpdate: (uiState: AppUiState) -> Unit
) {
    Log.i(
        "AccessToken",
        "onAccessTokenWillExpire ---- ${
            SimpleDateFormat(
                "yyyy-MM-dd hh:mm:ss",
                Locale.getDefault()
            ).format(Date())
        }"
    )
    HttpServiceManager.tiCloudHttpService.login(loginParams).enqueueWithLog(object : Callback<BaseResult<LoginResult>> {
        override fun onResponse(
            call: Call<BaseResult<LoginResult>>,
            response: Response<BaseResult<LoginResult>>
        ) {
            val loginResult = response.parseHttpResult()
            if (loginResult.isSuccessful) {
                // 更新 access token
                rtcClient.renewAccessToken(loginResult.result!!.accessToken)
            } else {
                onUiStateUpdate(AppUiState.OnRefreshTokenFailed("获取新的 access token 失败:${loginResult.message}"))
            }
        }

        override fun onFailure(call: Call<BaseResult<LoginResult>>, t: Throwable) {
            onUiStateUpdate(AppUiState.OnRefreshTokenFailed("获取新的 access token 失败:${t.message}"))
        }

    })
}
