<template>
    <view class="root-view">
        <clearableInput class="input" :currentText="tel" hintText="客户号码" @onClear="onTelClear" @onChange="onTelChange" prefixIcon=""/>
        <clearableInput class="input"  :currentText="clid" hintText="外显号码(可选)" @onClear="onClidClear" @onChange="onClidChange" prefixIcon=""/>
        <textarea
            class="input  textarea"
            :value="userField"
            :maxlength="-1"
            placeholder="随路数据(可选)"
            @input="onUserFieldChange"
        />
        <view class="btn-call" @click="call">呼叫</view>
    </view>
</template>

<script setup>
import clearableInput from "../../components/clearableInput.vue"
</script>

<script>
import { AppModel,CallScene } from '../../state/AppState';


export default {
    name: "CallerScene",
    data() {
        return {
            appModel: null,
            tel:"",
            clid:"",
            userField:"",
        };
    },
    mounted() {
        this.appModel = AppModel();
        // this.appModel.$subscribe((mutation, state) => {
        // });
    },
    methods: {
        onUserFieldChange(event){
            this.userField = event.detail.value
        },
        onTelClear(){
            this.tel = ""
        },
        onClidClear(){
            this.clid = ""
        },
        onTelChange(value){
            this.tel = value
        },
        onClidChange(value){
            this.clid = value
        },
        call(){
            this.appModel.call(
                this.tel,
                this.clid,
                "",
                this.userField,
                CallScene.CallScene
            )
        }
    },
}
</script>

<style  scoped>

.root-view{
    width:100%;
    height:100%;
    background-color: transparent;
    display: flex;
    flex-direction: column;
    align-items: center;
    padding-top: 60rpx;
}

.input{
    margin-top:60rpx;
    width:calc(100% - 80rpx);
}

.textarea{
    width:calc(100% - 120rpx);
    margin-top:60rpx;
    margin-bottom: 60rpx;
    flex-grow: 1;
    flex-shrink: 1;
}

.btn-call {
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
