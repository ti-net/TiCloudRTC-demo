package com.example.common

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.common.http.HttpServiceManager
import com.tencent.bugly.crashreport.CrashReport
import com.tinet.ticloudrtc.DestroyResultCallback
import com.tinet.ticloudrtc.ErrorCode
import com.tinet.ticloudrtc.TiCloudRTC
import com.tinet.ticloudrtc.TiCloudRTCEventListener
import com.tinet.ticloudrtc.bean.CallOption
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class AppViewModel : ViewModel() {

    /** 事件接收 */
    val intentChannel: Channel<AppIntent> = Channel(Channel.UNLIMITED)

    /** UI 状态 */
    internal val _appUiState = MutableStateFlow<AppUiState>(AppUiState.WaitToLogin)
    val appUiState = _appUiState.asStateFlow()

    /** 静音状态，true：静音，false：非静音 */
    internal val _muteState = MutableStateFlow(false)
    val muteState = _muteState.asStateFlow()

    /** 扬声器状态，true：扬声器打开，false：扬声器关闭 */
    internal val _speakerphoneOpenSate = MutableStateFlow(false)
    val speakerphoneOpenState = _speakerphoneOpenSate.asStateFlow()

    internal val DEFAULT_BIGGER_TEXT = ""

    /** 呼叫中界面大文本内容 */
    internal val _biggerText = MutableStateFlow(DEFAULT_BIGGER_TEXT)
    val biggerText = _biggerText.asStateFlow()

    internal val CALLING_TIP = "外呼中..."

    /** 呼叫中界面小文本内容 */
    internal val _smallText = MutableStateFlow(CALLING_TIP)
    val smallText = _smallText.asStateFlow()

    internal val DEFAULT_DTMF_PANEL_STATE = false

    /** dtmf 键盘打开标识，true：打开 dtmf 键盘，false：关闭 dtmf 键盘 */
    internal val _isShowDtmfPanel =
        MutableStateFlow(DEFAULT_DTMF_PANEL_STATE)
    val isShowDtmfPanel = _isShowDtmfPanel.asStateFlow()

    /** 开发者模式标识，true：开发者模式，false：非开发者模式 */
    internal val _isDevMode = MutableStateFlow(true)
    val isDevMode = _isDevMode.asStateFlow()

    /** 保存登录信息标识，true：登录时保存登录信息，false：登录时不保存登录信息 */
    internal val _isSaveLoginMessage = MutableStateFlow(true)
    val isSaveLoginMessage = _isSaveLoginMessage.asStateFlow()

    /** 通话事件计时器 */
    internal var callingTimer: Timer? = null

    internal var rtcClient: TiCloudRTC? = null

    /** 当前通话中号码 */
    internal var currentTel = ""

    /** 当前 dtmf 输入记录 */
    internal var currentDtmfHistory = ""

    internal var enterpriseId = ""
    internal var username = ""
    internal var password = ""
    internal var callerNumber = ""

    internal val Context.loginDataStore: DataStore<Preferences> by preferencesDataStore("login_message")
    internal val KEY_ENTERPRISE_ID = stringPreferencesKey("key_enterprise_id")
    internal val KEY_USERNAME = stringPreferencesKey("key_username")
    internal val KEY_PASSWORD = stringPreferencesKey("key_password")
    internal val KEY_CALLER_NUMBER = stringPreferencesKey("key_caller_number")
    internal val KEY_IS_SAVE_LOGIN_MESSAGE = booleanPreferencesKey("key_is_save_login_message")

    suspend fun getLoginMessageFromLocalStore(context: Context): StateFlow<LoginMessage> {
        return context.loginDataStore.data.map {
            _isSaveLoginMessage.value = it[KEY_IS_SAVE_LOGIN_MESSAGE] == true
            LoginMessage(
                enterpriseId = it[KEY_ENTERPRISE_ID] ?: "",
                username = it[KEY_USERNAME] ?: "",
                password = it[KEY_PASSWORD] ?: "",
                callerNumber = it[KEY_CALLER_NUMBER] ?: ""
            )
        }.stateIn(viewModelScope)
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

    fun switchSaveLoginMsgMode() {
        _isSaveLoginMessage.value = _isSaveLoginMessage.value.not()
    }

    fun isRtcClientInit() = rtcClient != null

    fun isMute() = muteState.value

    internal fun resetCallingState() {
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

    internal fun sendDtmf(intent: AppIntent.SendDtmf) {
        currentDtmfHistory += intent.digits
        _biggerText.value = currentDtmfHistory
        rtcClient?.dtmf(intent.digits)
    }

    internal fun useSpeakerphone(intent: AppIntent.UseSpeakerphone) {
        rtcClient?.setEnableSpeakerphone(intent.isUse)
        _speakerphoneOpenSate.value = intent.isUse
    }

    internal fun mute(muteIntent: AppIntent.Mute) {
        rtcClient?.setEnableLocalAudio(muteIntent.isMute.not())
        _muteState.value = muteIntent.isMute
    }

    internal fun hangup() {
        rtcClient?.hangup()
    }

    internal fun call(callIntent: AppIntent.Call) {
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
            ).apply {
                callerNumber = callIntent.callerNumber
                obClidAreaCode = callIntent.obClidAreaCode
                obClidGroup = callIntent.obClidGroup
            }
        )
    }


    internal fun login(loginIntent: AppIntent.Login) {

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
        callerNumber = loginIntent.callerNumber

        loginExt(loginIntent)
    }

    internal fun saveLoginState(loginIntent: AppIntent.Login) {
        viewModelScope.launch {
            launch {
                loginIntent.context.loginDataStore.edit {
                    it[KEY_ENTERPRISE_ID] = this@AppViewModel.enterpriseId
                    it[KEY_USERNAME] = this@AppViewModel.username
                    if (isSaveLoginMessage.value) {
                        it[KEY_PASSWORD] = this@AppViewModel.password
                    } else {
                        it[KEY_PASSWORD] = ""
                    }
                    it[KEY_CALLER_NUMBER] = this@AppViewModel.callerNumber
                    it[KEY_IS_SAVE_LOGIN_MESSAGE] =
                        this@AppViewModel.isSaveLoginMessage.value
                }

            }
        }
    }

    internal fun logout() {
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

    internal inner class CustomEventListener : TiCloudRTCEventListener() {

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
            onAccessTokenWillExpireExt(accessToken)
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
            when (errorCode) {
                // 如果是呼叫相关错误码则重置呼叫中界面
                ErrorCode.ERR_CALL_FAILED_PARAMS_INCORRECT,
                ErrorCode.ERR_CALL_FAILED_CALL_REPEAT,
                ErrorCode.ERR_CALL_FAILED_REMOTE_OFFLINE,
                ErrorCode.ERR_CALL_FAILED_NET_ERROR,
                ErrorCode.ERR_CALL_FAILED_RTM_ERROR,
                ErrorCode.ERR_CALL_HOTLINE_NOT_EXIST -> resetCallingState()
            }
        }

        override fun onRemoteLogin() {
            _appUiState.value = AppUiState.OnKickOut
        }
    }

    companion object {
        internal var LOG_TAG = AppViewModel::class.java.simpleName
    }
}

sealed interface AppIntent {
    data class Login(
        val context: Context,
        val platformUrl: String,
        val enterpriseId: String,
        val username: String,
        val password: String,
        val callerNumber: String = ""
    ) : AppIntent

    object Logout : AppIntent

    data class Call(
        val tel: String,
        val clid: String,
        val userField: String,
        val type: Int
    ) : AppIntent {
        var callerNumber: String = ""
        var obClidAreaCode: String = ""
        var obClidGroup: String = ""
    }

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

    object OnAccessTokenWillExpire : AppUiState

    class OnRefreshTokenFailed(val errorMsg: String) : AppUiState

    object OnAccessTokenHasExpired : AppUiState

    object OnKickOut : AppUiState
}

data class LoginMessage(
    val enterpriseId: String = "",
    val username: String = "",
    val password: String = "",
    val callerNumber: String = ""
)





