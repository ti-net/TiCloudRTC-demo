<!-- eslint-disable vue/multi-word-component-names -->
<template>
	<view class="root-view">
		<button :disabled="false" hover-class="button-hover" class="button" @click="push">
			推送
		</button>
		<button :disabled="false" hover-class="button-hover" class="button" @click="play">
			拉流
		</button>
		<live-player id="player" :src="rtmpPullUrl" mode="RTC" :sound-mode="soundMode" autoplay="false" class="player"
			@statechange="playerStateChange" @error="playerError" />

		<live-pusher id="pusher" :url="rtmpPushUrl" autopush="false" mode="RTC" :enable-mic="enableMic" :muted="false"
			:enable-camera="false" class="pusher" @statechange="pusherStateChange" @error="pusherError"
			@netstatus="netstatus" />


	</view>
</template>

<script>

export default {
	data() {
		return {


			/** 拉流地址 */
			rtmpPullUrl: "rtmp://rtc.slaman.cn:1935/live/1234mmm",
			/** 推流地址 */
			rtmpPushUrl: "",
			/** mic 标识, true 为打开 mic(非静音),false 为关闭 mic (静音) */
			enableMic: true,
			/** 音频输出方式,speaker: 扬声器, ear: 听筒 */
			soundMode: "speaker",
			pusherContext: null,
			playerContext: null,
		}
	},
	mounted() {


	},
	onLoad() {

	},
	onReady() {
		console.log("push url: ", this.rtmpPushUrl)
		this.pusherContext = uni.createLivePusherContext('pusher')
		console.log("pusher context:", JSON.stringify(this.pusherContext))

		console.log("pull url: ", this.rtmpPullUrl)
		this.playerContext = uni.createLivePlayerContext("player")
		console.log("player conetxt:", JSON.stringify(this.playerContext))



	},
	onShow() {

	},
	methods: {
		push() {
			this.rtmpPushUrl = "rtmp://rtc.slaman.cn:1935/live/1234mmm"

			console.log("点击了推流按钮")
			this.pusherContext.start({
				success(msg) {
					console.log("推流成功:", JSON.stringify(msg))
					this.enableMic = true
				},
				fail(err) { console.log("推流失败", JSON.stringify(err)) }
			})
		},
		play() {
			console.log("点击了拉流按钮")
			this.playerContext.play({
				success() { console.log("拉流成功") },
				fail() { console.log("拉流失败") }
			})
		},
		pusherStateChange(e) {
			console.log("pusher state change: ", JSON.stringify(e))
		},
		playerStateChange(e) {
			console.log("player state change: ", JSON.stringify(e))
		},
		playerError(e) {
			console.log("player error: ", JSON.stringify(e))
		},
		pusherError(e) {
			console.log("pusher error: ", JSON.stringify(e))
		},
		netstatus(e) {
			console.log("netstatus:" + JSON.stringify(e))
		},
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


.button {
	width: calc(100% - 80rpx);
	height: 120rpx;
	background-color: rgb(44, 165, 48);
	text-align: center;
	line-height: 120rpx;
	color: white;
	margin-left: 40rpx;
	margin-right: 40rpx;
	margin-top: 30rpx;
	margin-bottom: 60rpx;
}
</style>
