<!-- eslint-disable vue/multi-word-component-names -->
<!-- eslint-disable vue/valid-template-root -->
<template>

    <view class="root-view">
        <image class="root-view-bg" src="../../static/calling/bg_call.png" mode="scaleToFill" />
        <live-player id="player" :src="rtmpPullUrl" mode="RTC" :sound-mode="soundMode" autoplay="false" class="player"
            @statechange="playerStateChange" @error="playerError" @netstatus="netstatus" />
        <live-pusher id="pusher" :url="rtmpPushUrl" mode="RTC" :enable-mic="enableMic" :enable-camera="false"
            autopush="false" class="pusher" @statechange="pusherStateChange" @error="pusherError" @netstatus="netstatus"
            waiting-image="../../static/login/logo.png" />
        <view class="top-area">
            <view class="current-phone-num">{{  currentPheneNum  }}</view>
            <view class="small-text">{{  smallText  }}</view>
        </view>
        <view class="center-area">
            <view v-if="isShowDtmfPanel" class="dtmf-keyboard-area">
                <view v-for="(row, rowIndex) in buttonChar" :key="rowIndex" class="keyboard-row">
                    <view v-for="(dtmfChat, chatIndex) in row" :key="chatIndex" class="dtmf-button"
                        @click="sendDtmf(dtmfChat)">
                        <text class="dtmf-text">{{  dtmfChat  }}</text>
                    </view>
                </view>

            </view>
            <view v-else class="control-area">
                <view class="control-button-img-area">
                    <image class="control-button-img" src="../../static/calling/ic_dtmf.png"
                        @click="switchDtmfShowState" />
                </view>
                <view class="control-button-img-area">
                    <image :class="{ 'control-button-img': true, 'enable': !enableMic }"
                        src="../../static/calling/ic_mic.png" @click="switchMuteState" />
                </view>
                <view class="control-button-img-area">
                    <image :class="{ 'control-button-img': true, 'enable': soundMode == 'speaker' }"
                        src="../../static/calling/ic_speaker.png" @click="switchSoundMode" />
                </view>
            </view>
        </view>
        <view class="bottom-area">
            <view class="hangupBtn" @click="hangup">挂断</view>
            <view v-if="isShowDtmfPanel" class="hideDtmfBtn" @click="switchDtmfShowState">隐藏</view>
        </view>
    </view>
</template>

<script>
import { TiCloudRTC } from "@tinet/ticloudrtc-wechat-sdk"
import { AppModel, AppUiState } from "@/state/AppState"

export default {
    data() {
        return {
            /* 当前电话号码 */
            currentPheneNum: "",
            /** 当前通话时间 */
            smallText: "外呼中",
            /** 标识是否显示 dtmf 按键面板, true 为显示 */
            isShowDtmfPanel: false,
            /** 按键字符 */
            buttonChar: [
                [1, 2, 3],
                [4, 5, 6],
                [7, 8, 9],
                ["*", "0", "#"]
            ],
            /** 拉流地址 */
            rtmpPullUrl: "",
            /** 推流地址 */
            rtmpPushUrl: "",
            appModel: null,
            pusherContext: null,
            playerContext: null,
            /** mic 标识, true 为打开 mic(非静音),false 为关闭 mic (静音) */
            enableMic: true,
            /** 音频输出方式,speaker: 扬声器, ear: 听筒 */
            soundMode: "ear",
        }
    },
    mounted() {
        console.log("calling page mounted")

        let that = this


        this.appModel = AppModel()

        this.appModel.$subscribe((mutation, state) => {
            switch (state.appUiState.stateName) {
                case AppUiState.Publishing:
                    that.publishing(state.appUiState.stateDetail.url)
                    break

                case AppUiState.OnRinging:
                    that.onRinging(state.appUiState.stateDetail.url)
                    that.smallText = "播放铃声中"
                    break
                case AppUiState.OnCallCanceled:
                    that.toastAndBackToMain("呼叫已取消")
                    break
                case AppUiState.OnCallRefused:
                    that.toastAndBackToMain("外呼被拒绝")
                    break
                case AppUiState.OnCallFailure:
                    that.toastAndBackToMain(`外呼错误: ${state.appUiState.stateDetail.errorMessage}`)
                    break
                case AppUiState.OnCallingEnd:
                    {
                        let hangupDetail
                        if (state.appUiState.stateDetail.isPeerHangup) {
                            hangupDetail = "对方挂断"
                        } else {
                            hangupDetail = "己方挂断"
                        }
                        that.toastAndBackToMain(`外呼结束: ${hangupDetail}`)
                    }
                    break
                case AppUiState.OnInnerSdkError:
                    that.onInnerSdkError(
                        state.appUiState.stateDetail.errorCode,
                        state.appUiState.stateDetail.errorMessage
                    )
                    that.backToMain()
                    break
                case AppUiState.OnRefreshTokenFailed:
                    that.toastAndBackToMain(`${state.appUiState.stateDetail.errorMessage}`)
                    break
                case AppUiState.OnAccessTokenHasExpired:
                    that.toastAndBackToMain(`access token 已过期`)
                    break
                case AppUiState.OnSmallTextUpdate:
                    that.smallText = state.appUiState.stateDetail.smallText
                    break
            }
        })
    },
    unmounted() {
        console.log("calling page unmounted")
        this.appModel.hangup()
    },
    onLoad(options) {
        if (options) {
            this.currentPheneNum = options.currentPheneNum
        }
        console.log("pusher context", this.pusherContext)

        this.pusherContext = uni.createLivePusherContext("pusher")
        this.playerContext = uni.createLivePlayerContext("player")


    },
    methods: {
        backToMain() {
            this.pusherContext?.stop({
                success() { console.log("已停止推流") },
                fail() { console.log("停止推流失败") }
            })
            this.playerContext?.stop({
                success() { console.log("已停止拉流") },
                fail() { console.log("停止拉流失败") }
            })
            // uni.navigateBack()

            uni.reLaunch({
                url: "/pages/index/index"
            })

            new Promise((resolve, reject) => {
                this.pusherContext?.stop({
                    success() {
                        console.log("已停止推流")
                        resolve()
                    },
                    fail() {
                        console.log("停止推流失败")
                        reject()
                    }
                })

            }).then(() => {
                new Promise((resolve, reject) => {
                    this.playerContext?.stop({
                        success() {
                            console.log("已停止拉流")
                            resolve()

                        },
                        fail() {
                            console.log("停止拉流失败")
                            reject()
                        }
                    })
                })
                    .finally(() => {
                        console.log("已完成关闭推流拉流")
                        // uni.navigateBack()
                        uni.switchTab({
                            url: "/pages/index/index"
                        })
                    })
            }).catch(err => {
                console.e(err)
            })

        },
        toastAndBackToMain(toastContent) {
            uni.showToast({
                title: `${toastContent}`,
                icon: 'none',
                mask: true
            })
            this.backToMain()
        },
        pusherStateChange(e) {
            console.log("pusher state change: ", e)
        },
        playerStateChange(e) {
            console.log("player state change: ", e)
        },
        playerError(e) {
            console.log("player error: ", e)
        },
        pusherError(e) {
            console.log("pusher error: ", e)
        },
        switchDtmfShowState() {
            this.isShowDtmfPanel = !this.isShowDtmfPanel
        },
        switchMuteState() {
            this.enableMic = !this.enableMic
        },
        switchSoundMode() {
            if (this.soundMode == "ear") {
                this.soundMode = "speaker"
            } else {
                this.soundMode = "ear"
            }
        },
        hangup() {
            // let that = this
            // this.pusherContext.stop({
            //     success() {
            //             console.log("已停止推流")
            //             that.appModel.hangup()
            //         },
            //         fail() {
            //             console.log("停止推流失败")
            //         }
            // })
            // this.playerContext.stop({
            //             success() {
            //                 console.log("已停止拉流")
            //             },
            //             fail() {
            //                 console.log("停止拉流失败")
            //             }
            //         })
            this.appModel.hangup()
        },
        sendDtmf(digits) {
            this.appModel.sendDtmf(digits)
        },
        publishing(url) {
            this.rtmpPushUrl = url
            console.log("push url: ", url)
            this.pusherContext.start({
                success() {
                    console.log("推流成功")
                    this.enableMic = true
                },
                fail(err) { console.log("推流失败", err) }
            })
        },
        onRinging(url) {
            this.rtmpPullUrl = url
            console.log("pull url: ", url)
            this.playerContext.play({
                success() { console.log("拉流成功") },
                fail() { console.log("拉流失败") }
            })
        },
        onInnerSdkError(errorCode, errorMessage) {
            uni.showToast({
                title: `内部错误.code:${errorCode},message:${errorMessage}`,
                icon: 'none',
                mask: true
            })
            if (errorCode == TiCloudRTC.ErrorCode.ERR_CALL_FAILED_PARAMS_INCORRECT ||
                errorCode == TiCloudRTC.ErrorCode.ERR_CALL_FAILED_CALL_REPEAT ||
                errorCode == TiCloudRTC.ErrorCode.ERR_CALL_FAILED_REMOTE_OFFLINE ||
                errorCode == TiCloudRTC.ErrorCode.ERR_CALL_FAILED_NET_ERROR ||
                errorCode == TiCloudRTC.ErrorCode.ERR_CALL_FAILED_RTM_ERROR
            ) {
                this.backToMain()
            }
        },
        // eslint-disable-next-line no-unused-vars
        netstatus(e) {
            // console.log("netstatus:" + JSON.stringify(e))
        },

    },
    onUnload() {

    }
}
</script>

<style >
page {
    width: 100%;
    height: 100%;
}
</style>

<style scoped>
.root-view {
    width: calc(100% - 80rpx);
    height: inherit;
    display: flex;
    flex-direction: column;
    align-items: center;
    padding-left: 40rpx;
    padding-right: 40rpx;

}

.root-view-bg {
    width: 100%;
    height: 100%;
    z-index: -2;
    position: fixed;
}

.top-area {
    width: inherit;
    margin-top: 120rpx;
    flex-shrink: 0;
}

.current-phone-num {
    width: inherit;
    height: 60rpx;

}

.center-area {
    width: 100%;
    flex-grow: 1;
    flex-shrink: 2;
    display: flex;
    flex-direction: column;
    justify-content: center;
}

.dtmf-keyboard-area {
    /* width: inherit; */
    height: 100%;
    padding: 40rpx;
    display: flex;
    flex-direction: column;
    align-content: stretch;
}

.keyboard-row {
    margin-top: 10rpx;
    flex-grow: 1;
    display: flex;
    flex-direction: row;
}

.dtmf-button {
    width: 25vw;
    height: 20vw;
    border-radius: 30vw;
    text-align: center;
    flex-grow: 1;
    flex-shrink: 1;
    margin-left: 10rpx;
    margin-right: 10rpx;
    background-color: #63636347;
    color: white;
    display: flex;
    flex-direction: row;
    align-items: center;
    justify-content: center;
}

.dtmf-text {
    text-align: center;
    font-size: 70rpx;
}

.control-area {
    width: 100%;
    height: fit-content;
    display: flex;
    flex-direction: row;
}

.control-button {
    height: 120rpx;
    flex-grow: 1;
    flex-shrink: 1;
    border-radius: 60rpx;
    margin: 20rpx;
    color: white;
    background-color: rgb(63 63 63 / 32%);
    text-align: center;
    line-height: 120rpx;
}

.control-button-img-area {
    display: flex;
    justify-content: center;
    align-items: center;
    flex-grow: 1;
}

.control-button-img {
    background-color: #8a8a8a40;
    border-radius: 100rpx;
    width: 100rpx;
    height: 100rpx;
    padding: 40rpx;
}

.bottom-area {
    width: inherit;
    height: 300rpx;
    flex-shrink: 0;
    display: flex;
    justify-content: center;
}

.hangupBtn {
    color: white;
    border-radius: 80rpx;
    background-color: rgb(213, 33, 33);
    height: 160rpx;
    width: 160rpx;
    line-height: 160rpx;
    text-align: center;
}

.hideDtmfBtn {
    position: fixed;
    right: 18%;
    bottom: 180rpx;
    color: white;
    height: 100rpx;
    line-height: 100rpx;
    width: 100rpx;
    text-align: center;
}

.player {
    position: fixed;
    width: 0%;
    height: 0%;
}

.pusher {
    position: fixed;
    width: 0%;
    height: 0%;
}

.enable {
    background-color: rgb(247 247 247 / 56%);
}
</style>