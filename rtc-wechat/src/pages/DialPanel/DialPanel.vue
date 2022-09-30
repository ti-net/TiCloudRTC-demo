<template>
    <view class="root-view">
        <view class="phone-num-area">
            <text class="phone-num">{{  phoneNumber  }}</text>
        </view>
        <view class="keyboard-area">
            <view class="keyboard-row">
                <view class="keyboard-btn" @click="addNum(1)"><text class="keyboard-btn-text" >1</text></view>
                <view class="keyboard-btn" @click="addNum(2)"><text class="keyboard-btn-text" >2</text></view>
                <view class="keyboard-btn" @click="addNum(3)"><text class="keyboard-btn-text">3</text></view>
            </view>
            <view class="keyboard-row">
                <view class="keyboard-btn" @click="addNum(4)"><text class="keyboard-btn-text">4</text></view>
                <view class="keyboard-btn" @click="addNum(5)"><text class="keyboard-btn-text">5</text></view>
                <view class="keyboard-btn" @click="addNum(6)"><text class="keyboard-btn-text">6</text></view>
            </view>
            <view class="keyboard-row">
                <view class="keyboard-btn" @click="addNum(7)"><text class="keyboard-btn-text">7</text></view>
                <view class="keyboard-btn" @click="addNum(8)"><text class="keyboard-btn-text">8</text></view>
                <view class="keyboard-btn" @click="addNum(9)"><text class="keyboard-btn-text">9</text></view>
            </view>
            <view class="keyboard-row">
                <view class="keyboard-btn"><text class="keyboard-btn-text keyboard-btn-ats">*</text></view>
                <view class="keyboard-btn" @click="addNum(0)"><text class="keyboard-btn-text">0</text></view>
                <view class="keyboard-btn"><text class="keyboard-btn-text">#</text></view>
            </view>

        </view>
        <view class="control-area">
            <image src="../../static/DialPanel/call-green.png" mode="scaleToFill" class="btn-call" @click="call()"/>
            <image src="../../static/DialPanel/delete-phone-num.png" mode="scaleToFill" class="btn-delete" @click="deleteNum"/>
        </view>
    </view>
</template>

<script setup>

</script>

<script>
import { AppModel, AppUiState ,CallScene} from "../../state/AppState"
export default {
    data() {
        return {
            phoneNumber: "",
            appModel: null,

        }
    },
    mounted() {
        let that = this
        this.appModel = AppModel()

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
        }
        )
    },
    onLoad(options) {
        try {
            console.log(`options: ${JSON.stringify(options)}`)
            this.phoneNumber = options.phoneNumber ? options.phoneNumber : ""
        } catch (err) { console.log(err) }

    },
    methods: {
        onCallStart() {
            uni.navigateTo({
                url:`/pages/DemoCalling/DemoCalling?phoneNumber=${this.phoneNumber}`
            })
        },
        showErrorMsg(errorMessage) {
            uni.showToast({
                title: `${errorMessage}`,
                icon: 'none',
                mask: true
            })
        },
        deleteNum(){
            if(this.phoneNumber != ""){
                this.phoneNumber = this.phoneNumber.substring(0,this.phoneNumber.length - 1)
            }
        },
        addNum(value){
            this.phoneNumber += value
        },
        call(){
            this.appModel.call(
                this.phoneNumber,
                "",
                "",
                "",
                CallScene.CallScene
            )
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
    width: 100%;
    height: 100%;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: flex-start;

}

.phone-num-area {
    margin-top: 180rpx;
    height: 64rpx;
}

.phone-num {
    font-size: 72rpx;
    font-family: PingFang SC-Regular, PingFang SC;
    font-weight: 400;
    color: #262626;
    line-height: 64px;
}

.keyboard-area {
    width: calc(100% - 200rpx);
    margin-top: 258rpx;
}

.keyboard-row {
    width: 100%;
    height: 144rpx;
    display: flex;
    flex-direction: row;
    justify-content: space-between;
    margin-top: 40rpx;

}

.keyboard-row:first-child{
    margin-top: 0rpx;
}

.keyboard-btn {
    width: 144rpx;
    height: 144rpx;
    background-color: #F0F0F0;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    border-radius: 72rpx;
}

.keyboard-btn:active {
    background-color: #D9D9D9;
}

.keyboard-btn-ats{
    margin-top: 16rpx;
}

.keyboard-btn-text {
    font-size: 72rpx;
    font-family: PingFang SC-Regular, PingFang SC;
    font-weight: 400;
    color: #262626;
    line-height: 64rpx;
}

.control-area {
    height: 144rpx;
    width: calc(100% - 128rpx);
    display: flex;
    justify-content: center;
    align-items: center;
    margin-top: 40rpx;
}

.btn-call {
    width: 144rpx;
    height: 144rpx;
}

.btn-delete {
    width: 84rpx;
    height: 84rpx;
    position: absolute;
    right: 17.33%;
}
</style>>
