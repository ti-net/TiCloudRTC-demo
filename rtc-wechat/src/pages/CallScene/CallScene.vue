<template>
    <view class="root-view">
        <view class="tab-area">
            <DemoTab :tabs="tabs" class="tab" />
        </view>
        <view class="no-input-call-item">
            <view class="no-input-call-item-up-area">
                <text class="left-dsc">VIP 客户</text>
                <image src="../../static/CallScene/call.png" mode="scaleToFill" class="right-call-img" />
            </view>
            <view class="no-input-call-item-bottom-line" />
        </view>
        <view class="no-input-call-item">
            <view class="no-input-call-item-up-area">
                <text class="left-dsc">有意向客户</text>
                <image src="../../static/CallScene/call.png" mode="scaleToFill" class="right-call-img" />
            </view>
            <view class="no-input-call-item-bottom-line" />
        </view>

        <view class="no-input-call-item">
            <view class="no-input-call-item-up-area">
                <input class="left-dsc" placeholder-class="left-phone-number-input-placeholder" placeholder="请输入客户号码"
                    :value="phoneNumber" @change="phoneNumberChange" />
                <image src="../../static/CallScene/call.png" mode="scaleToFill" class="right-call-img" @click="call"/>
            </view>
            <view class="no-input-call-item-bottom-line" />
        </view>
        <view v-if="(phoneNumber != '') && (!isPhoneNumRight)" class="alert-text-area">
            <text>号码格式不正确</text>
        </view>

    </view>
</template>

<script setup>
import { DemoTab } from "../../components/DemoTab.vue"

</script>

<script>
export default {
    data() {
        return {
            tabs: ["客户资料"],
            phoneNumber: "",
            isPhoneNumRight:false,
        }
    },
    methods: {
        showErrorMsg(errorMessage) {
            uni.showToast({
                title: `${errorMessage}`,
                icon: 'none',
                mask: true
            })
        },
        phoneNumberChange(event) {
            this.phoneNumber = event.detail.value
            this.isPhoneNumRight = /^1(3\d|4[5-9]|5[0-35-9]|6[567]|7[0-8]|8\d|9[0-35-9])\d{8}$/.test(this.phoneNumber)
        },
        call(){
            if(this.isPhoneNumRight){
                uni.navigateTo({
                    url:`/pages/DialPanel/DialPanel?phoneNumber=${this.phoneNumber}`
                })
            }
        }
    }
}
</script>


<style >
page {
    width: 100%;
    height: 100%;
}

.left-phone-number-input-placeholder {
    font-size: 30rpx;
    font-family: PingFang SC-Regular, PingFang SC;
    font-weight: 400;
    color: #BFBFBF;
    line-height: 36rpx;
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

.tab-area {
    margin-top: 180rpx;
    display: flex;
    width: calc(100% - 128rpx);
    justify-content: flex-start;
}


.no-input-call-item {
    width: calc(100% - 128rpx);
    display: flex;
    flex-direction: column;
}

.no-input-call-item:nth-child(2) {
    margin-top: 100rpx;
}

.no-input-call-item-up-area {
    width: 100%;
    display: flex;
    justify-content: space-between;
    margin-top: 48rpx;
}



.left-dsc {
    font-size: 30rpx;
    font-family: PingFang SC-Regular, PingFang SC;
    font-weight: 400;
    color: #262626;
    line-height: 36rpx;
    flex-grow: 1;
    flex-shrink: 1;
    margin-right: 40rpx;
}

.right-call-img {
    width: 56rpx;
    height: 56rpx;
}


.no-input-call-item-bottom-line {
    width: 100%;
    height: 2rpx;
    background-color: #000000;
    opacity: 0.06;
}



.alert-text-area {
    font-size: 24rpx;
    font-family: PingFang SC-Regular, PingFang SC;
    font-weight: 400;
    color: #FF4D4F;
    line-height: 36rpx;
    margin-top: 16rpx;
    width: calc(100% - 128rpx);
    align-self: flex-start;
    margin-left: 64rpx;
}

</style>