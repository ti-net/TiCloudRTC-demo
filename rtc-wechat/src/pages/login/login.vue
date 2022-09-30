<!-- eslint-disable vue/multi-word-component-names -->
<template>
	<view class="root-view">
		<view class="fill-login-message" @click="fillLoginMessage"></view>
		<image src="../../static/login/logo.png" mode="scaleToFill" class="logo" />
		<text class="text-ti-rtc">Ti-RTC</text>
		<text class="text-sub-title">让客户联络效率更高, 体验更好</text>
		<picker :range="envName" :value="currentEnvIndex" @change="onEnvPickerChange" class="picker">
			<view>{{  envName[currentEnvIndex]  }}</view>
		</picker>
		<clearableInput class="input input-enterprise-id" prefixIcon="../../../static/login/icon-company.png"
			:currentText="enterpriseId" hintText="企业 ID" @onClear="onEnterpriseIdClear"
			@onChange="onEnterpriseIdChange" />
		<clearableInput class="input" prefixIcon="../../../static/login/icon-people.png" :currentText="username"
			hintText="用户 ID" @onClear="onUsernameClear" @onChange="onUsernameChange" />
		<clearableInput class="input" prefixIcon="../../../static/login/icon-psw.png" :currentText="password"
			:isPassword="true" hintText="输入密码" @onClear="onPasswordClear" @onChange="onPasswordChange" />
		<view class="check-area">
			<image v-if="!devState" src="../../static/login/nocheck.png" mode="scaleToFill" class="check-img"
				@click="switchDevState" />
			<image v-else src="../../static/login/checked.png" mode="scaleToFill" class="check-img"
				@click="switchDevState" />
			<text class="check-text" @click="switchDevState">开发者模式</text>
		</view>
		<view :class="{ 'login-button': true, 'login-button-disable': !isInputLoginMessage() }" @click="login">登录</view>
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
			devState: false, // 开发者模式开关, true: 开发者模式, false 演示模式
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
		isInputLoginMessage() {
			return this.enterpriseId != '' && this.username != '' && this.password != ''
		},
		switchDevState() {
			this.devState = !this.devState
		},
		fillLoginMessage() {
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
			if (this.devState) {
				uni.switchTab({
					url: "/pages/index/index"
				})
			} else {
				uni.reLaunch({ url: '/pages/CallScene/CallScene' })
			}
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

.fill-login-message {
	width: 120rpx;
	height: 120rpx;
	position: fixed;
	top: 0%;
	left: 0%;
	z-index: 3;
}

.logo {
	width: 128rpx;
	height: 128rpx;
	margin-top: 236rpx;
	border-radius: 32rpx;
	box-shadow: 0rpx 0rpx 64rpx 5rpx #1f376f2c;
}

.text-ti-rtc {
	font-size: 48rpx;
	font-family: PingFang SC-Medium, PingFang SC;
	font-weight: 500;
	color: #595959;
	line-height: 56rpx;
	margin-top: 24rpx;
}

.text-sub-title {
	font-size: 24rpx;
	font-family: PingFang SC-Regular, PingFang SC;
	font-weight: 400;
	color: #8C8C8C;
	line-height: 36rpx;
}

.picker {
	width: calc(100% - 80rpx);
	position: relative;
	left: 64rpx;
	display: none;
}

.input {
	width: calc(100% - 128rpx);
	margin-top: 48rpx;
}

.input-enterprise-id {
	margin-top: 98rpx;
}

.check-area {
	display: flex;
	align-items: center;
	justify-content: flex-start;
	width: calc(100% - 128rpx);
	margin-top: 24rpx;
}

.check-img {
	width: 28rpx;
	height: 28rpx;
}

.check-text {
	font-size: 24rpx;
	font-family: PingFang SC-Regular, PingFang SC;
	font-weight: 400;
	color: #262626;
	line-height: 36rpx;
	margin-left: 8rpx;
}

.login-button {
	width: 622rpx;
	height: 96rpx;
	background-color: #4385FF;
	text-align: center;
	line-height: 96rpx;
	color: white;
	border-radius: 16rpx;
	margin-left: 64rpx;
	margin-right: 64rpx;
	margin-top: 48rpx;
}

.login-button-disable {
	background-color: #BDD5FF;
}
</style>
