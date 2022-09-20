package com.example.common

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.common.bean.BaseResult
import com.example.common.bean.LoginParams
import com.example.common.bean.LoginResult
import com.example.common.http.HttpServiceManager
import com.example.common.http.HttpUtils.enqueueWithLog
import com.example.common.http.HttpUtils.parseHttpResult
import com.tencent.bugly.crashreport.CrashReport
import com.tinet.ticloudrtc.CreateResultCallback
import com.tinet.ticloudrtc.DestroyResultCallback
import com.tinet.ticloudrtc.TiCloudRTC
import com.tinet.ticloudrtc.TiCloudRTCEventListener
import com.tinet.ticloudrtc.bean.CallOption
import com.tinet.ticloudrtc.bean.CreateClientOption
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class MainActivityViewModel : ViewModel() {

    val intentChannel: Channel<AppIntent> = Channel(Channel.UNLIMITED)

    private val _appUiState = MutableStateFlow<AppUiState>(AppUiState.WaitToLogin)
    val appUiState = _appUiState.asStateFlow()

    private val _muteState = MutableStateFlow(false)
    val muteState = _muteState.asStateFlow()

    private val _speakerphoneOpenSate = MutableStateFlow(false)
    val speakerphoneOpenState = _speakerphoneOpenSate.asStateFlow()

    private val DEFAULT_BIGGER_TEXT = ""
    private val _biggerText = MutableStateFlow(DEFAULT_BIGGER_TEXT)
    val biggerText = _biggerText.asStateFlow()

    private val CALLING_TIP = "外呼中..."
    private val _smallText = MutableStateFlow(CALLING_TIP)
    val smallText = _smallText.asStateFlow()

    private val DEFAULT_DTMF_PANEL_STATE = false
    private val _isShowDtmfPanel =
        MutableStateFlow(DEFAULT_DTMF_PANEL_STATE)
    val isShowDtmfPanel = _isShowDtmfPanel.asStateFlow()

    private val _isDevMode = MutableStateFlow(true)
    val isDevMode = _isDevMode.asStateFlow()

    private var callingTimer: Timer? = null

    private var rtcClient: TiCloudRTC? = null

    private var currentTel = ""
    private var currentDtmfHistory = ""

    private var enterpriseId = ""
    private var username = ""
    private var password = ""

    private val Context.loginDataStore: DataStore<Preferences> by preferencesDataStore("login_message")
    private val KEY_ENTERPRISE_ID = stringPreferencesKey("key_enterprise_id")
    private val KEY_USERNAME = stringPreferencesKey("key_username")
    private val KEY_PASSWORD = stringPreferencesKey("key_password")

    fun getLoginMessageFromLocalStore(context: Context): Flow<LoginMessage> {
        return context.loginDataStore.data.map {
            LoginMessage(
                enterpriseId = it[KEY_ENTERPRISE_ID] ?: "",
                username = it[KEY_USERNAME] ?: "",
                password = it[KEY_PASSWORD] ?: ""
            )
        }
    }

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

    fun switchDevMode() {
        _isDevMode.value = _isDevMode.value.not()
    }

    fun isRtcClientInit() = rtcClient != null

    fun isMute() = muteState.value

    private fun resetCallingState() {
        _muteState.value = false
        _speakerphoneOpenSate.value = false
        callingTimer?.cancel()
        currentTel = ""
        currentDtmfHistory = ""
        _biggerText.value = DEFAULT_BIGGER_TEXT
        _smallText.value = CALLING_TIP
        _isShowDtmfPanel.value = DEFAULT_DTMF_PANEL_STATE
    }

    fun switchDtmfShowState() {
        _isShowDtmfPanel.value = _isShowDtmfPanel.value.not()
        if (_isShowDtmfPanel.value) {
            _biggerText.value = currentDtmfHistory
        } else {
            _biggerText.value = currentTel
        }
    }

    fun isUseSpeakerphone(): Boolean = rtcClient?.isSpeakerphoneEnabled() ?: false

    private fun sendDtmf(intent: AppIntent.SendDtmf) {
        currentDtmfHistory += intent.digits
        _biggerText.value = currentDtmfHistory
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
        if (callIntent.tel.isEmpty() && callIntent.type == 6) {
            _appUiState.value = AppUiState.CallFailed("号码不正确")
            return
        }
        currentTel = callIntent.tel
        _biggerText.value = currentTel
        rtcClient?.call(
            CallOption(
                tel = callIntent.tel,
                clid = callIntent.clid,
                requestUniqueId = "",
                userField = callIntent.userField,
                type = callIntent.type
            )
        )
    }


    /**
     * 注：当前的登录只是虚假的登录，仅供 demo 获取初始化参数，
     *      正式使用时，请使用 /interface/v10/rtc/getAccessToken 获取初始化参数
     */
    private fun login(loginIntent: AppIntent.Login) {

        CrashReport.initCrashReport(loginIntent.context, BuildConfig.BUGLY_APPID, BuildConfig.DEBUG)

        Log.i("rtc_android", """$loginIntent""")

        if (
            loginIntent.platformUrl.isEmpty() ||
            loginIntent.enterpriseId.isEmpty() ||
            loginIntent.username.isEmpty() ||
            loginIntent.password.isEmpty()
        ) {
            _appUiState.value = AppUiState.LoginFailed("参数不正确")
            return
        }

        HttpServiceManager.url = loginIntent.platformUrl

        enterpriseId = loginIntent.enterpriseId
        username = loginIntent.username
        password = loginIntent.password

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
                Log.i(LOG_TAG, "登录接口请求成功")
                Log.i(LOG_TAG, "返回结果： ${response.body()}")
                val loginResult = response.parseHttpResult()
                if (loginResult.code == 200) {
                    Log.i(LOG_TAG, "获取初始化参数成功")

                    var rtcEndpoint = ""
                    var enterpriseId = 0
                    var accessToken = ""

                    viewModelScope.launch {
                        loginIntent.context.loginDataStore.edit {
                            it[KEY_ENTERPRISE_ID] = this@MainActivityViewModel.enterpriseId
                            it[KEY_USERNAME] = this@MainActivityViewModel.username
                            it[KEY_PASSWORD] = this@MainActivityViewModel.password
                        }
                    }

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
                        },
                        resultCallback = object : CreateResultCallback {
                            override fun onFailed(errorCode: Int, errorMessage: String) {
                                _appUiState.value = AppUiState.LoginFailed(errorMessage)
                            }

                            override fun onSuccess(rtcClient: TiCloudRTC) {
                                this@MainActivityViewModel.rtcClient = rtcClient
                                rtcClient.setEventListener(CustomEventListener())
                                _appUiState.value = AppUiState.LoginSuccess
                            }
                        }
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

                    @SuppressLint("ConstantLocale")
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

        override fun onAccessTokenHasExpired() {
            Log.i(
                "AccessToken",
                "onAccessTokenHasExpired ---- ${
                    SimpleDateFormat(
                        "yyyy-MM-dd hh:mm:ss",
                        Locale.getDefault()
                    ).format(Date())
                }"
            )
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
        val tel: String,
        val clid: String,
        val userField: String,
        val type: Int
    ) : AppIntent

    object Hangup : AppIntent

    data class Mute(val isMute: Boolean) : AppIntent

    data class UseSpeakerphone(val isUse: Boolean) : AppIntent

    data class SendDtmf(val digits: String) : AppIntent

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

    class OnRefreshTokenFailed(val errorMsg: String) : AppUiState

    object OnAccessTokenHasExpired : AppUiState
}

data class LoginMessage(
    val enterpriseId: String = "",
    val username: String = "",
    val password: String = ""
)





