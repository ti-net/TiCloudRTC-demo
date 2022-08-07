package com.example.rtc_android_compose.ui

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.common.AppIntent
import com.example.common.AppUiState
import com.example.common.MainActivityViewModel
import com.example.rtc_android_compose.R
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LoginPage(
    mainViewModel: MainActivityViewModel,
    navController: NavController
) {
    val context = LocalContext.current
    var strPlatform by remember { mutableStateOf("") }
    var enterpriseId by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 30.dp),
        horizontalAlignment = CenterHorizontally
    ) {
        Image(painter = painterResource(id = R.mipmap.ic_launcher),
            contentDescription = "",
            modifier = Modifier.combinedClickable(
                onLongClick = {
                    strPlatform = context.getString(R.string.default_url)
                    enterpriseId = context.getString(R.string.default_enterprise_id)
                    username = context.getString(R.string.default_username)
                    password = context.getString(R.string.default_password)
                }
            ) {})
        TextField(
            value = strPlatform,
            placeholder = { Text(context.getString(R.string.app_compose_platform_url)) },
            onValueChange = { strPlatform = it })
        TextField(
            value = enterpriseId,
            placeholder = { Text(context.getString(R.string.app_compose_enterprise_id)) },
            onValueChange = { enterpriseId = it })
        TextField(
            value = username,
            placeholder = { Text(context.getString(R.string.app_compose_username)) },
            onValueChange = { username = it })
        TextField(value = password,
            placeholder = { Text(context.getString(R.string.app_compose_password)) },
            onValueChange = { password = it })
        Button(modifier = Modifier.fillMaxWidth(), onClick = {
            mainViewModel.viewModelScope.launch {
                mainViewModel.intentChannel.send(
                    AppIntent.Login(
                        context,
                        platformUrl = strPlatform,
                        enterpriseId = enterpriseId,
                        username = username,
                        password = password
                    )
                )
            }
        }) {
            Text("登录")
        }
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.lifecycleScope.launch {
            lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                mainViewModel.appUiState.collect {
                    Log.i("app_compose","login page collected state: $it")
                    when (it) {
                        is AppUiState.OnInnerSdkError -> Toast.makeText(
                            context,
                            """
                                sdk 内部错误
                                errorCode: ${it.errorCode}
                                errorMessage: ${it.errorMessage}
                            """.trimIndent(),
                            Toast.LENGTH_SHORT
                        ).show()
                        is AppUiState.WaitToLogin -> {}
                        is AppUiState.LoginSuccess -> {
                            Toast.makeText(context, "登录成功,引擎已创建", Toast.LENGTH_SHORT).show()
                            navController.navigateUp()
                        }
                        is AppUiState.LogoutSuccess -> Toast.makeText(
                            context,
                            "退出登录，引擎成功销毁",
                            Toast.LENGTH_SHORT
                        ).show()
                        is AppUiState.LoginFailed -> {
                            Toast.makeText(
                                context,
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