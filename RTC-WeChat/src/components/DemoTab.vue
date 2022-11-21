<template>
    <view class="root-view">
        <DemoTabItem class="tab-item-class" v-for="(item, index) in tabs" :key="index" :tabName="item" :isSelected="currentSelected == index" @tap="onTabItemClick(index)"/>
    </view>
</template>

<script setup>
import { DemoTabItem } from "./DemoTabItem.vue"
</script>

<script>
const EasyTabEvent = {
    TabClicked:"tabClicked"
}
export default {
    name: "EasyTab",
    emits:[EasyTabEvent.TabClicked],
    props: {
        tabs: {
            require: true,
            type: Array,
            default: new Array()
        }
    },
    data() {
        return {
            /** 当前选择的 tab 的索引 */
            currentSelected:0
        }
    },
    methods: {
        onTabItemClick(index){
            this.currentSelected = index
            this.$emit(EasyTabEvent.TabClicked,index)
        }
    }

}
</script>

<style scoped>
.root-view {
    width: 100%;
    height: 76rpx;
    display: flex;
    flex-direction: row;
    flex-wrap: nowrap;
}

.tab-item-class{
    flex-grow: 1;
}
</style>