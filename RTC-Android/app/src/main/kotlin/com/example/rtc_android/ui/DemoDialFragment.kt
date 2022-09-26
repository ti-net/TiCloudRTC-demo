package com.example.rtc_android.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.example.common.AppIntent
import com.example.common.AppUiState
import com.example.common.AppViewModel
import com.example.rtc_android.R
import com.example.rtc_android.databinding.FragmentDemoDialBinding
import com.tinet.ticloudrtc.ErrorCode
import kotlinx.coroutines.launch

class DemoDialFragment : Fragment() {

    private lateinit var binding: FragmentDemoDialBinding
    private val viewModel by activityViewModels<AppViewModel>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDemoDialBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            btnNum1.setOnClickListener { addNum(1) }
            btnNum2.setOnClickListener { addNum(2) }
            btnNum3.setOnClickListener { addNum(3) }
            btnNum4.setOnClickListener { addNum(4) }
            btnNum5.setOnClickListener { addNum(5) }
            btnNum6.setOnClickListener { addNum(6) }
            btnNum7.setOnClickListener { addNum(7) }
            btnNum8.setOnClickListener { addNum(8) }
            btnNum9.setOnClickListener { addNum(9) }
            btnNum0.setOnClickListener { addNum(0) }
            btnDemoCall.setOnClickListener {
                if (tvShowPhoneNum.text.matches(Regex("^1(3\\d|4[5-9]|5[0-35-9]|6[567]|7[0-8]|8\\d|9[0-35-9])\\d{8}\$"))) {
                    viewModel.viewModelScope.launch {
                        viewModel.intentChannel.send(
                            AppIntent.Call(
                                tel = tvShowPhoneNum.text.toString(),
                                clid = "",
                                userField = "",
                                type = 6 // 6 为外呼场景
                            )
                        )
                    }
                } else {
                    Toast.makeText(requireContext(), "无效号码", Toast.LENGTH_SHORT).show()
                }
            }
            btnDemoDelete.setOnClickListener { deleteNum() }
            btnDemoDelete.setOnLongClickListener { tvShowPhoneNum.text = ""; true }
        }.apply {
            tvShowPhoneNum.text = arguments?.getString("phoneNum") ?: ""
        }

        obsState()
    }

    private fun addNum(num: Int) {
        addNum(num.toString())
    }

    private fun addNum(num: String) {
        binding.tvShowPhoneNum.text = binding.tvShowPhoneNum.text.toString() + num
    }

    private fun deleteNum() {
        binding.tvShowPhoneNum.text = binding.tvShowPhoneNum.text.toString().dropLast(1)
    }

    private fun obsState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.appUiState.collect {
                    when (it) {
                        is AppUiState.OnInnerSdkError -> {
                            if (it.errorCode == ErrorCode.ERR_CALL_FAILED_PARAMS_INCORRECT) {
                                Toast.makeText(
                                    requireContext(),
                                    """
                                        sdk 内部错误
                                        errorCode: ${it.errorCode}
                                        errorMessage: ${it.errorMessage}
                                    """.trimIndent(),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        is AppUiState.CallFailed -> Toast.makeText(
                            requireContext(),
                            "外呼失败：${it.errorMsg}",
                            Toast.LENGTH_SHORT
                        ).show()
                        is AppUiState.OnCallStart -> findNavController().navigate(R.id.action_demoDialFragment_to_demoCallingFragment)
                        is AppUiState.OnCallFailure -> Toast.makeText(
                            requireContext(),
                            "外呼错误：${it.errorMsg}",
                            Toast.LENGTH_SHORT
                        ).show()
                        is AppUiState.OnRefreshTokenFailed -> {
                            Toast.makeText(
                                requireContext(),
                                it.errorMsg,
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