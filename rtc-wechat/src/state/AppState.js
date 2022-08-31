
import { defineStore } from 'pinia'
import { ref } from 'vue'
import { TiCloudRTC } from '../libs/ticloudrtc-wechat-sdk-1.0.0'
import { HttpApiManager } from '../http/HttpApiManager'

const AppModel = defineStore("AppState", () => {

    const appUiState = ref(AppUiState.WaitToLogin)

    /** TiCloudRtc */
    const rtcClient = ref(null)

    const callingTimer = null

    let startTime = null

    const currentTel = ref("")

    const enterpriseId = ref("")

    const username = ref("")

    const password = ref("")

    function formatTime(seconds) {
        let m = parseInt(seconds / 60 % 60);
        m = m < 10 ? "0" + m : m
        let s = parseInt(seconds % 60);
        s = s < 10 ? "0" + s : s;
        return  m + ':' + s;
    }


    function login(
        tokenServerUrl,
        enterpriseId,
        username,
        password
    ) {
        console.log("login...")

        let that = this

        HttpApiManager.initService(tokenServerUrl)

        that.enterpriseId = enterpriseId

        that.username = username

        that.password = password

        HttpApiManager.httpService.login(
            enterpriseId,
            username,
            password
        ).then((result) => {
            console.log("login result:", result)
            if (result.statusCode == 200 && result.data) {
                let data = result.data
                if (data.code == 200 && data.result) {
                    let result = data.result
                    let option = new TiCloudRTC.CreateClientOption(
                        result.rtcEndpoint,
                        result.enterpriseId,
                        username,
                        result.accessToken,
                        true
                    )
                    
                    TiCloudRTC.createClient(option, {
                        onSuccess(rtcClient) {
                            that.rtcClient = rtcClient

                            initEventListener(that)

                            that.appUiState = new UiStatePayload(
                                AppUiState.LoginSuccess
                            )

                        },
                        onFailed(errorCode, errorMessage) {
                            that.appUiState = new UiStatePayload(
                                AppUiState.LoginFailed,
                                new StateLoginFailed(`errorCode:${errorCode},errorMessage:${errorMessage}`)
                            )
                        }
                    })
                } else {
                    that.appUiState = new UiStatePayload(
                        AppUiState.LoginFailed,
                        new StateLoginFailed(`code:${data.code}, message:${data.message}`)
                    )
                }
            } else {
                that.appUiState = new UiStatePayload(
                    AppUiState.LoginFailed,
                    new StateLoginFailed(`code:${result.statusCode}, message:${result.errMsg}`)
                )
            }

        }).catch((error) => {
            console.log(error)
            that.appUiState = new UiStatePayload(
                AppUiState.LoginFailed,
                new StateLoginFailed(`${error}`)
            )
        })


    }

    function logout() {
        let that = this
        this.rtcClient?.destroyClient({
            onSuccess: () => that.appUiState = new UiStatePayload(AppUiState.LogoutSuccess),
            onFailed: (errorCode, errorMessage) => new UiStatePayload(
                AppUiState.LogoutFailed,
                new StateLogoutFailed(`code:${errorCode},message:${errorMessage}`)
            )
        })
    }

    function call(tel, clid, requestUniqueId, userField, type) {
        this.currentTel = tel
        this.rtcClient?.call(new TiCloudRTC.CallOption(
            tel,
            clid,
            requestUniqueId,
            userField,
            type
        ))
    }

    function hangup() {
        this.rtcClient?.hangup()
    }

    function sendDtmf(digits) {
        this.rtcClient?.dtmf(digits)
    }

    function initEventListener(model) {
        let client = model.rtcClient
        let that = model

        client
            .on(TiCloudRTC.TiCloudRtcEvent.error, (errorCode, errorMessage) => {
                model.appUiState = new UiStatePayload(AppUiState.OnInnerSdkError,new StateOnInnerSdkError(errorCode,errorMessage))
            })
            .on(TiCloudRTC.TiCloudRtcEvent.callingStart, (requestUniqueId) => {
                clearInterval(that.callingTimer)
                model.appUiState = new UiStatePayload(AppUiState.OnCallStart)
            })
            .on(TiCloudRTC.TiCloudRtcEvent.publishing, url => {
                model.appUiState = new UiStatePayload(AppUiState.Publishing, new StatePublishing(url))
            })
            .on(TiCloudRTC.TiCloudRtcEvent.ringing, (url) => {
                model.appUiState = new UiStatePayload(AppUiState.OnRinging, new StateOnRinging(url))
            })
            .on(TiCloudRTC.TiCloudRtcEvent.callCancelled, () => {
                model.appUiState = new UiStatePayload(AppUiState.OnCallCanceled)
            })
            .on(TiCloudRTC.TiCloudRtcEvent.callRefused, () => {
                model.appUiState = new UiStatePayload(AppUiState.OnCallRefused)
            })
            .on(TiCloudRTC.TiCloudRtcEvent.calling, () => {
                model.appUiState = new UiStatePayload(AppUiState.OnCalling)
                that.startTime = new Date()
                that.callingTimer = setInterval(()=>{
                    let sceonds = ((new Date()).getTime() - that.startTime.getTime())/1000
                    model.appUiState = new UiStatePayload(AppUiState.OnSmallTextUpdate,new StateOnSmallTextUpdate(that.formatTime(sceonds)))
                },1000)
            })
            .on(TiCloudRTC.TiCloudRtcEvent.callingEnd, (isPeerHangup) => {
                model.appUiState = new UiStatePayload(AppUiState.OnCallingEnd, new StateOnCallingEnd(isPeerHangup))
            })
            .on(TiCloudRTC.TiCloudRtcEvent.callFailure, (errorCode, failureMessage) => {
                model.appUiState = new UiStatePayload(AppUiState.OnCallFailure, new StateOnCallFailure(`call failed. code:${errorCode},message:${failureMessage}`))
            })
            .on(TiCloudRTC.TiCloudRtcEvent.accessTokenWillExpire, (accessToken) => {
                console.log("accessTokenWillExpire", accessToken)

                HttpApiManager.httpService.login(
                    model.enterpriseId,
                    model.username,
                    model.password
                ).then((result) => {
                    console.log("login result:", result)
                    if (result.statusCode == 200 &&
                        result.data &&
                        result.data.code == 200 &&
                        result.data.result) {
                        let data = result.data
                        model.rtcClient.renewAccessToken(data.result.accesToken)
                    } else {
                        model.appUiState = new UiStatePayload(AppUiState.OnRefreshTokenFailed, new StateOnRefreshTokenFailed(`获取新的 access token 失败.code:${result.code},message:${result.message}`))
                    }

                }).catch((error) => {
                    model.appUiState = new UiStatePayload(
                        AppUiState.OnRefreshTokenFailed,
                        new StateOnRefreshTokenFailed(`获取新的 access token 失败.error:${error}`)
                    )
                })
            })
            .on(TiCloudRTC.TiCloudRtcEvent.accessTokenHasExpired, () => {
                console.log("accessTokenHasExpired")

                that.resetCallingState()

                model.rtcClient = null

                model.appUiState = new UiStatePayload(AppUiState.OnAccessTokenHasExpired)
            })
    }

    return {
        appUiState,
        startTime,
        callingTimer,
        formatTime,
        login,
        logout,
        call,
        hangup,
        sendDtmf
    }
})

const AppUiState = {
    WaitToLogin: "WaitToLogin",
    LoginSuccess: "LoginSuccess",
    LoginFailed: "LoginFailed",
    LogoutSuccess: "LogoutSuccess",
    LogoutFailed: "LogoutFailed",
    CallFailed: "CallFailed",
    OnCallStart: "OnCallStart",
    Publishing: "Publishing",
    OnRinging: "OnRinging",
    OnCallCanceled: "OnCallCanceled",
    OnCallRefused: "OnCallRefused",
    OnCalling: "OnCalling",
    OnCallFailure: "OnCallFailure",
    OnCallingEnd: "OnCallingEnd",
    OnInnerSdkError: "OnInnerSdkError",
    OnRefreshTokenFailed: "OnRefreshTokenFailed",
    OnAccessTokenHasExpired: "OnAccessTokenHasExpired",
    OnSmallTextUpdate:"OnSmallTextUpdate"
}

class UiStatePayload {
    constructor(stateName, stateDetail) {
        this.stateName = stateName,
            this.stateDetail = stateDetail
    }
}

class StatePublishing {
    constructor(url) { this.url = url }
}

class StateOnRinging {
    constructor(url) { this.url = url }
}

class StateLoginFailed {
    constructor(errorMessage) { this.errorMessage = errorMessage }
}

class StateLogoutFailed {
    constructor(errorMessage) { this.errorMessage = errorMessage }
}

class StateOnCallFailure {
    constructor(errorMessage) { this.errorMessage = errorMessage }
}

class StateOnCallingEnd {
    constructor(isPeerHangup) { this.isPeerHangup = isPeerHangup }
}

class StateOnInnerSdkError {
    constructor(errorCode, errorMessage) {
        this.errorCode = errorCode
        this.errorMessage = errorMessage
    }
}

class StateOnRefreshTokenFailed {
    constructor(errorMessage) { this.errorMessage = errorMessage }
}

class StateOnSmallTextUpdate{
    constructor(smallText){this.smallText = smallText}
}

// const AppIntent = {
//
// }

/** 外呼场景常量 */
const CallScene = {
    CallerScene: 6,  // 外呼场景
    AgentScene: 1    // 客服场景
}

export {
    AppModel,
    AppUiState,
    CallScene
}