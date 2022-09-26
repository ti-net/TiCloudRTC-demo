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
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.common.AppUiState
import com.example.common.AppViewModel
import com.example.rtc_android.R
import com.example.rtc_android.databinding.FragmentMainBinding
import com.tinet.ticloudrtc.ErrorCode
import kotlinx.coroutines.launch

class MainFragment : Fragment() {

    private lateinit var binding: FragmentMainBinding

    private val viewModel by activityViewModels<AppViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (viewModel.isRtcClientInit().not()) {
            // 引擎未创建，去登录界面登录并创建引擎
            findNavController().navigate(R.id.action_mainFragment_to_loginFragment)
        }

        val navHostFragment =
            childFragmentManager.findFragmentById(R.id.bottom_nav_container) as NavHostFragment
        NavigationUI.setupWithNavController(
            binding.bottomNavigationView,
            navHostFragment.navController
        )


        obsState()
    }

    private fun obsState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.appUiState.collect {
                    when (it) {
                        is AppUiState.OnInnerSdkError -> {
                            if(it.errorCode == ErrorCode.ERR_CALL_FAILED_PARAMS_INCORRECT) {
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
                        is AppUiState.LogoutFailed -> Toast.makeText(
                            requireContext(),
                            "退出登录失败：${it.errorMsg}",
                            Toast.LENGTH_SHORT
                        ).show()
                        is AppUiState.LogoutSuccess -> findNavController().navigate(R.id.global_action_to_loginFragment2)
                        is AppUiState.CallFailed -> Toast.makeText(
                            requireContext(),
                            "外呼失败：${it.errorMsg}",
                            Toast.LENGTH_SHORT
                        ).show()
                        is AppUiState.OnCallStart -> findNavController().navigate(R.id.action_mainFragment_to_callingFragment)
                        is AppUiState.OnCallFailure -> Toast.makeText(
                            requireContext(),
                            "外呼错误：${it.errorMsg}",
                            Toast.LENGTH_SHORT
                        ).show()
                        is AppUiState.OnRefreshTokenFailed ->{
                            Toast.makeText(
                                requireContext(),
                                it.errorMsg,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        is AppUiState.OnAccessTokenHasExpired ->{
                            Toast.makeText(
                                requireContext(),
                                "access token 已过期",
                                Toast.LENGTH_SHORT
                            ).show()
                            findNavController().navigate(R.id.action_mainFragment_to_loginFragment)
                        }
                        else -> {}
                    }
                }
            }
        }
    }


}