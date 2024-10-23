package com.example.rtc_android_compose.ui

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.common.AppIntent
import com.example.common.AppUiState
import com.example.common.AppViewModel
import com.example.rtc_android_compose.ui.theme.App_composeTheme
import kotlinx.coroutines.launch

@Composable
fun CallingPage(
    mainViewModel: AppViewModel,
    onBackToMain: () -> Unit = {},
    handleIntent: (intent: AppIntent) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var currentPhoneNum by remember { mutableStateOf("") }

    var isShowDialPanel by remember { mutableStateOf(false) }

    var smallText by remember { mutableStateOf("") }

    var muteState by remember { mutableStateOf(false) }

    var speakerphoneOpenState by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(currentPhoneNum)
        Text(smallText)
        if (isShowDialPanel) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Button(onClick = { handleIntent(AppIntent.SendDtmf("1")) }) { Text("1") }
                Button(onClick = { handleIntent(AppIntent.SendDtmf("2")) }) { Text("2") }
                Button(onClick = { handleIntent(AppIntent.SendDtmf("3")) }) { Text("3") }
            }
            Row(modifier = Modifier.fillMaxWidth()) {
                Button(onClick = { handleIntent(AppIntent.SendDtmf("4")) }) { Text("4") }
                Button(onClick = { handleIntent(AppIntent.SendDtmf("5")) }) { Text("5") }
                Button(onClick = { handleIntent(AppIntent.SendDtmf("6")) }) { Text("6") }
            }
            Row(modifier = Modifier.fillMaxWidth()) {
                Button(onClick = { handleIntent(AppIntent.SendDtmf("7")) }) { Text("7") }
                Button(onClick = { handleIntent(AppIntent.SendDtmf("8")) }) { Text("8") }
                Button(onClick = { handleIntent(AppIntent.SendDtmf("9")) }) { Text("9") }
            }
            Row(modifier = Modifier.fillMaxWidth()) {
                Button(onClick = { handleIntent(AppIntent.SendDtmf("*")) }) { Text("*") }
                Button(onClick = { handleIntent(AppIntent.SendDtmf("0")) }) { Text("0") }
                Button(onClick = { handleIntent(AppIntent.SendDtmf("#")) }) { Text("#") }
            }
        } else {
            Row {
                Button(onClick = { handleIntent(AppIntent.ClickDtmfButton) }) {
                    Text("dtmf")
                }
                Button(onClick = {
                    Log.i("CallingPage", "mute btn click")
                    handleIntent(AppIntent.ClickMuteButton)
                }) {
                    Text("mute")
                }
                Button(onClick = {
                    if (mainViewModel.appUiState.value is AppUiState.OnRinging ||
                        mainViewModel.appUiState.value is AppUiState.OnCalling
                    ) {
                        Log.i("CallingPage", "speakerphone btn click")
                        handleIntent(AppIntent.ClickSpeakerPhoneButton)
                    } else {
                        Toast.makeText(
                            context,
                            "播放铃声或通话中才能使用扬声器",
                            Toast.LENGTH_SHORT
                        ).show()
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
                Log.i("CallingPage", "hangup btn click")
                handleIntent(AppIntent.Hangup)
            }) {
                Text("挂断")
            }
            if (isShowDialPanel) {
                Button(onClick = { handleIntent(AppIntent.ClickDtmfButton) }) {
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
                                onBackToMain()
                            }

                            is AppUiState.OnCalling -> {
                                currentPhoneNum = it.biggerText
                                smallText = it.smallerText
                                isShowDialPanel = it.isShowDtmfPanel
                                muteState = it.isMuted
                                speakerphoneOpenState = it.isUseSpeakerPhone
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

@Preview
@Composable
fun PreviewCallingPage() {
    App_composeTheme {
        CallingPage(viewModel(), {}, {})
    }
}