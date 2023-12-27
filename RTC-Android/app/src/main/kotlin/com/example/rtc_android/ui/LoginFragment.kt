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
import com.example.common.AppViewModel
import com.example.rtc_android.BuildConfig
import com.example.rtc_android.R
import com.example.rtc_android.databinding.FragmentLoginBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding

    private val viewModel by activityViewModels<AppViewModel>()

    private var isIgnoreFillLoginMsg = false

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

                    if (isIgnoreFillLoginMsg) {
                        isIgnoreFillLoginMsg = false
                        return
                    }

                    edtEnterpriseId.text = Editable.Factory.getInstance()
                        .newEditable(BuildConfig.LOGIN_ENTERPRISE_ID_VALUE[position])
                    edtUsernameOrUserId.text = Editable.Factory.getInstance()
                        .newEditable(BuildConfig.LOGIN_USER_NAME_OR_USER_ID_VALUE[position])
                    edtPasswordOrAccessToken.text = Editable.Factory.getInstance()
                        .newEditable(BuildConfig.LOGIN_PASSWORD_OR_ACCESS_TOKEN_VALUE[position])
                    edtCallerNumber.text = Editable.Factory.getInstance()
                        .newEditable(BuildConfig.LOGIN_CALLER_NUMBER_VALUE[position])
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}

            }
            imgLogo.setOnLongClickListener {
                isIgnoreFillLoginMsg = true
                spinnerEnv.setSelection(resources.getInteger(R.integer.specified_spinner_env_selected_index))
                edtEnterpriseId.text = Editable.Factory.getInstance()
                    .newEditable(resources.getString(R.string.specified_enterprise_id))
                edtUsernameOrUserId.text = Editable.Factory.getInstance()
                    .newEditable(resources.getString(R.string.specified_username))
                edtPasswordOrAccessToken.text = Editable.Factory.getInstance()
                    .newEditable(resources.getString(R.string.specified_password))
                edtCallerNumber.text = Editable.Factory.getInstance()
                    .newEditable(resources.getString(R.string.specified_caller_number))
                true
            }
            btnLogin.setOnClickListener {
                viewModel.viewModelScope.launch {
                    viewModel.intentChannel.send(
                        AppIntent.Login(
                            context = requireContext(),
                            selectedEnvIndex = spinnerEnv.selectedItemPosition,
                            platformUrl = edtPlatformUrl.text.toString(),
                            enterpriseId = edtEnterpriseId.text.toString(),
                            usernameOrUserId = edtUsernameOrUserId.text.toString(),
                            passwordOrAccessToken = edtPasswordOrAccessToken.text.toString(),
                            callerNumber = edtCallerNumber.text.toString()
                        )
                    )
                }
            }
            icDevCheck.setOnClickListener { viewModel.switchDevMode() }
            tvDevModeCheck.setOnClickListener { viewModel.switchDevMode() }
            icSaveLoginMsgCheck.setOnClickListener { viewModel.switchSaveLoginMsgMode() }
            tvSaveLoginMsgCheck.setOnClickListener { viewModel.switchSaveLoginMsgMode() }
            edtEnterpriseId.addTextChangedListener {
                updateLoginButtonState()
            }
            edtUsernameOrUserId.addTextChangedListener {
                updateLoginButtonState()
            }
            edtPasswordOrAccessToken.addTextChangedListener {
                updateLoginButtonState()
            }
        }.apply {
//            edtPlatformUrl.text =
//                Editable.Factory.getInstance()
//                    .newEditable(BuildConfig.LOGIN_ENVIRONMENT_VALUE[BuildConfig.DEFAULT_SPINNER_SELECTION])
            edtPlatformUrl.visibility = if (BuildConfig.DEBUG) View.VISIBLE else View.GONE
//            edtEnterpriseId.text =
//                Editable.Factory.getInstance()
//                    .newEditable(BuildConfig.LOGIN_ENTERPRISE_ID_VALUE[BuildConfig.DEFAULT_SPINNER_SELECTION])

        }

        ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            BuildConfig.LOGIN_ENVIRONMENT_NAME
        ).also {
            it.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
            binding.spinnerEnv.adapter = it
        }

        binding.spinnerEnv.setSelection(BuildConfig.DEFAULT_SPINNER_SELECTION)

        obsState()
    }

    private fun initLoginMessage() {
        lifecycleScope.launchWhenResumed {
            viewModel.getLoginMessageFromLocalStore(requireContext())
                .collectLatest { loginMessage ->
                    isIgnoreFillLoginMsg = true
                    binding.apply {
                        spinnerEnv.setSelection(loginMessage.selectedEnvIndex)
                        edtEnterpriseId.text = Editable.Factory.getInstance()
                            .newEditable(loginMessage.enterpriseId)
                        edtUsernameOrUserId.text = Editable.Factory.getInstance()
                            .newEditable(loginMessage.usernameOrUserId)
                        edtUsernameOrUserId.text = Editable.Factory.getInstance()
                            .newEditable(loginMessage.passwordOrAccessToken)
                        edtCallerNumber.text = Editable.Factory.getInstance()
                            .newEditable(loginMessage.callerNumber)
                    }
                }
        }
    }

    private fun updateLoginButtonState() {
        binding.apply {
            btnLogin.isEnabled = edtEnterpriseId.text.toString().isNotEmpty() &&
                    edtUsernameOrUserId.text.toString().isNotEmpty() &&
                    edtPasswordOrAccessToken.text.toString().isNotEmpty()
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
                                Toast.makeText(
                                    requireContext(),
                                    "登录成功,引擎已创建",
                                    Toast.LENGTH_SHORT
                                )
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
                            binding.icDevCheck.setImageResource(R.drawable.icon_checked)
                        } else {
                            binding.icDevCheck.setImageResource(R.drawable.icon_unchecked)
                        }
                    }
                }
                launch {
                    viewModel.isSaveLoginMessage.collect { isSaveLoginMsg ->
                        if (isSaveLoginMsg) {
                            binding.icSaveLoginMsgCheck.setImageResource(R.drawable.icon_checked)
                        } else {
                            binding.icSaveLoginMsgCheck.setImageResource(R.drawable.icon_unchecked)
                        }
                    }
                }
            }
        }
    }

}