<!-- eslint-disable vue/multi-word-component-names -->
<template>
    <view class="root-view">
        <EasyTab :tabs="tabs" @tabClicked="tabClicked" class="easy-tab" />
        <CallerScene class="scene" v-if="currentSubPageIndex == Tab.CallerScene" />
        <AgentScene class="scene" v-if="currentSubPageIndex == Tab.AgentScene" />
    </view>
</template>

<script setup>
import { CallerScene } from "./CallerScene.vue"
import { AgentScene } from "./AgentScene.vue"
import { EasyTab } from "../../components/EasyTab.vue"

</script>


<script>
// import { TiCloudRTC } from "../../libs/ticloudrtc-wechat-sdk-1.0.0"
import { AppModel, AppUiState } from "@/state/AppState"

const Tab = {
    CallerScene: 0,  // 外呼场景
    AgentScene: 1    // 客服场景
}

export default {
    data() {
        return {
            tabs: ["外呼场景", "客服场景"],
            appModel: null,
            currentSubPageIndex: Tab.CallerScene
        }
    },
    mounted() {
        // 监听引擎事件
        this.appModel = AppModel()
        console.log("index rtc client: ", this.appModel.rtcClient)

        this.appModel.$subscribe((mutation, state) => {
            switch (state.appUiState.stateName) {
                case AppUiState.OnInnerSdkError:
                    {
                        let stateDetail = state.appUiState.stateDetail
                        let errorMessage = `SDK 内部错误. code: ${stateDetail.errorCode}, message: ${stateDetail.errorMessage}`
                        this.showErrorMsg(errorMessage)
                    }
                    break
                case AppUiState.OnCallStart:
                    this.onCallStart()
                    break
                case AppUiState.OnCallFailure:
                case AppUiState.OnRefreshTokenFailed:
                    this.showErrorMsg(state.appUiState.stateDetail.errorMessage)
                    break
                case AppUiState.OnAccessTokenHasExpired:
                    this.showErrorMsg(state.appUiState.stateDetail.errorMessage)
                    uni.reLaunch({ url: "/pages/login/login" })
                    break

            }
        })
    },
    methods: {
        onCallStart() {
            uni.reLaunch({
                url: "/pages/calling/calling"
            })
        },
        tabClicked(index) {
            this.currentSubPageIndex = index
        },
        showErrorMsg(errorMessage) {
            uni.showToast({
                title: `${errorMessage}`,
                icon: 'none',
                mask: true
            })
        }
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
    width: inherit;
    height: inherit;
    display: flex;
    flex-direction: column;
    background-color: ghostwhite;
}

.easy-tab {
    background-color: white
}

.scene {
    width: inherit;
    flex-grow: 1;
}

.out-call-button {
    margin-top: 80rpx;
    width: inherit;
    height: 80rpx;
    color: white;
    background-color: rgb(44, 165, 48);
    text-align: center;
    line-height: 80rpx;
}
</style>