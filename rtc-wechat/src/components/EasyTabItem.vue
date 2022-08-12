<template>
    <view class="view-root">
        <view :class="{ 'tab-item-content': true, 'tab-item-selected': isSelected }">
            <text>{{ tabName }}</text>
        </view>
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
    height: 100%
}

.tab-item-content {
    width: 100%;
    height: 100%;
    text-align: center;
    vertical-align: center;
    color: black;
    font-size: 32rpx;
    display: flex;
    flex-direction: column;
    justify-content: center;
}

.tab-item-selected {
    color: darkgreen;
    border-bottom-color: darkgreen;
    border-bottom-style: solid;
}
</style>