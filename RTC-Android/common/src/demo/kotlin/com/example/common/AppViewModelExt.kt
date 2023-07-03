package com.example.common

import com.tinet.ticloudrtc.CreateResultCallback
import com.tinet.ticloudrtc.TiCloudRTC
import com.tinet.ticloudrtc.bean.CreateClientOption

internal fun AppViewModel.loginExt(loginIntent: AppIntent.Login) {
    // 保存登录信息
    saveLoginState(loginIntent)

    // 创建 RTC 客户端
    TiCloudRTC.createClient(
        context = loginIntent.context,
        CreateClientOption(
            rtcEndpoint = loginIntent.platformUrl,
            enterpriseId = loginIntent.enterpriseId,
            userId = loginIntent.username,
            accessToken = loginIntent.password,
        ).apply {
            isDebug = true
            callerNumber = loginIntent.callerNumber
        },
        resultCallback = object : CreateResultCallback {
            override fun onFailed(errorCode: Int, errorMessage: String) {
                _appUiState.value = AppUiState.LoginFailed(errorMessage)
            }

            override fun onSuccess(rtcClient: TiCloudRTC, fields: HashMap<String, String>) {
                this@loginExt.rtcClient = rtcClient
                rtcClient.setEventListener(CustomEventListener())
                _appUiState.value = AppUiState.LoginSuccess
            }


        }
    )
}


internal fun AppViewModel.onAccessTokenWillExpireExt(accessToken: String) {
    _appUiState.value = AppUiState.OnAccessTokenWillExpire
}