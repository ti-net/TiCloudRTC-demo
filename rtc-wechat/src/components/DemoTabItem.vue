<template>
    <view class="view-root">
        <view :class="{ 'tab-item-content': true, 'tab-item-selected': isSelected }">
            <text>{{  tabName  }}</text>
        </view>
        <view :class="{ 'tab-item-bottom-line': true, 'tab-item-bottom-line-selected': isSelected }" />
    </view>
</template>

<script>
export default {
    name: "EasyTabItem",
    props: {
        isSelected: {
            require: true,
            type: Boolean,
            default: false
        },
        tabName: {
            require: true,
            typs: String,
            default: ""
        }
    },
}
</script>

<style scoped>
.view-root {
    width: 100%;
    height: 100%;

    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
}

.tab-item-content {
    width: 100%;
    /* height: 100%; */
    text-align: center;
    vertical-align: center;
    font-size: 40rpx;
    display: flex;
    font-family: PingFang SC-Medium, PingFang SC;
    flex-direction: column;
    justify-content: center;
}

.tab-item-selected {
    font-weight: 500;
    color: #262626;
    /* border-bottom-color: darkgreen;
    border-bottom-style: solid; */
}

.tab-item-bottom-line {
    width: 100%;
    height: 10rpx;
    border-radius: 16rpx;
    margin-top: 10rpx;
    opacity: 0;
}

.tab-item-bottom-line-selected {
    background-color: #4385FF;
    opacity: 1;
}
</style>