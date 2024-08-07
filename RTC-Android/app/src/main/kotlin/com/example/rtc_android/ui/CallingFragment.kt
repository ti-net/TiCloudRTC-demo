package com.example.rtc_android.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.common.AppIntent
import com.example.common.AppUiState
import com.example.common.AppViewModel
import com.example.rtc_android.R
import com.example.rtc_android.databinding.FragmentCallingBinding
import kotlinx.coroutines.launch

class CallingFragment : Fragment() {

    private lateinit var binding: FragmentCallingBinding

    private val viewModel by activityViewModels<AppViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCallingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            tvShowCallTel.text = AppViewModel.DEFAULT_BIGGER_TEXT

            btnDialPanelSwitch.setOnClickListener {
                viewModel.handleIntent(AppIntent.ClickDtmfButton)
            }

            btnHideDial.setOnClickListener {
                viewModel.handleIntent(AppIntent.ClickDtmfButton)
            }

            includeDtmfPanel.apply {
                numPanel0.setOnClickListener { sendDtmf("0") }
                numPanel1.setOnClickListener { sendDtmf("1") }
                numPanel2.setOnClickListener { sendDtmf("2") }
                numPanel3.setOnClickListener { sendDtmf("3") }
                numPanel4.setOnClickListener { sendDtmf("4") }
                numPanel5.setOnClickListener { sendDtmf("5") }
                numPanel6.setOnClickListener { sendDtmf("6") }
                numPanel7.setOnClickListener { sendDtmf("7") }
                numPanel8.setOnClickListener { sendDtmf("8") }
                numPanel9.setOnClickListener { sendDtmf("9") }
                numPanelAsterisk.setOnClickListener { sendDtmf("*") }
                numPanelSign.setOnClickListener { sendDtmf("#") }
            }

            btnMuteSwitch.setOnClickListener {
                Log.i(LOG_TAG, "mute btn click")
                viewModel.handleIntent(AppIntent.ClickMuteButton)
            }

            btnSpeakerphoneSwitch.setOnClickListener {
                if (viewModel.appUiState.value is AppUiState.OnRinging ||
                    viewModel.appUiState.value is AppUiState.OnCalling
                ) {
                    Log.i(LOG_TAG, "speakerphone btn click")
                    viewModel.handleIntent(AppIntent.ClickSpeakerPhoneButton)
                } else {
                    Toast.makeText(
                        requireContext(),
                        "播放铃声或通话中才能使用扬声器",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }
            }

            btnHangup.setOnClickListener {
                hangup()
            }
        }

        obsState()
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
                                backToMain()
                            }

                            is AppUiState.OnCallStart -> {
                                binding.tvShowCallTel.text = it.biggerText
                            }

                            is AppUiState.OnRinging -> {
                                binding.tvCallingTip.text = it.smallerText
                            }

                            is AppUiState.OnCalling -> {
                                binding.btnMuteSwitch.setImageResource(
                                    if (it.isMuted) R.drawable.icon_mute else R.drawable.icon_no_mute
                                )

                                binding.btnSpeakerphoneSwitch.setImageResource(
                                    if(it.isUseSpeakerPhone) R.drawable.icon_speaker_enable else R.drawable.icon_speaker_disable
                                )

                                binding.tvShowCallTel.text = it.biggerText

                                binding.tvCallingTip.text = it.smallerText

                                if (it.isShowDtmfPanel) {
                                    binding.includeDtmfPanel.rtcSdkNumberPanel.visibility = View.VISIBLE
                                    binding.btnHideDial.visibility = View.VISIBLE
                                    binding.btnDialPanelSwitch.visibility = View.GONE
                                    binding.btnMuteSwitch.visibility = View.GONE
                                    binding.btnSpeakerphoneSwitch.visibility = View.GONE
                                } else {
                                    binding.includeDtmfPanel.rtcSdkNumberPanel.visibility = View.GONE
                                    binding.btnHideDial.visibility = View.GONE
                                    binding.btnDialPanelSwitch.visibility = View.VISIBLE
                                    binding.btnMuteSwitch.visibility = View.VISIBLE
                                    binding.btnSpeakerphoneSwitch.visibility = View.VISIBLE
                                }
                            }

                            is AppUiState.OnCallCanceled -> {
                                Toast.makeText(requireContext(), "呼叫已取消", Toast.LENGTH_SHORT)
                                    .show()
                                backToMain()
                            }

                            is AppUiState.OnCallRefused -> {
                                Toast.makeText(requireContext(), "外呼被拒绝", Toast.LENGTH_SHORT)
                                    .show()
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
                            is AppUiState.OnLocalNoVoiceStreamSent -> {
                                Toast.makeText(
                                    requireContext(),
                                    "本地没有音频流",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            is AppUiState.OnUserFieldModified -> {
                                Toast.makeText(
                                    requireContext(),
                                    "userField 中的 ${it.removedCharList} 字符已被替换",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            else -> {}
                        }
                    }
                }
            }
        }
    }

    private fun sendDtmf(digits: String) {
        viewModel.handleIntent(AppIntent.SendDtmf(digits))
    }

    private fun backToMain() {
        findNavController().navigateUp()
    }

    private fun hangup() {
        Log.i(LOG_TAG, "hangup btn click")
        viewModel.handleIntent(AppIntent.Hangup)
    }


    companion object {
        private var LOG_TAG = CallingFragment::class.java.simpleName
    }
}