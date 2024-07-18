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
import androidx.navigation.fragment.findNavController
import com.example.common.AppIntent
import com.example.common.AppUiState
import com.example.common.AppViewModel
import com.example.rtc_android.R
import com.example.rtc_android.databinding.FragmentDemoCallingBinding
import kotlinx.coroutines.launch

class DemoCallingFragment : Fragment() {

    private lateinit var binding: FragmentDemoCallingBinding

    private val viewModel by activityViewModels<AppViewModel>()

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
            btnDemoCallingKeyboardSwitch.setOnClickListener {
                viewModel.handleIntent(AppIntent.ClickDtmfButton)
            }

            btnDemoCallingMuteSwitch.setOnClickListener {
                viewModel.handleIntent(AppIntent.ClickMuteButton)
            }

            btnDemoCallingSpeakerSwitch.setOnClickListener {
                if (viewModel.appUiState.value is AppUiState.OnRinging ||
                    viewModel.appUiState.value is AppUiState.OnCalling
                ) {
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

            btnDemoCallingHide.setOnClickListener { viewModel.handleIntent(AppIntent.ClickSpeakerPhoneButton) }

            btnDemoCallingHangup.setOnClickListener { viewModel.handleIntent(AppIntent.Hangup) }

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
            tvDemoCallingBiggerText.text = ""
        }

        startStatusBarControl()

        obsState()
    }

    private fun sendDtmf(digits: String) = viewModel.handleIntent(AppIntent.SendDtmf(digits))

    private fun backToMain() {
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
                                backToMain()
                            }
                            is AppUiState.OnCallStart -> {
                                binding.tvDemoCallingBiggerText.text = it.biggerText
                            }

                            is AppUiState.OnRinging -> {
                                binding.tvDemoCallingSmallerText.text = it.smallerText
                            }

                            is AppUiState.OnCalling -> {
                                binding.btnDemoCallingMuteSwitch.setImageResource(
                                    if (it.isMuted) R.drawable.icon_demo_calling_mic_disable else R.drawable.icon_demo_calling_mic_enable
                                )

                                binding.btnDemoCallingSpeakerSwitch.setImageResource(
                                    if(it.isUseSpeakerPhone) R.drawable.icon_demo_calling_speaker_enable else R.drawable.icon_demo_calling_speaker_disable
                                )

                                binding.tvDemoCallingBiggerText.text = it.biggerText

                                binding.tvDemoCallingSmallerText.text = it.smallerText

                                if (it.isShowDtmfPanel) {
                                    binding.includeDemoCallingDtmfPanel.refDtmfPanel.visibility = View.VISIBLE
                                    binding.btnDemoCallingHide.visibility = View.VISIBLE
                                    binding.tvDemoCallingSmallerText.visibility = View.GONE
                                    binding.btnDemoCallingKeyboardSwitch.visibility = View.GONE
                                    binding.tvDemoCallingKeyboard.visibility = View.GONE
                                    binding.btnDemoCallingMuteSwitch.visibility = View.GONE
                                    binding.tvDemoCallingMute.visibility = View.GONE
                                    binding.btnDemoCallingSpeakerSwitch.visibility = View.GONE
                                    binding.tvDemoCallingSpeaker.visibility = View.GONE
                                } else {
                                    binding.includeDemoCallingDtmfPanel.refDtmfPanel.visibility = View.GONE
                                    binding.btnDemoCallingHide.visibility = View.GONE
                                    binding.tvDemoCallingSmallerText.visibility = View.VISIBLE
                                    binding.btnDemoCallingKeyboardSwitch.visibility = View.VISIBLE
                                    binding.tvDemoCallingKeyboard.visibility = View.VISIBLE
                                    binding.btnDemoCallingMuteSwitch.visibility = View.VISIBLE
                                    binding.tvDemoCallingMute.visibility = View.VISIBLE
                                    binding.btnDemoCallingSpeakerSwitch.visibility = View.VISIBLE
                                    binding.tvDemoCallingSpeaker.visibility = View.VISIBLE
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

                            else -> {}
                        }
                    }
                }

            }
        }
    }
}