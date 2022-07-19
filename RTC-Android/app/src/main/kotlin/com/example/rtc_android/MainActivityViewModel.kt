package com.example.rtc_android

import android.content.Context
import android.util.Log
import android.webkit.HttpAuthHandler
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rtc_android.bean.BaseResult
import com.example.rtc_android.bean.LoginParams
import com.example.rtc_android.bean.LoginResult
import com.example.rtc_android.http.HttpServiceManager
import com.example.rtc_android.http.HttpUtils.enqueueWithLog
import com.example.rtc_android.http.HttpUtils.parseHttpResult
import com.tinet.ticloudrtc.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

internal class MainActivityViewModel : ViewModel() {

    val intentChannel: Channel<AppIntent> = Channel(Channel.UNLIMITED)

    private val _appUiState: MutableStateFlow<AppUiState> = MutableStateFlow(AppUiState.WaitToLogin)
    val appUiState = _appUiState.asStateFlow()

    private val _muteState: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val muteState = _muteState.asStateFlow()

    private val _speakerphoneOpenSate: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val speakerphoneOpenState: StateFlow<Boolean> = _speakerphoneOpenSate

    private val CALLING_TIP = "外呼中..."
    private val _smallText: MutableStateFlow<String> = MutableStateFlow(CALLING_TIP)
    val smallText = _smallText.asStateFlow()

    private val DEFAULT_DTMF_PANEL_STATE = false
    private val _isShowDtmfPanel : MutableStateFlow<Boolean> = MutableStateFlow(DEFAULT_DTMF_PANEL_STATE)
    val isShowDtmfPanel = _isShowDtmfPanel.asStateFlow()

    private var callingTimer: Timer? = null

    private var rtcClient: TiCloudRTC? = null

    private var currentTel = ""

    private var enterpriseId:Int = 0
    private var username:String = ""
    private var password:String = ""

    init {
        viewModelScope.launch {
            intentChannel.consumeAsFlow().catch {
                Log.e(LOG_TAG, it.message ?: "")
            }.collect {
                when (it) {
                    is AppIntent.Login -> login(it)
                    is AppIntent.Logout -> logout()
                    is AppIntent.Call -> call(it)
                    is AppIntent.Hangup -> hangup()
                    is AppIntent.Mute -> mute(it)
                    is AppIntent.UseSpeakerphone -> useSpeakerphone(it)
                    is AppIntent.SendDtmf -> sendDtmf(it)
                }
            }
        }
    }

    fun isRtcClientInit() = rtcClient != null

    fun currentTel() = currentTel

    fun isMute() = muteState.value

    private fun resetCallingState() {
        _muteState.value = false
        _speakerphoneOpenSate.value = false
        callingTimer?.cancel()
        _smallText.value = CALLING_TIP
        _isShowDtmfPanel.value = DEFAULT_DTMF_PANEL_STATE
    }

    fun switchDtmfShowState(){
        _isShowDtmfPanel.value = _isShowDtmfPanel.value.not()
    }

    fun isUseSpeakerphone(): Boolean = rtcClient?.isSpeakerphoneEnabled() ?: false

    private fun sendDtmf(intent:AppIntent.SendDtmf){
        rtcClient?.dtmf(intent.digits)
    }

    private fun useSpeakerphone(intent: AppIntent.UseSpeakerphone) {
        rtcClient?.setEnableSpeakerphone(intent.isUse)
        _speakerphoneOpenSate.value = intent.isUse
    }

    private fun mute(muteIntent: AppIntent.Mute) {
        rtcClient?.setEnableLocalAudio(muteIntent.isMute.not())
        _muteState.value = muteIntent.isMute
    }

    private fun hangup() {
        rtcClient?.hangup()
    }

    private fun call(callIntent: AppIntent.Call) {
        if (callIntent.tel.isEmpty()) {
            _appUiState.value = AppUiState.CallFailed("号码不正确")
            return
        }
        currentTel = callIntent.tel
        rtcClient?.call(
            tel = callIntent.tel,
            clid = "",
            requestUniqueId = "",
            userField = "",
            6 // 1: 客服场景，6：外呼场景
        )
    }


    /**
     * 注：当前的登录只是虚假的登录，仅供 demo 获取初始化参数，
     *      正式使用时，请使用 /interface/v10/rtc/getAccessToken 获取初始化参数
     */
    private fun login(loginIntent: AppIntent.Login) {

        if (
            loginIntent.platformUrl.isEmpty() ||
            loginIntent.enterpriseId.isEmpty() ||
            loginIntent.username.isEmpty() ||
            loginIntent.password.isEmpty()
        ) {
            _appUiState.value = AppUiState.LoginFailed("参数不正确")
            return
        }

        enterpriseId = loginIntent.enterpriseId.toInt()
        username = loginIntent.username
        password = loginIntent.password

        HttpServiceManager.tiCloudHttpService.login(
            LoginParams(
                enterpriseId = enterpriseId,
                username = username,
                password = password
            )
        ).enqueue(object :Callback<BaseResult<LoginResult>>{
            override fun onResponse(
                call: Call<BaseResult<LoginResult>>,
                response: Response<BaseResult<LoginResult>>
            ) {
                Log.i(LOG_TAG, "登录接口请求成功")
                Log.i(LOG_TAG, "返回结果： ${response.body()}")
                val loginResult = response.parseHttpResult()
                if (loginResult.code == 200) {
                    Log.i(LOG_TAG, "获取初始化参数成功")

                    var rtcEndpoint = ""
                    var enterpriseId = 0
                    var accessToken = ""

                    loginResult.result?.also {
                        rtcEndpoint = it.rtcEndpoint
                        enterpriseId = it.enterpriseId
                        accessToken = it.accessToken
                    }

                    // 创建 RTC 客户端
                    TiCloudRTC.createClient(
                        context = loginIntent.context,
                        rtcEndpoint = rtcEndpoint,
                        enterpriseId = enterpriseId.toString(),
                        userId = loginIntent.username,
                        accessToken = accessToken,
                        resultCallback = object : CreateResultCallback {
                            override fun onFailed(errorCode: Int, errorMessage: String) {
                                _appUiState.value = AppUiState.LoginFailed(errorMessage)
                            }

                            override fun onSuccess(rtcClient: TiCloudRTC) {
                                this@MainActivityViewModel.rtcClient = rtcClient
                                _appUiState.value = AppUiState.LoginSuccess
                            }
                        },
                        eventListener = CustomEventListener()
                    )
                } else {
                    Log.i(LOG_TAG, "返回结果无效")
                    _appUiState.value = AppUiState.LoginFailed(loginResult.message)
                }
            }

            override fun onFailure(call: Call<BaseResult<LoginResult>>, t: Throwable) {
                Log.i(LOG_TAG, "登录接口请求失败")
                _appUiState.value = AppUiState.LoginFailed(t.message ?: "接口访问错误")
            }

        })

    }

    private fun logout() {
        rtcClient?.destroyClient(object : DestroyResultCallback {
            override fun onFailed(errorCode: Int, errorMessage: String) {
                _appUiState.value = AppUiState.LogoutFailed(errorMessage)
            }

            override fun onSuccess() {
                rtcClient = null
                _appUiState.value = AppUiState.LogoutSuccess
            }

        })
    }

    private inner class CustomEventListener : TiCloudRTCEventListener() {

        override fun onCallingStart(requestUniqueId: String) {
            _appUiState.value = AppUiState.OnCallStart
        }

        override fun onRinging() {
            _appUiState.value = AppUiState.OnRinging
            _smallText.value = "播放铃声中..."
        }

        override fun onCallCancelled() {
            _appUiState.value = AppUiState.OnCallCanceled
            resetCallingState()
        }

        override fun onCallRefused() {
            _appUiState.value = AppUiState.OnCallRefused
            resetCallingState()
        }

        override fun onCalling() {
            _appUiState.value = AppUiState.OnCalling
            callingTimer = Timer().apply {
                schedule(object : TimerTask() {
                    val startTime = Date().time
                    val sdp = SimpleDateFormat("mm:ss", Locale.getDefault())

                    override fun run() {
                        _smallText.value = sdp.format(Date(Date().time - startTime))
                    }

                }, 0, 1000)
            }
        }

        override fun onCallingEnd(isPeerHangup: Boolean) {
            _appUiState.value = AppUiState.OnCallingEnd(isPeerHangup)
            resetCallingState()
        }


        override fun onCallFailure(errorCode: Int, failureMessage: String) {
            _appUiState.value = AppUiState.OnCallFailure(failureMessage)
            resetCallingState()
        }

        override fun onAccessTokenWillExpire(accessToken: String) {
            Log.i("AccessToken","onAccessTokenWillExpire ---- ${SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(Date())}")
            HttpServiceManager.tiCloudHttpService.login(
                LoginParams(
                    enterpriseId = enterpriseId,
                    username = username,
                    password = password
                )
            ).enqueueWithLog(object:Callback<BaseResult<LoginResult>>{
                override fun onResponse(
                    call: Call<BaseResult<LoginResult>>,
                    response: Response<BaseResult<LoginResult>>
                ) {
                    val loginResult = response.parseHttpResult()
                    if(loginResult.isSuccessful){
                        // 更新 access token
                        rtcClient?.renewAccessToken(loginResult.result!!.accessToken)
                    }else{
                        _appUiState.value = AppUiState.OnRefreshTokenFailed("获取新的 access token 失败:${loginResult.message}")
                    }
                }

                override fun onFailure(call: Call<BaseResult<LoginResult>>, t: Throwable) {
                    _appUiState.value = AppUiState.OnRefreshTokenFailed("获取新的 access token 失败:${t.message}")
                }

            })
        }

        override fun onAccessTokenHasExpired() {
            Log.i("AccessToken","onAccessTokenHasExpired ---- ${SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(Date())}")
            resetCallingState()
            rtcClient = null
            _appUiState.value = AppUiState.OnAccessTokenHasExpired
        }

        override fun onError(errorCode: Int, errorMessage: String) {
            _appUiState.value = AppUiState.OnInnerSdkError(errorCode, errorMessage)
        }
    }

    companion object {
        private var LOG_TAG = MainActivityViewModel::class.java.simpleName
    }
}

sealed interface AppIntent {
    data class Login(
        val context: Context,
        val platformUrl: String,
        val enterpriseId: String,
        val username: String,
        val password: String
    ) : AppIntent

    object Logout : AppIntent

    data class Call(
        val tel: String
    ) : AppIntent

    object Hangup : AppIntent

    data class Mute(val isMute: Boolean) : AppIntent

    data class UseSpeakerphone(val isUse: Boolean) : AppIntent

    data class SendDtmf(val digits:String):AppIntent

}

sealed interface AppUiState {
    object WaitToLogin : AppUiState

    object LoginSuccess : AppUiState

    class LoginFailed(
        val errorMsg: String,
    ) : AppUiState


    object LogoutSuccess : AppUiState

    class LogoutFailed(val errorMsg: String) : AppUiState

    class CallFailed(val errorMsg: String) : AppUiState

    object OnCallStart : AppUiState

    object OnRinging : AppUiState

    object OnCallCanceled : AppUiState

    object OnCallRefused : AppUiState

    object OnCalling : AppUiState

    class OnCallFailure(
        val errorMsg: String
    ) : AppUiState

    class OnCallingEnd(val isPeerHangup: Boolean) : AppUiState

    class OnInnerSdkError(
        val errorCode: Int,
        val errorMessage: String
    ) : AppUiState

    class OnRefreshTokenFailed(val errorMsg: String):AppUiState

    object OnAccessTokenHasExpired:AppUiState
}





