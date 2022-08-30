<template>
    <view class="root-view">
        <view class="call-area-1">
            <view class="call-num-area"><text class="call-num">{{ 95092 }}</text></view>
            <view class="btn-call-area-1">
                <view class="btn-call-1" @tap="call('')">联系客服</view>
            </view>
        </view>
        <view class="call-area-2">
            <view class="node-area">
                <view class="node-area-top">
                    <view :class="{ 'node': true, 'root-node': true, 'node-selected': currentSelectedNodeIndex == 0 }"
                        @click="selectNode(0)">根节点</view>
                    <view class="root-ver-line-layer">
                        <view class="ver-line "></view>
                    </view>
                </view>
                <view class="node-area-bottom">
                    <view class="sec-node-area">
                        <view
                            :class="{ 'node': true, 'sec-node': true, 'node-selected': currentSelectedNodeIndex == 1 }"
                            @click="selectNode(1)">直呼节点</view>
                        <view class="sub-ver-line-layer">
                            <view class="ver-line "></view>
                        </view>
                        <view class="sub-hor-line-layer">
                            <view class="hor-line hor-line-1"></view>
                        </view>
                    </view>
                    <view class="sec-node-area">
                        <view
                            :class="{ 'node': true, 'sec-node': true, 'node-selected': currentSelectedNodeIndex == 2 }"
                            @click="selectNode(2)">播放节点</view>
                        <view class="sub-ver-line-layer">
                            <view class="ver-line "></view>
                        </view>
                        <view class="sub-hor-line-layer">
                            <view class="hor-line hor-line-2"></view>
                        </view>
                    </view>
                    <view class="sec-node-area">
                        <view
                            :class="{ 'node': true, 'sec-node': true, 'node-selected': currentSelectedNodeIndex == 3 }"
                            @click="selectNode(3)">队列节点</view>
                        <view class="sub-ver-line-layer">
                            <view class="ver-line "></view>
                        </view>
                        <view class="sub-hor-line-layer">
                            <view class="hor-line hor-line-3"></view>
                        </view>
                    </view>
                </view>
            </view>
            <textarea class="textarea" :value="userField" :maxlength="-1" placeholder="随路数据(可选)"
                @input="onUserFieldChange" />
            <view class="btn-call-2" @tap="call(userField)">联系客服</view>
        </view>


    </view>
</template>
<script>
import { BuildConfig } from '../../config/config'
import { AppModel, CallScene } from '../../state/AppState'

export default {
    name: "AgentScene",
    data() {
        return {
            currentSelectedNodeIndex: 0,
            appModel: null,
            userField: ""
        }
    },
    mounted() {
        this.appModel = AppModel()
    },
    methods: {
        selectNode(index) {
            this.currentSelectedNodeIndex = index
            switch (index) {
                case 0:
                    this.userField = BuildConfig.ROOT_NODE_USER_FIELD
                    break
                case 1:
                    this.userField = BuildConfig.NODE_1_USER_FIELD
                    break
                case 2:
                    this.userField = BuildConfig.NODE_2_USER_FIELD
                    break
                case 3:
                    this.userField = BuildConfig.NODE_3_USER_FIELD
            }
        },
        call(userField) {
            this.appModel.call("", "", "", userField, CallScene.AgentScene)
        },
        onUserFieldChange(event) {
            this.userField = event.detail.value
        }
    }
}
</script>


<style scoped>
.root-view {
    width: 100%;
    height: 100%;
    display: flex;
    flex-direction: column;
    align-items: center;
}

.call-area-1 {
    width: 100%;
    height: 200rpx;
    display: flex;
    flex-direction: row;
    flex-shrink: 0;
    border-bottom-color: darkolivegreen;
    border-bottom-style: solid;
    border-bottom-width: 2rpx;
}

.call-num-area {
    flex-grow: 1;
    height: 100%;
    display: flex;
    align-items: center;
}

.call-num {
    flex-grow: 1;
    text-align: center;
    font-size: 48rpx;
    font-style: oblique;
}

.btn-call-area-1 {
    flex-grow: 1;
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
}

.btn-call-1 {
    width: 60%;
    text-align: center;
    height: 80rpx;
    line-height: 80rpx;
    color: white;
    background-color: darkgreen;
}

.call-area-2 {
    width: 100%;
    flex-grow: 1;
    flex-shrink: 1;
    display: flex;
    flex-direction: column;
    align-items: center;
}

.node-area {
    width: inherit;
    height: 360rpx;
    display: flex;
    flex-direction: column;
    align-items: center;
}

.node-area-top {
    flex-grow: 1;
    width: inherit;
    display: flex;
    align-items: center;
    justify-content: center;
}

.node-area-bottom {
    flex-grow: 1;
    width: 100%;
    display: flex;
    align-items: center;
}

.sec-node-area {
    height: 100%;
    flex-grow: 1;
    display: flex;
    justify-content: center;
    align-items: center;
}

.node {
    background-color: grey;
    color: white;
    height: 70rpx;
    line-height: 70rpx;
    width: 180rpx;
    border-radius: 16rpx;
    text-align: center;
    z-index: 10;
}

.node-selected {
    background-color: deepskyblue;
}

.sec-node {}

.root-ver-line-layer {
    position: absolute;
    height: calc(360rpx / 2);
    width: 100%;
    display: flex;
    flex-direction: column-reverse;
    align-items: center;
    z-index: 1;
}

.sub-ver-line-layer {
    position: absolute;
    height: calc(360rpx / 2);
    width: calc(100% / 3);
    display: flex;
    flex-direction: column;
    align-items: center;
    z-index: 1;
}



.ver-line {
    width: 2rpx;
    height: 50%;
    background-color: black;
    position: absolute;
}

.sub-hor-line-layer {
    position: absolute;
    height: calc(360rpx / 2);
    width: calc(100% / 3);
    display: flex;
    flex-direction: column;
    z-index: 0;
}

.hor-line {
    height: 2rpx;
    background-color: black;
}

.hor-line-1 {
    width: 50%;
    align-self: flex-end;
}

.hor-line-2 {}

.hor-line-3 {
    width: 50%;
    align-self: flex-start;
}

.textarea {
    width: calc(100% - 120rpx);
    margin-top: 60rpx;
    margin-bottom: 60rpx;
    flex-grow: 1;
    flex-shrink: 1;
}

.btn-call-2 {
    width: calc(100% - 80rpx);
    height: 80rpx;
    color: white;
    background-color: darkgreen;
    text-align: center;
    line-height: 80rpx;
    margin-bottom: 160rpx;
    bottom: 0;
}
</style>