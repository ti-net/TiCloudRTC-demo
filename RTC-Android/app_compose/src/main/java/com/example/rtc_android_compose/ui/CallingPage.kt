package com.example.rtc_android_compose.ui

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.common.AppIntent
import com.example.common.AppUiState
import com.example.common.AppViewModel
import com.example.rtc_android_compose.ui.theme.App_composeTheme
import com.tinet.ticloudrtc.ErrorCode
import kotlinx.coroutines.launch

@Composable
fun CallingPage(
    mainViewModel: AppViewModel,
    onBackToMain:()->Unit={}
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val currentPhoneNum by mainViewModel.biggerText.collectAsState()

    val isShowDialPanel by mainViewModel.isShowDtmfPanel.collectAsState()

    val smallText by mainViewModel.smallText.collectAsState()

    val muteState by mainViewModel.muteState.collectAsState()

    val speakerphoneOpenState by mainViewModel.speakerphoneOpenState.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(currentPhoneNum)
        Text(smallText)
        if (isShowDialPanel) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Button(onClick = { sendDtmf(mainViewModel, "1") }) { Text("1") }
                Button(onClick = { sendDtmf(mainViewModel, "2") }) { Text("2") }
                Button(onClick = { sendDtmf(mainViewModel, "3") }) { Text("3") }
            }
            Row(modifier = Modifier.fillMaxWidth()) {
                Button(onClick = { sendDtmf(mainViewModel, "4") }) { Text("4") }
                Button(onClick = { sendDtmf(mainViewModel, "5") }) { Text("5") }
                Button(onClick = { sendDtmf(mainViewModel, "6") }) { Text("6") }
            }
            Row(modifier = Modifier.fillMaxWidth()) {
                Button(onClick = { sendDtmf(mainViewModel, "7") }) { Text("7") }
                Button(onClick = { sendDtmf(mainViewModel, "8") }) { Text("8") }
                Button(onClick = { sendDtmf(mainViewModel, "9") }) { Text("9") }
            }
            Row(modifier = Modifier.fillMaxWidth()) {
                Button(onClick = { sendDtmf(mainViewModel, "*") }) { Text("*") }
                Button(onClick = { sendDtmf(mainViewModel, "0") }) { Text("0") }
                Button(onClick = { sendDtmf(mainViewModel, "#") }) { Text("#") }
            }
        } else {
            Row {
                Button(onClick = { mainViewModel.switchDtmfShowState() }) {
                    Text("dtmf")
                }
                Button(onClick = {
                    mainViewModel.viewModelScope.launch {
                        Log.i("CallingPage", "mute btn click")
                        mainViewModel.intentChannel.send(
                            AppIntent.Mute(
                                mainViewModel.isMute().not()
                            )
                        )
                    }
                }) {
                    Text("mute")
                }
                Button(onClick = {
                    if (mainViewModel.appUiState.value is AppUiState.OnRinging ||
                        mainViewModel.appUiState.value is AppUiState.OnCalling
                    ) {
                        mainViewModel.viewModelScope.launch {
                            Log.i("CallingPage", "speakerphone btn click")
                            mainViewModel.intentChannel.send(
                                AppIntent.UseSpeakerphone(
                                    mainViewModel.isUseSpeakerphone().not()
                                )
                            )
                        }
                    } else {
                        Toast.makeText(context, "播放铃声或通话中才能使用扬声器", Toast.LENGTH_SHORT).show()
                    }
                }) {
                    Text("speakerphone")
                }
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(onClick = {
                mainViewModel.viewModelScope.launch {
                    Log.i("CallingPage", "hangup btn click")
                    mainViewModel.intentChannel.send(AppIntent.Hangup)
                }
            }) {
                Text("挂断")
            }
            if (isShowDialPanel) {
                Button(onClick = {
                    mainViewModel.switchDtmfShowState()
                }) {
                    Text("隐藏")
                }
            }
        }
    }

    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.lifecycleScope.launch {
            lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    mainViewModel.appUiState.collect {
                        when (it) {
                            is AppUiState.OnInnerSdkError -> {
                                Toast.makeText(
                                    context,
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
                                    ErrorCode.ERR_CALL_FAILED_RTM_ERROR -> onBackToMain()
                                }
                            }
                            is AppUiState.OnCallCanceled -> {
                                Toast.makeText(context, "呼叫已取消", Toast.LENGTH_SHORT).show()
                                onBackToMain()
                            }
                            is AppUiState.OnCallRefused -> {
                                Toast.makeText(context, "外呼被拒绝", Toast.LENGTH_SHORT).show()
                                onBackToMain()
                            }
                            is AppUiState.OnCallingEnd -> {
                                Toast.makeText(
                                    context,
                                    "外呼结束：${if (it.isPeerHangup) "对方挂断" else "己方挂断"}",
                                    Toast.LENGTH_SHORT
                                ).show()
                                onBackToMain()
                            }
                            is AppUiState.OnCallFailure -> {
                                Toast.makeText(
                                    context,
                                    "外呼错误：${it.errorMsg}",
                                    Toast.LENGTH_SHORT
                                ).show()
                                onBackToMain()
                            }
                            is AppUiState.OnRefreshTokenFailed -> {
                                Toast.makeText(
                                    context,
                                    it.errorMsg,
                                    Toast.LENGTH_SHORT
                                ).show()
                                onBackToMain()
                            }
                            is AppUiState.OnAccessTokenHasExpired -> {
                                Toast.makeText(
                                    context,
                                    "access token 已过期",
                                    Toast.LENGTH_SHORT
                                ).show()
                                onBackToMain()
                            }
                            else -> {}
                        }
                    }
                }
            }
        }
    }
}

fun sendDtmf(mainViewModel: AppViewModel, digits: String) {
    mainViewModel.viewModelScope.launch {
        mainViewModel.intentChannel.send(AppIntent.SendDtmf(digits))
    }
}

@Preview
@Composable
fun PreviewCallingPage(){
    App_composeTheme {
        CallingPage(viewModel())
    }
}