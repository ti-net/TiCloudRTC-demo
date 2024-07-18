package com.example.common

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.common.bean.LoginParams
import com.example.common.http.HttpServiceManager
import com.tencent.bugly.crashreport.CrashReport
import com.tinet.ticloudrtc.DestroyResultCallback
import com.tinet.ticloudrtc.TiCloudRTC
import com.tinet.ticloudrtc.TiCloudRTCEventListener
import com.tinet.ticloudrtc.bean.CallOption
import com.tinet.ticloudrtc.contants.CallScene
import io.agora.rtc.IRtcEngineEventHandler
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class AppViewModel : ViewModel() {

    /** UI 状态 */
    private val _appUiState = MutableStateFlow<AppUiState>(AppUiState.WaitToLogin)
    val appUiState = _appUiState.asStateFlow()

    /** 开发者模式标识，true：开发者模式，false：非开发者模式 */
    private val _isDevMode = MutableStateFlow(true)
    val isDevMode = _isDevMode.asStateFlow()

    /** 保存登录信息标识，true：登录时保存登录信息，false：登录时不保存登录信息 */
    private val _isSaveLoginMessage = MutableStateFlow(true)
    val isSaveLoginMessage = _isSaveLoginMessage.asStateFlow()

    /** 通话事件计时器 */
    internal var callingTimer: Timer? = null

    internal var rtcClient: TiCloudRTC? = null

    /** 当前通话中号码 */
    private var currentTel = ""

    /** 当前 dtmf 输入记录 */
    private var currentDtmfHistory = ""

    private var enterpriseId = ""
    private var username = ""
    private var password = ""
    private var callerNumber = ""

    private val Context.loginDataStore: DataStore<Preferences> by preferencesDataStore("login_message")
    private val KEY_SELECTED_ENV_INDEX = intPreferencesKey("key_selected_env_index")
    private val KEY_ENTERPRISE_ID = stringPreferencesKey("key_enterprise_id")
    private val KEY_USERNAME_OR_USER_ID = stringPreferencesKey("key_username_or_user_id")
    private val KEY_PASSWORD_OR_ACCESS_TOKEN = stringPreferencesKey("key_password_or_access_token")
    private val KEY_CALLER_NUMBER = stringPreferencesKey("key_caller_number")
    private val KEY_IS_SAVE_LOGIN_MESSAGE = booleanPreferencesKey("key_is_save_login_message")

    suspend fun getLoginMessageFromLocalStore(context: Context): StateFlow<LoginMessage> {
        return context.loginDataStore.data.map {
            _isSaveLoginMessage.value = it[KEY_IS_SAVE_LOGIN_MESSAGE] == true
            LoginMessage(
                selectedEnvIndex = it[KEY_SELECTED_ENV_INDEX] ?: 0,
                enterpriseId = it[KEY_ENTERPRISE_ID] ?: "",
                usernameOrUserId = it[KEY_USERNAME_OR_USER_ID] ?: "",
                passwordOrAccessToken = it[KEY_PASSWORD_OR_ACCESS_TOKEN] ?: "",
                callerNumber = it[KEY_CALLER_NUMBER] ?: ""
            )
        }.stateIn(viewModelScope)
    }

    fun handleIntent(intent: AppIntent) {
        when (intent) {
            is AppIntent.Login -> login(intent)
            is AppIntent.Logout -> logout()
            is AppIntent.Call -> call(intent)
            is AppIntent.Hangup -> hangup()
            is AppIntent.ClickDtmfButton -> clickDtmfButton(intent)
            is AppIntent.ClickMuteButton -> clickMuteButton(intent)
            is AppIntent.ClickSpeakerPhoneButton -> clickSpeakerPhoneButton(intent)
            is AppIntent.SendDtmf -> sendDtmf(intent)
        }
    }

    fun switchDevMode() {
        _isDevMode.value = _isDevMode.value.not()
    }

    fun switchSaveLoginMsgMode() {
        _isSaveLoginMessage.value = _isSaveLoginMessage.value.not()
    }

    fun isRtcClientInit() = rtcClient != null

    internal fun resetCallingState() {
        callingTimer?.cancel()
        currentTel = ""
        currentDtmfHistory = ""
    }

    private fun clickDtmfButton(intent: AppIntent.ClickDtmfButton){
        _appUiState.update {
            if (it is AppUiState.OnCalling) {
                val isShowDtmfPanel = !it.isShowDtmfPanel
                val biggerText = if(isShowDtmfPanel) currentDtmfHistory else currentTel
                it.copy(isShowDtmfPanel = isShowDtmfPanel, biggerText = biggerText)
            }else {
                it
            }
        }
    }

    private fun sendDtmf(intent: AppIntent.SendDtmf) {
        currentDtmfHistory += intent.digits
        rtcClient?.dtmf(intent.digits)
        _appUiState.update { state->
            if(state is AppUiState.OnCalling){
                state.copy(biggerText = currentDtmfHistory)
            }else {
                state
            }
        }
    }

    private fun clickSpeakerPhoneButton(intent: AppIntent.ClickSpeakerPhoneButton) {
        _appUiState.update { state ->
            if(state is AppUiState.OnCalling){
                rtcClient?.setEnableSpeakerphone(!state.isUseSpeakerPhone)
                state.copy(isUseSpeakerPhone = !state.isUseSpeakerPhone)
            } else {
                state
            }
        }
    }

    private fun clickMuteButton(muteIntent: AppIntent.ClickMuteButton) {
        _appUiState.update {state ->
            if(state is AppUiState.OnCalling){
                rtcClient?.setEnableLocalAudio(state.isMuted)
                state.copy(isMuted = !state.isMuted)
            }else{
                state
            }
        }
    }

    private fun hangup() {
        rtcClient?.hangup()
    }

    private fun call(callIntent: AppIntent.Call) {
        if (callIntent.tel.isEmpty() && callIntent.type == CallScene.CallerScene.value) {
            _appUiState.value = AppUiState.CallFailed("号码不正确")
            return
        }
        currentTel = callIntent.tel
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


    private fun login(loginIntent: AppIntent.Login) {

        CrashReport.initCrashReport(loginIntent.context, BuildConfig.BUGLY_APPID, BuildConfig.DEBUG)

        Log.i("rtc_android", """$loginIntent""")

        if (
            loginIntent.platformUrl.isEmpty() ||
            loginIntent.enterpriseId.isEmpty() ||
            loginIntent.usernameOrUserId.isEmpty() ||
            loginIntent.passwordOrAccessToken.isEmpty()
        ) {
            _appUiState.value = AppUiState.LoginFailed("参数不正确")
            return
        }

        HttpServiceManager.url = loginIntent.platformUrl

        enterpriseId = loginIntent.enterpriseId
        username = loginIntent.usernameOrUserId
        password = loginIntent.passwordOrAccessToken
        callerNumber = loginIntent.callerNumber

        loginExt(
            loginIntent = loginIntent,
            loginParams = LoginParams(
                enterpriseId = enterpriseId.toInt(),
                username = username,
                password = password
            ),
            callerNumber = callerNumber,
            onSaveLoginMessage = ::saveLoginState,
            onRtcClientCreated = {
                rtcClient = it
                rtcClient!!.setEventListener(CustomEventListener())
            },
            onUiStateUpdate = { _appUiState.value = it }
        )
    }

    private fun saveLoginState(loginIntent: AppIntent.Login) {
        viewModelScope.launch {
            launch {
                loginIntent.context.loginDataStore.edit {
                    it[KEY_SELECTED_ENV_INDEX] = loginIntent.selectedEnvIndex
                    it[KEY_ENTERPRISE_ID] = this@AppViewModel.enterpriseId
                    it[KEY_USERNAME_OR_USER_ID] = this@AppViewModel.username
                    if (isSaveLoginMessage.value) {
                        it[KEY_PASSWORD_OR_ACCESS_TOKEN] = this@AppViewModel.password
                    } else {
                        it[KEY_PASSWORD_OR_ACCESS_TOKEN] = ""
                    }
                    it[KEY_CALLER_NUMBER] = this@AppViewModel.callerNumber
                    it[KEY_IS_SAVE_LOGIN_MESSAGE] =
                        this@AppViewModel.isSaveLoginMessage.value
                }

            }
        }
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

    internal inner class CustomEventListener : TiCloudRTCEventListener() {

        override fun onCallingStart(requestUniqueId: String) {
            _appUiState.value = AppUiState.OnCallStart(currentTel)
        }

        override fun onRinging() {
            _appUiState.value = AppUiState.OnRinging()
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
            _appUiState.value = AppUiState.OnCalling(
                biggerText = currentTel,
                smallerText = "00:00"
            )
            callingTimer = Timer().apply {
                schedule(object : TimerTask() {
                    val startTime = Date().time

                    @SuppressLint("ConstantLocale")
                    val sdp = SimpleDateFormat("mm:ss", Locale.getDefault())

                    override fun run() {
                        _appUiState.update { state ->
                            if (state is AppUiState.OnCalling) {
                                state.copy(smallerText = sdp.format(Date(Date().time - startTime)))
                            } else {
                                state
                            }
                        }
                    }

                }, 0, 1000)
            }
        }

        override fun onCallingEnd(errorCode: Int, errorMessage: String, sipCode: Int) {
            _appUiState.value = AppUiState.OnCallFailure("$errorCode $errorMessage $sipCode")
            resetCallingState()
        }


        override fun onCallFailure(errorCode: Int, failureMessage: String) {
            _appUiState.value = AppUiState.OnCallFailure(failureMessage)
            resetCallingState()
        }

        override fun onLocalHangup() {
            _appUiState.value = AppUiState.OnCallingEnd(false)
            resetCallingState()
        }

        override fun onRemoteHangup() {
            _appUiState.value = AppUiState.OnCallingEnd(true)
            resetCallingState()
        }

        override fun onAccessTokenWillExpire(accessToken: String) {
            onAccessTokenWillExpireExt(
                rtcClient=rtcClient!!,
                accessToken = accessToken,
                loginParams = LoginParams(
                    enterpriseId = enterpriseId.toInt(),
                    username = username,
                    password = password
                ),
                onUiStateUpdate = { _appUiState.value = it }
            )
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

        override fun onNetworkQuality(uid: Int, txQuality: Int, rxQuality: Int) {
            Log.i("onNetworkQuality", "uid: $uid, txQuality: $txQuality, rxQuality: $rxQuality")
        }

        override fun onError(errorCode: Int, errorMessage: String) {
            _appUiState.value = AppUiState.OnInnerSdkError(errorCode, errorMessage)
            resetCallingState()
        }

        override fun onRemoteLogin() {
            _appUiState.value = AppUiState.OnKickOut
        }

        override fun onLocalNoVoiceStreamSent() {
            _appUiState.value = AppUiState.OnLocalNoVoiceStreamSent
        }

        override fun onUserFieldModifiedByConfig(
            removedCharList: List<String>,
            srcUserField: String,
            finalUserField: String
        ) {
            _appUiState.value = AppUiState.OnUserFieldModified(removedCharList)
        }

        override fun onLocalAudioStats(stats: IRtcEngineEventHandler.LocalAudioStats?) {
            Log.i(LOG_TAG, "onLocalAudioStats: $stats")
        }

        override fun onRemoteAudioStats(stats: IRtcEngineEventHandler.RemoteAudioStats?) {
            Log.i(LOG_TAG, "onRemoteAudioStats: $stats")
        }

        override fun onLocalAudioStateChanged(state: Int, error: Int) {
            Log.i(LOG_TAG, "onLocalAudioStateChanged: $state, $error")
        }

        override fun onRemoteAudioStateChanged(uid: Int, state: Int, reason: Int, elapsed: Int) {
            Log.i(LOG_TAG, "onRemoteAudioStateChanged: $uid, $state, $reason, $elapsed")
        }
    }

    companion object {
        internal var LOG_TAG = AppViewModel::class.java.simpleName

        const val DEFAULT_BIGGER_TEXT = ""
        const val CALLING_TIP = "外呼中..."
        const val DEFAULT_DTMF_PANEL_STATE = false

        const val DEFAULT_RINGING_SMALLER_TEXT = "播放铃声中..."
    }
}

sealed interface AppIntent {
    data class Login(
        val context: Context,
        val selectedEnvIndex: Int,
        val platformUrl: String,
        val enterpriseId: String,
        val usernameOrUserId: String,
        val passwordOrAccessToken: String,
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

    object ClickDtmfButton : AppIntent

    object ClickMuteButton : AppIntent

    object ClickSpeakerPhoneButton : AppIntent

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

    data class OnCallStart(val biggerText: String) : AppUiState

    data class OnRinging(
        val smallerText: String = AppViewModel.DEFAULT_RINGING_SMALLER_TEXT
    ) : AppUiState

    object OnCallCanceled : AppUiState

    object OnCallRefused : AppUiState

    data class OnCalling(
        /** 静音状态，true：静音，false：非静音 */
        val isMuted: Boolean = false,

        /** 扬声器状态，true：扬声器打开，false：扬声器关闭 */
        val isUseSpeakerPhone: Boolean = false,

        /** dtmf 键盘打开标识，true：打开 dtmf 键盘，false：关闭 dtmf 键盘 */
        val isShowDtmfPanel:Boolean = AppViewModel.DEFAULT_DTMF_PANEL_STATE,

        /** 呼叫中界面大文本内容 */
        val biggerText:String = AppViewModel.DEFAULT_BIGGER_TEXT,

        /** 呼叫中界面小文本内容 */
        val smallerText:String = AppViewModel.CALLING_TIP
    ) : AppUiState

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

    object OnLocalNoVoiceStreamSent : AppUiState

    class OnUserFieldModified(val removedCharList: List<String>) : AppUiState
}

data class LoginMessage(
    val selectedEnvIndex: Int = 0,
    val enterpriseId: String = "",
    val usernameOrUserId: String = "",
    val passwordOrAccessToken: String = "",
    val callerNumber: String = ""
)





