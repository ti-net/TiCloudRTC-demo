import android.util.Log
import com.example.common.AppIntent
import com.example.common.AppUiState
import com.example.common.AppViewModel
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

internal fun AppViewModel.loginExt(loginIntent: AppIntent.Login) {
    HttpServiceManager.tiCloudHttpService.login(
        LoginParams(
            enterpriseId = enterpriseId.toInt(),
            username = username,
            password = password
        )
    ).enqueueWithLog(object : Callback<BaseResult<LoginResult>> {
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

                saveLoginState(loginIntent)

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
                        userId = loginIntent.username,
                        accessToken = accessToken,
                    ).apply {
                        isDebug = true
                        callerNumber = this@loginExt.callerNumber
                    },
                    resultCallback = object : CreateResultCallback {
                        override fun onFailed(errorCode: Int, errorMessage: String) {
                            _appUiState.value = AppUiState.LoginFailed(errorMessage)
                        }

                        override fun onSuccess(
                            rtcClient: TiCloudRTC,
                            fields: HashMap<String, String>
                        ) {
                            this@loginExt.rtcClient = rtcClient
                            rtcClient.setEventListener(CustomEventListener())
                            _appUiState.value = AppUiState.LoginSuccess
                        }
                    }
                )
            } else {
                Log.i(AppViewModel.LOG_TAG, "返回结果无效")
                _appUiState.value = AppUiState.LoginFailed(loginResult.message)
            }
        }

        override fun onFailure(call: Call<BaseResult<LoginResult>>, t: Throwable) {
            Log.i(AppViewModel.LOG_TAG, "登录接口请求失败")
            _appUiState.value = AppUiState.LoginFailed(t.message ?: "接口访问错误")
        }

    })
}


internal fun AppViewModel.onAccessTokenWillExpireExt(accessToken: String) {
    Log.i(
        "AccessToken",
        "onAccessTokenWillExpire ---- ${
            SimpleDateFormat(
                "yyyy-MM-dd hh:mm:ss",
                Locale.getDefault()
            ).format(Date())
        }"
    )
    HttpServiceManager.tiCloudHttpService.login(
        LoginParams(
            enterpriseId = enterpriseId.toInt(),
            username = username,
            password = password
        )
    ).enqueueWithLog(object : Callback<BaseResult<LoginResult>> {
        override fun onResponse(
            call: Call<BaseResult<LoginResult>>,
            response: Response<BaseResult<LoginResult>>
        ) {
            val loginResult = response.parseHttpResult()
            if (loginResult.isSuccessful) {
                // 更新 access token
                rtcClient?.renewAccessToken(loginResult.result!!.accessToken)
            } else {
                _appUiState.value =
                    AppUiState.OnRefreshTokenFailed("获取新的 access token 失败:${loginResult.message}")
            }
        }

        override fun onFailure(call: Call<BaseResult<LoginResult>>, t: Throwable) {
            _appUiState.value =
                AppUiState.OnRefreshTokenFailed("获取新的 access token 失败:${t.message}")
        }

    })
}
