package com.example.rtc_android.ui

import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
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
import com.example.rtc_android.databinding.FragmentLoginBinding
import kotlinx.coroutines.flow.collectLatest
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

        initLoginMessage()

        binding.apply {
            spinnerEnv.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    edtPlatformUrl.text = Editable.Factory.getInstance()
                        .newEditable(BuildConfig.LOGIN_ENVIRONMENT_VALUE[position])
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}

            }
            imgLogo.setOnLongClickListener {
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
            icDevCheck.setOnClickListener { viewModel.switchDevMode() }
            tvDevModeCheck.setOnClickListener { viewModel.switchDevMode() }
            edtEnterpriseId.addTextChangedListener {
                updateLoginButtonState()
            }
            edtUsername.addTextChangedListener {
                updateLoginButtonState()
            }
            edtPassword.addTextChangedListener {
                updateLoginButtonState()
            }
        }.apply {
            edtPlatformUrl.text =
                Editable.Factory.getInstance().newEditable(BuildConfig.LOGIN_ENVIRONMENT_VALUE[0])
            edtPlatformUrl.visibility = if (BuildConfig.DEBUG) View.VISIBLE else View.GONE
//            spinnerEnv.visibility = if (BuildConfig.DEBUG) View.VISIBLE else View.GONE
//            spinnerIcon.visibility = if (BuildConfig.DEBUG) View.VISIBLE else View.GONE
        }

        ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            BuildConfig.LOGIN_ENVIRONMENT_NAME
        ).also {
            it.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
            binding.spinnerEnv.adapter = it
        }


        obsState()
    }

    private fun initLoginMessage() {
        lifecycleScope.launchWhenResumed {
            viewModel.getLoginMessageFromLocalStore(requireContext())
                .collectLatest { loginMessage ->
                    binding.apply {
                        edtEnterpriseId.text = Editable.Factory.getInstance()
                            .newEditable(loginMessage.enterpriseId)
                        edtUsername.text = Editable.Factory.getInstance()
                            .newEditable(loginMessage.username)
                        edtPassword.text = Editable.Factory.getInstance()
                            .newEditable(loginMessage.password)
                    }
                }
        }
    }

    private fun updateLoginButtonState() {
        binding.apply {
            btnLogin.isEnabled = edtEnterpriseId.text.toString().isNotEmpty() &&
                    edtUsername.text.toString().isNotEmpty() &&
                    edtPassword.text.toString().isNotEmpty()
        }
    }

    private fun obsState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
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
                                if (viewModel.isDevMode.value) {
                                    findNavController().navigateUp()
                                } else {
                                    findNavController().navigate(R.id.action_loginFragment_to_demoMainFragment)
                                }
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
                launch {
                    viewModel.isDevMode.collect { isDevMode ->
                        if (isDevMode) {
                            binding.icDevCheck.setImageResource(R.drawable.icon_dev_mode_checked)
                        } else {
                            binding.icDevCheck.setImageResource(R.drawable.icon_dev_mode_unchecked)
                        }
                    }
                }
            }
        }
    }

}