<!-- eslint-disable vue/multi-word-component-names -->
<template>
	<view class="root-view">
		<view class="fill-login-message" @click="fillLoginMessage"></view>
		<image src="../../static/login/logo.png" mode="scaleToFill" class="logo" />
		<picker :range="envName" :value="currentEnvIndex" @change="onEnvPickerChange" class="picker">
			<view>{{ envName[currentEnvIndex] }}</view>
		</picker>
		<clearableInput class="input" prefixIcon="../../../static/login/icon-company.png" :currentText="enterpriseId"
			hintText="企业 ID" @onClear="onEnterpriseIdClear" @onChange="onEnterpriseIdChange" />
		<clearableInput class="input" prefixIcon="../../../static/login/icon-people.png" :currentText="username"
			hintText="用户 ID" @onClear="onUsernameClear" @onChange="onUsernameChange" />
		<clearableInput class="input" prefixIcon="../../../static/login/icon-psw.png" :currentText="password"
			hintText="输入密码" @onClear="onPasswordClear" @onChange="onPasswordChange" />
		<view class="login-button" @click="login">登录</view>
	</view>
</template>

<script setup>
import clearableInput from "../../components/clearableInput.vue"
</script>

<script>
import { AppModel, AppUiState } from "@/state/AppState"
import { BuildConfig } from '../../config/config'


export default {
	data() {
		return {
			currentEnvIndex: 0,
			envName: BuildConfig.APP_ENV_NAME,
			envUrl: BuildConfig.APP_ENV_URL,
			enterpriseId: "",
			username: "",
			password: "",
			appModel: null,
		}
	},
	mounted() {
		this.appModel = AppModel()

		this.appModel.$subscribe((mutation, state) => {
			switch (state.appUiState.stateName) {
				case AppUiState.LoginSuccess:
					this.loginSuccess()
					break
				case AppUiState.LoginFailed:
					this.loginFailed(state.appUiState.stateDetail.errorMessage)

			}
		})
	},
	onLoad() {

	},
	onReady() {
	},
	onShow() {

	},
	methods: {
		fillLoginMessage(){
			this.enterpriseId = BuildConfig.ENTERPRISE_ID
			this.username = BuildConfig.USERNAME
			this.password = BuildConfig.PASSWORD
		},
		login() {
			this.appModel.login(
				this.envUrl[this.currentEnvIndex],
				this.enterpriseId,
				this.username,
				this.password
			)
		},
		loginSuccess() {
			uni.switchTab({
				url: "/pages/index/index"
			})
		},
		loginFailed(errorMessage) {
			uni.showToast({
				title: `${errorMessage}`,
				icon: 'none',
				mask: true
			})
		},
		onEnterpriseIdClear() {
			this.enterpriseId = ""
		},
		onUsernameClear() {
			this.username = ""
		},
		onPasswordClear() {
			this.password = ""
		},
		onEnterpriseIdChange(value) {
			this.enterpriseId = value
		},
		onUsernameChange(value) {
			this.username = value
		},
		onPasswordChange(value) {
			this.password = value
		},
		onEnvPickerChange(event) {
			this.currentEnvIndex = event.detail.value
		}

	}
}
</script>

<style>
page {
	width: 100%;
	height: 100%;
}

.root-view {
	width: inherit;
	height: inherit;
	display: flex;
	flex-direction: column;
	align-items: center;

}

.fill-login-message{
	width:120rpx;
	height:120rpx;
	position: fixed;
	top:0%;
	left:0%;
	z-index: 3;
}

.logo {
	width: 120rpx;
	height: 120rpx;
	margin-top: 200rpx;
	margin-bottom: 80rpx;
}

.picker{
	width:calc(100% - 80rpx);
	position: relative;
	left: 64rpx;
}

.input {
	width: calc(100% - 80rpx);
	margin-top: 40rpx;
}


.login-button {
	width: calc(100% - 80rpx);
	height: 80rpx;
	background-color: rgb(44, 165, 48);
	text-align: center;
	line-height: 80rpx;
	color: white;
	margin-left: 40rpx;
	margin-right: 40rpx;
	margin-top: 60rpx;
}
</style>
