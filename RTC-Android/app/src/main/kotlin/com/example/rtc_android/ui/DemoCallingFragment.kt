package com.example.rtc_android.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.example.common.AppIntent
import com.example.common.AppUiState
import com.example.common.MainActivityViewModel
import com.example.rtc_android.R
import com.example.rtc_android.databinding.FragmentDemoCallingBinding
import com.tinet.ticloudrtc.ErrorCode
import kotlinx.coroutines.launch

class DemoCallingFragment : Fragment() {

    private lateinit var binding: FragmentDemoCallingBinding

    private val viewModel by activityViewModels<MainActivityViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDemoCallingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            btnDemoCallingKeyboardSwitch.setOnClickListener { viewModel.switchDtmfShowState() }

            btnDemoCallingMuteSwitch.setOnClickListener {
                sendIntent(
                    AppIntent.Mute(
                        viewModel.isMute().not()
                    )
                )
            }

            btnDemoCallingSpeakerSwitch.setOnClickListener {
                if (viewModel.appUiState.value is AppUiState.OnRinging ||
                    viewModel.appUiState.value is AppUiState.OnCalling
                ) {
                    sendIntent(AppIntent.UseSpeakerphone(viewModel.isUseSpeakerphone().not()))
                } else {
                    Toast.makeText(requireContext(), "播放铃声或通话中才能使用扬声器", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
            }

            btnDemoCallingHide.setOnClickListener { viewModel.switchDtmfShowState() }

            btnDemoCallingHangup.setOnClickListener { sendIntent(AppIntent.Hangup) }

            includeDemoCallingDtmfPanel.apply {
                btnDtmf0.setOnClickListener { sendDtmf("0") }
                btnDtmf1.setOnClickListener { sendDtmf("1") }
                btnDtmf2.setOnClickListener { sendDtmf("2") }
                btnDtmf3.setOnClickListener { sendDtmf("3") }
                btnDtmf4.setOnClickListener { sendDtmf("4") }
                btnDtmf5.setOnClickListener { sendDtmf("5") }
                btnDtmf6.setOnClickListener { sendDtmf("6") }
                btnDtmf7.setOnClickListener { sendDtmf("7") }
                btnDtmf8.setOnClickListener { sendDtmf("8") }
                btnDtmf9.setOnClickListener { sendDtmf("9") }
                btnDtmfAsterisk.setOnClickListener { sendDtmf("*") }
                btnDtmfSign.setOnClickListener { sendDtmf("#") }
            }

        }.apply {
            tvDemoCallingBiggerText.text = viewModel.biggerText.value
        }

        startStatusBarControl()

        obsState()
    }

    private fun sendDtmf(digits: String) = sendIntent(AppIntent.SendDtmf(digits))

    private fun sendIntent(appIntent: AppIntent) {
        viewModel.viewModelScope.launch {
            viewModel.intentChannel.send(appIntent)
        }
    }

    private fun backToMain(){
        findNavController().navigateUp()
    }

    private fun startStatusBarControl() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                WindowCompat.getInsetsController(requireActivity().window, binding.root)
                    .hide(WindowInsetsCompat.Type.statusBars())
            }
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.DESTROYED) {
                WindowCompat.getInsetsController(requireActivity().window, binding.root)
                    .show(WindowInsetsCompat.Type.statusBars())
            }
        }
    }

    private fun obsState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.appUiState.collect {
                        when (it) {
                            is AppUiState.OnInnerSdkError -> {
                                Toast.makeText(
                                    requireContext(),
                                    """
                                    sdk 内部错误
                                    errorCode: ${it.errorCode}
                                    errorMessage: ${it.errorMessage}
                                """.trimIndent(),
                                    Toast.LENGTH_SHORT
                                ).show()
                                when (it.errorCode) {
                                    ErrorCode.ERR_CALL_FAILED_PARAMS_INCORRECT,
                                    ErrorCode.ERR_CALL_FAILED_CALL_REPEAT,
                                    ErrorCode.ERR_CALL_FAILED_REMOTE_OFFLINE,
                                    ErrorCode.ERR_CALL_FAILED_NET_ERROR,
                                    ErrorCode.ERR_CALL_FAILED_RTM_ERROR -> backToMain()
                                }
                            }
                            is AppUiState.OnCallCanceled -> {
                                Toast.makeText(requireContext(), "呼叫已取消", Toast.LENGTH_SHORT).show()
                                backToMain()
                            }
                            is AppUiState.OnCallRefused -> {
                                Toast.makeText(requireContext(), "外呼被拒绝", Toast.LENGTH_SHORT).show()
                                backToMain()
                            }
                            is AppUiState.OnCallingEnd -> {
                                Toast.makeText(
                                    requireContext(),
                                    "外呼结束：${if (it.isPeerHangup) "对方挂断" else "己方挂断"}",
                                    Toast.LENGTH_SHORT
                                ).show()
                                backToMain()
                            }
                            is AppUiState.OnCallFailure -> {
                                Toast.makeText(
                                    requireContext(),
                                    "外呼错误：${it.errorMsg}",
                                    Toast.LENGTH_SHORT
                                ).show()
                                backToMain()
                            }
                            is AppUiState.OnRefreshTokenFailed -> {
                                Toast.makeText(
                                    requireContext(),
                                    it.errorMsg,
                                    Toast.LENGTH_SHORT
                                ).show()
                                backToMain()
                            }
                            is AppUiState.OnAccessTokenHasExpired -> {
                                Toast.makeText(
                                    requireContext(),
                                    "access token 已过期",
                                    Toast.LENGTH_SHORT
                                ).show()
                                backToMain()
                            }
                            else -> {}
                        }
                    }
                }
                launch {
                    viewModel.biggerText.collect{
                        binding.tvDemoCallingBiggerText.text = it
                    }
                }
                launch {
                    viewModel.muteState.collect {
                        binding.btnDemoCallingMuteSwitch.setImageResource(
                            when (it) {
                                true -> R.drawable.icon_demo_calling_mic_disable
                                false -> R.drawable.icon_demo_calling_mic_enable
                            }
                        )
                    }
                }
                launch {
                    viewModel.speakerphoneOpenState.collect {
                        binding.btnDemoCallingSpeakerSwitch.setImageResource(
                            when (it) {
                                true -> R.drawable.icon_demo_calling_speaker_enable
                                false -> R.drawable.icon_demo_calling_speaker_disable
                            }
                        )
                    }
                }
                launch {
                    viewModel.smallText.collect {
                        binding.tvDemoCallingSmallerText.text = it
                    }
                }
                launch {
                    viewModel.isShowDtmfPanel.collect { isShow ->
                        binding.apply {
                            if (isShow) {
                                includeDemoCallingDtmfPanel.refDtmfPanel.visibility = View.VISIBLE
                                btnDemoCallingHide.visibility = View.VISIBLE
                                tvDemoCallingSmallerText.visibility = View.GONE
                                btnDemoCallingKeyboardSwitch.visibility = View.GONE
                                tvDemoCallingKeyboard.visibility = View.GONE
                                btnDemoCallingMuteSwitch.visibility = View.GONE
                                tvDemoCallingMute.visibility = View.GONE
                                btnDemoCallingSpeakerSwitch.visibility = View.GONE
                                tvDemoCallingSpeaker.visibility = View.GONE
                            } else {
                                includeDemoCallingDtmfPanel.refDtmfPanel.visibility = View.GONE
                                btnDemoCallingHide.visibility = View.GONE
                                tvDemoCallingSmallerText.visibility = View.VISIBLE
                                btnDemoCallingKeyboardSwitch.visibility = View.VISIBLE
                                tvDemoCallingKeyboard.visibility = View.VISIBLE
                                btnDemoCallingMuteSwitch.visibility = View.VISIBLE
                                tvDemoCallingMute.visibility = View.VISIBLE
                                btnDemoCallingSpeakerSwitch.visibility = View.VISIBLE
                                tvDemoCallingSpeaker.visibility = View.VISIBLE
                            }
                        }
                    }
                }

            }
        }
    }
}