package com.example.rtc_android.ui

import android.os.Bundle
import android.text.Editable
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
import com.example.rtc_android.AppIntent
import com.example.rtc_android.AppUiState
import com.example.rtc_android.MainActivityViewModel
import com.example.rtc_android.R
import com.example.rtc_android.databinding.FragmentLoginBinding
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding

    private val viewModel by activityViewModels<MainActivityViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            imgLogo.setOnLongClickListener {
                edtPlatformUrl.text = Editable.Factory.getInstance()
                    .newEditable(resources.getString(R.string.default_url))
                edtEnterpriseId.text = Editable.Factory.getInstance()
                    .newEditable(resources.getString(R.string.default_enterprise_id))
                edtUsername.text = Editable.Factory.getInstance()
                    .newEditable(resources.getString(R.string.default_username))
                edtPassword.text = Editable.Factory.getInstance()
                    .newEditable(resources.getString(R.string.default_password))
                true
            }
            btnLogin.setOnClickListener {
                viewModel.viewModelScope.launch {
                    viewModel.intentChannel.send(
                        AppIntent.Login(
                            context = requireContext(),
                            platformUrl = edtPlatformUrl.text.toString(),
                            enterpriseId = edtEnterpriseId.text.toString(),
                            username = edtUsername.text.toString(),
                            password = edtPassword.text.toString()
                        )
                    )
                }
            }
        }

        obsState()
    }

    private fun obsState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.appUiState.collect {
                    when (it) {
                        is AppUiState.OnInnerSdkError -> Toast.makeText(
                            requireContext(),
                            """
                                sdk 内部错误
                                errorCode: ${it.errorCode}
                                errorMessage: ${it.errorMessage}
                            """.trimIndent(),
                            Toast.LENGTH_SHORT
                        ).show()
                        is AppUiState.WaitToLogin -> {}
                        is AppUiState.LoginSuccess -> {
                            Toast.makeText(requireContext(), "登录成功,引擎已创建", Toast.LENGTH_SHORT)
                                .show()
                            findNavController().navigateUp()
                        }
                        is AppUiState.LogoutSuccess -> Toast.makeText(
                            requireContext(),
                            "退出登录，引擎成功销毁",
                            Toast.LENGTH_SHORT
                        ).show()
                        is AppUiState.LoginFailed -> {
                            Toast.makeText(
                                requireContext(),
                                "登录失败:${it.errorMsg}",
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