<template>
    <view class="root-view">
        <image class="root-view-bg" src="../../static/DemoCalling/bg.png" mode="scaleToFill" />
        <live-player id="player" :src="rtmpPullUrl" mode="RTC" :sound-mode="soundMode" autoplay="false" class="player"
            @statechange="playerStateChange" @error="playerError" @netstatus="netstatus" />
        <live-pusher id="pusher" :url="rtmpPushUrl" mode="RTC" :enable-mic="enableMic" :enable-camera="false"
            autopush="false" class="pusher" @statechange="pusherStateChange" @error="pusherError" @netstatus="netstatus"
            waiting-image="../../static/login/logo.png" />
        <view v-if="!isShowDtmfPanel" class="top-area">
            <view class="current-phone-num">{{  currentPheneNum  }}</view>
            <view class="small-text">{{  smallText  }}</view>
        </view>
        <view v-else class="top-area">
            <view class="current-dtmf-num">{{  currentDtmf  }}</view>
        </view>
        <view :class="{ 'center-area': true, 'center-area-showing-bottom': isShowDtmfPanel }">
            <view class="center-fillter"></view>
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
                    <image class="control-button-img" src="../../static/DemoCalling/ic-kb-disable.png"
                        @click="switchDtmfShowState" />
                    <text class="control-button-text">拨号键盘</text>
                </view>
                <view class="control-button-img-area">
                    <image v-if="enableMic" class="control-button-img" src="../../static/DemoCalling/ic-mic-enable.png"
                        @click="switchMuteState" />
                    <image v-else class="control-button-img" src="../../static/DemoCalling/ic-mic-disable.png"
                        @click="switchMuteState" />
                    <text class="control-button-text">静音</text>
                </view>
                <view class="control-button-img-area">
                    <image v-if="soundMode == 'speaker'" class="control-button-img"
                        src="../../static/DemoCalling/ic-speaker-enable.png" @click="switchSoundMode" />
                    <image v-else class="control-button-img" src="../../static/DemoCalling/ic-speaker-disable.png"
                        @click="switchSoundMode" />
                    <text class="control-button-text">扬声器</text>
                </view>
            </view>
        </view>
        <view class="bottom-area">
            <image src="../../static/DemoCalling/icon-hangup.png" mode="scaleToFill" class="hangupBtn"
                @click="hangup" />
            <view v-if="isShowDtmfPanel" class="hideDtmfBtn" @click="switchDtmfShowState"><text
                    class="hideDtmfBtnText">隐藏</text></view>
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
            /** 当前已输入的 dtmf */
            currentDtmf: "",
            /** 当前通话时间 */
            smallText: "呼叫中...",
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
        try {
            console.log(`options: ${JSON.stringify(options)}`)
            this.currentPheneNum = options.phoneNumber ? options.phoneNumber : ""
        } catch (err) { console.log(err) }

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
                        uni.navigateBack()
                        
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
            this.currentDtmf = this.currentDtmf + digits
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

<style>
page {
    width: 100%;
    height: 100%;
}
</style>

<style scoped>
.root-view {
    width: 100%;
    height: inherit;
    display: flex;
    flex-direction: column;
    align-items: center;
}

.root-view-bg {
    width: 100%;
    height: 100%;
    z-index: -2;
    position: fixed;
}

.top-area {
    margin-top: 268rpx;
    flex-shrink: 0;
}

.current-phone-num {
    width: 100%;
    height: 64rpx;
    font-size: 72rpx;
    font-family: PingFang SC-Regular, PingFang SC;
    font-weight: 400;
    color: #FFFFFF;
    line-height: 64rpx;
    text-align: center;

}

.current-dtmf-num {
    width: 100%;
    height: 64rpx;
    font-size: 72rpx;
    font-family: PingFang SC-Regular, PingFang SC;
    font-weight: 400;
    color: #FFFFFF;
    line-height: 64rpx;
    text-align: center;

}

.small-text {
    width: 100%;
    height: 56rpx;
    font-size: 40rpx;
    font-family: PingFang SC-Regular, PingFang SC;
    font-weight: 400;
    color: rgba(255, 255, 255, 0.65);
    line-height: 56rpx;
    margin-top: 28rpx;
    text-align: center;
}

.center-area {
    width: 100%;
    flex-grow: 1;
    flex-shrink: 2;
    display: flex;
    flex-direction: column;
    justify-content: flex-start;
    align-items: center;
    margin-bottom: 80rpx;
}

.center-fillter{
    flex-grow: 1;
    width: 100%;
    flex-shrink: 1;
}

.center-area-showing-bottom {
    margin-bottom: 40rpx;
}

.dtmf-keyboard-area {
    width: calc(100% - 200rpx);
    height: 696rpx;
    display: flex;
    flex-direction: column;
    justify-content: space-between;
}

.keyboard-row {
    margin-top: 40rpx;
    display: flex;
    flex-direction: row;
    height: fit-content;
    justify-content: space-between;
}

.keyboard-row:first-child {
    margin-top: 0rpx;
}

.dtmf-button {
    width: 144rpx;
    height: 144rpx;
    border-radius: 72rpx;
    text-align: center;
    flex-shrink: 1;
    background-color: rgba(255, 255, 255, 0.2500);
    display: flex;
    flex-direction: row;
    align-items: center;
    justify-content: center;
}

.keyboard-row:last-child .dtmf-button:nth-last-child(3) .dtmf-text {
    margin-top: 16rpx;
}

.dtmf-button:active {
    background-color: rgba(255, 255, 255, 0.6500);
}

.dtmf-text {
    text-align: center;
    height: 64rpx;
    font-size: 72rpx;
    font-family: PingFang SC-Regular, PingFang SC;
    font-weight: 400;
    color: #FFFFFF;
    line-height: 64rpx;
}

.control-area {
    width: calc(100% - 200rpx);
    height: fit-content;
    display: flex;
    flex-direction: row;
    justify-content: space-between;
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
    flex-direction: column;
    justify-content: center;
    align-items: center;
    height: fit-content;
}

.control-button-img {
    width: 144rpx;
    height: 144rpx;
}

.control-button-text {
    height: 48rpx;
    font-size: 32rpx;
    font-family: PingFang SC-Regular, PingFang SC;
    font-weight: 400;
    color: rgba(255, 255, 255, 0.65);
    line-height: 48rpx;
    margin-top: 16rpx;
}

.bottom-area {
    width: inherit;
    height: fit-content;
    flex-shrink: 0;
    display: flex;
    justify-content: center;
    align-items: center;
    margin-bottom: 154rpx;
}

.hangupBtn {
    height: 144rpx;
    width: 144rpx;
}

.hideDtmfBtn {
    position: absolute;
    right: 17.6%;
    height: 100rpx;
    width: 100rpx;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
}

.hideDtmfBtnText {
    font-size: 40rpx;
    font-family: PingFang SC-Regular, PingFang SC;
    font-weight: 400;
    color: rgba(255, 255, 255, 0.85);
    line-height: 56rpx;
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