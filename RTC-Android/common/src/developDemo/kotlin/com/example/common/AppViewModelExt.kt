package com.example.common

import com.example.common.bean.LoginParams
import com.tinet.ticloudrtc.CreateResultCallback
import com.tinet.ticloudrtc.TiCloudRTC
import com.tinet.ticloudrtc.bean.CreateClientOption
import kotlinx.coroutines.flow.MutableStateFlow

internal fun loginExt(
    loginIntent: AppIntent.Login,
    loginParams: LoginParams,
    callerNumber: String,
    onUiStateUpdate: (uiState: AppUiState) -> Unit,
    onSaveLoginMessage: (loginIntent: AppIntent.Login) -> Unit,
    onRtcClientCreated: (rtcClient: TiCloudRTC) -> Unit
) {
    // 保存登录信息
    onSaveLoginMessage(loginIntent)

    // 创建 RTC 客户端
    TiCloudRTC.createClient(
        context = loginIntent.context,
        CreateClientOption(
            rtcEndpoint = loginIntent.platformUrl,
            enterpriseId = loginIntent.enterpriseId,
            userId = loginIntent.usernameOrUserId,
            accessToken = loginIntent.passwordOrAccessToken,
        ).also {
            it.isDebug = true
            it.callerNumber = loginIntent.callerNumber
        },
        resultCallback = object : CreateResultCallback {
            override fun onFailed(errorCode: Int, errorMessage: String) {
                onUiStateUpdate(AppUiState.LoginFailed(errorMessage))
            }

            override fun onSuccess(rtcClient: TiCloudRTC, fields: HashMap<String, String>) {
                onRtcClientCreated(rtcClient)
                onUiStateUpdate(AppUiState.LoginSuccess)
            }


        }
    )
}


internal fun onAccessTokenWillExpireExt(
    rtcClient: TiCloudRTC,
    accessToken: String,
    loginParams: LoginParams,
    onUiStateUpdate: (uiState: AppUiState) -> Unit
) {
    onUiStateUpdate(AppUiState.OnAccessTokenWillExpire)
}