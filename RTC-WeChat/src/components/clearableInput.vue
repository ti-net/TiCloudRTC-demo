<template>
	<view class="rootView">
		<view class="inputArea">
			<!-- 前缀图标 -->
			<image class="prefixIcon" v-if="prefixIcon != ''" :src="prefixIcon"></image>
			<input :class="inputStyle" :adjust-position="adjust_position" :confirm-hold="confirm_hold"
				:hold-keyboard="hold_keyboard" :confirm-type="confirm_type" :auto-blur="auto_blur" :focus="focus"
				:maxlength="maxlength" :disabled="disabled" :placeholder="hintText" :value="handledText"
				:placeholder-class="placeholder_class" :password="judgeShowPassword()" :type="type"
				@input="onChange" @confirm="onConfirm" @focus="onFocus" @blur="onBlur"/>
			<!-- 清除图标 -->
			<image class="btnClear" v-show="judgeShowClearIcon" src="../static/login/close.png" @click="onClear">
			</image>
			<!-- 查看密码图标 -->
			<image class="switchShowPasswordIcon" v-if="isPassword" :src="showPasswordSwitchIcon"
				@click="switchShowPassword()"></image>
			<!-- 后缀文字 -->
			<text class="suffixText">{{ suffixText }}</text>
		</view>
		<!-- 下划线 -->
		<view v-if="underline" class="underline"></view>
	</view>
</template>

<style scoped>
	.rootView {
		display: flex;
		flex-direction: column;
		max-width: 100%;
	}

	.underline {
		margin-top: 22rpx;
		border: 1rpx solid #F0F0F0;
		align-self: stretch;
	}

	.inputArea {
		display: flex;
		flex-direction: row;
		align-items: center;
		height: 44rpx;
	}

	>>> .prefixIcon {
		width: 44rpx;
		height: 44rpx;
		flex-shrink: 0;
	}

	>>> .uni-input {
		margin-left: 20rpx !important;
		margin-right: 20rpx !important;
		width: 100% !important;
		font-family: 'PingFang SC' !important;
		font-style: normal !important;
		font-weight: 400 !important;
		font-size: 32rpx !important;
		line-height: 44rpx !important;
		color: #262626 !important;
		flex-shrink: 1 !important;
	}

	::v-deep .uni-input-placeholder {
		width: 100% !important;
		font-family: 'PingFang SC' !important;
		font-style: normal !important;
		font-weight: 400 !important;
		font-size: 32rpx !important;
		line-height: 44rpx !important;
		color: #8C8C8C !important;
		flex-shrink: 1 !important;
	}

	.btnClear {
		width: 32rpx;
		height: 32rpx;
		padding: 10rpx;
		flex-shrink: 0;
	}

	.suffixText {
		font-family: 'PingFang SC';
		font-style: normal;
		font-weight: 500;
		font-size: 28rpx;
		line-height: 40rpx;
		color: #4385FF;
		flex-shrink: 0;
	}

	.switchShowPasswordIcon {
		width: 48rpx;
		height: 48rpx;
		flex-shrink: 0;
	}
</style>

<script>
	export default {
		name: "clearableInput",
		data() {
			return {
				/** 标识是否显示密码,true 为显示密码,false 反之,默认为 false */
				isShowPassword: false,
				showPasswordSwitchIcon: "../static/login/hidding-password.png",
				/** 标识当前输入框是否已获取焦点, true 为已获取焦点, false 反之,默认为 false */
				hasFocus: false,
				/** 标识是否已清除输入框内容, true 为已清除内容, false 反之 */
				isContentCleared: false,
			}
		},
		onLoad: function(){
			if(this.focus && this.focus == true){
				this.hasFocus = true
			}
		},
		props: {
			currentText: {
				type: String,
				defalut: ""
			},
			// 输入框前缀图标
			prefixIcon: {
				type: String,
				defalut: ""
			},
			// 配置输入框后缀文字
			suffixText: {
				type: String,
				default: ""
			},
			// 配置提示文字
			hintText: {
				type: String,
				defalut: ""
			},
			// 配置是否显示清除图标,true 为显示,false 为不显示
			isShowClearIcon: {
				type: Boolean,
				default: true,
			},
			// 配置是否是密码输入框,true 为密码输入框,false 反之
			isPassword: {
				type: Boolean,
				default: false
			},
			// 配置是否显示下划线,true 为显示,false 反之
			underline: {
				type: Boolean,
				default: true
			},
			// 键盘弹起时，是否自动上推页面,true 为上推,false 反之,默认为 true
			adjust_position: {
				type: Boolean,
				default: true
			},
			// 点击键盘右下角按钮时是否保持键盘不收起,true 不收起,false 反之
			confirm_hold: {
				type: Boolean,
				default: false
			},
			// focus时，点击页面的时候不收起键盘,true 为收起键盘,false 反之
			hold_keyboard: {
				type: Boolean,
				default: false
			},
			// 键盘收起时，是否自动失去焦点,true 为自动失去焦点,false 反之
			auto_blur: {
				type: Boolean,
				default: false
			},
			/**
			 * 设置键盘右下角按钮的文字，仅在 type="text" 时生效,
			 * 有效值:
			 * send     右下角按钮为“发送”
			 * search   右下角按钮为“搜索”
			 * next     右下角按钮为“下一个”
			 * go       右下角按钮为“前往”
			 * done     右下角按钮为“完成”
			 */
			confirm_type: {
				type: String,
				default: "done"
			},
			// 获取焦点,true 为获取焦点,false 反之
			focus: {
				type: Boolean,
				default: false
			},
			// 最大输入长度，设置为 -1 的时候不限制最大长度
			maxlength: {
				type: Number,
				default: -1
			},
			// 是否禁用,true 为禁用,false 反之
			disabled: {
				type: Boolean,
				default: false
			},
			// 组件样式
			componentStyle: {
				type: String,
				default: "rootView"
			},
			// 指定输入框样式
			inputStyle: {
				type: String,
				default: "uni-input"
			},
			// 指定 placeholder 的样式类
			placeholder_class: {
				type: String,
				default: "uni-input-placeholder"
			},
			/**
			 * 输入类型, type 有效值:
			 *  text    文本输入键盘
			 *  number  数字输入键盘
			 *  idcard  身份证输入键盘
			 *  digit   带小数点的数字键盘
			 *  tel     电话输入键盘
			 */
			type: {
				type: String,
				default: "text"
			},

		},
		computed: {
			judgeShowClearIcon() {
				return this.isShowClearIcon == true && 
					(this.hasFocus || this.isContentCleared) && 
					this.currentText && 
					this.currentText.length > 0;
			},
			handledText(){
				return this.currentText
			}
		},
		emits: ["onClear", "onChange", "onFocus", "onBlur", "confirm", "onKeyboardHeightChange"],
		methods: {
			switchShowPassword() {
				this.isShowPassword = !this.isShowPassword;
				if (this.isShowPassword) {
					this.showPasswordSwitchIcon = "../static/login/showing-password.png"
				} else {
					this.showPasswordSwitchIcon = "../static/login/hidding-password.png"
				}
			},
			judgeShowPassword() {
				return this.isPassword && !this.isShowPassword;
			},
			// 文字改变监听
			onChange(event) {
				// this.currentText = event.detail.value
				if(this.isContentCleared){
					this.isContentCleared = false
					// this.$emit('onChange', "")
				}else{
					this.$emit('onChange', event.detail.value)
				}
				
			},
			// 文字清除监听
			onClear() {
				this.isContentCleared = true
				this.$emit('onClear')
			},
			// 输入框获取焦点监听
			onFocus(event) {
				this.hasFocus = true
				this.$emit('onFocus', event.detail.value, event.detail.height)
			},
			// 输入框失去焦点监听
			onBlur(event) {
				this.hasFocus = false
				this.$emit('onBlur', event.detail.value)
			},
			// 点击键盘的完成按钮监听
			onConfirm(event) {
				this.$emit('onConfirm', event.detail.value)
			},
			// 键盘高度变化监听
			onKeyboardHeightChange(event) {
				this.$emit('onKeyboardHeightChange', event.detail.height, event.detail.duration)
			},
			// onFocusGot(){
			// 	this.hasFocus = true
			// },
			// onFocusLost(){
			// 	this.hasFocus = false
			// }
		}
	}
</script>
