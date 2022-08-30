<!-- eslint-disable vue/multi-word-component-names -->
<template>
    <view class="root-view">
        <view class="msgItem app-version">应用版本:&nbsp;{{ appVersion }}</view>
        <view class="msgItem sdk-version">SDK 版本:&nbsp;{{ sdkVersion }}</view>
        <view class="logout-button" @click="logout">退出登录</view>
    </view>

</template>

<script>
import { AppConfig } from "../../config/config"
import { TiCloudRTC } from "../../libs/ticloudrtc-wechat-sdk-1.0.0"
import { AppModel, AppUiState } from "../../state/AppState"

export default {
    data: function () {
        return {
            appVersion: "",
            sdkVersion: "",
            appModel: null
        }
    },
    onShow() {
        this.appVersion = AppConfig.VERSION_NAME
        this.sdkVersion = TiCloudRTC.getVersion()

        this.appModel = AppModel()
        this.appModel.$subscribe((mutation, state) => {
            switch (state.appUiState.stateName) {
                case AppUiState.LogoutSuccess:
                    this.logoutSuccess()
                    break
                case AppUiState.LogoutFailed:
                    this.logoutFailed(state.appUiState.stateDetail.errorMsg)
            }
        })
    },
    methods: {
        logout() {
            this.appModel.logout()
        },
        logoutSuccess() {
            uni.reLaunch({ url: '/pages/login/login' })
        },
        logoutFailed(errorMsg) {
            uni.showToast({
                title: `${errorMsg}`,
                icon: 'none',
                mask: true
            })
        }
    }
}
</script>
<style>
page{
    width: 100%;
    height:100%;
}
</style>
<style scoped>

.root-view {
    width: inherit;
    height: inherit;
    display: flex;
    flex-direction: column;
    align-items: center;
    background-color:ghostwhite;
}

.msgItem{
    width: calc(100% - 80rpx);
    height: 80rpx;
    line-height: 80rpx;
    padding:0 40rpx 0rpx 40rpx;
    background-color: white;
    margin-top:40rpx;
}

.logout-button {
    width: calc(100% - 80rpx);
    height: 80rpx;
    color: white;
    background-color: rgb(44, 165, 48);
    text-align: center;
    line-height: 80rpx;
    position:fixed;
    margin-bottom: 120rpx;
    bottom: 0;
}
</style>