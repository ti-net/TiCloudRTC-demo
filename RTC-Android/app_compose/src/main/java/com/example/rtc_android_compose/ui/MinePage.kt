package com.example.rtc_android_compose.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterStart
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.common.AppIntent
import com.example.common.AppViewModel
import com.example.rtc_android_compose.ui.theme.App_composeTheme
import com.example.rtc_android_compose.ui.theme.MainBackgroundColor
import com.tinet.ticloudrtc.TiCloudRTC
import com.example.rtc_android_compose.R

@Composable
fun MinePage(
    mainViewModel: AppViewModel,
    handleIntent: (intent:AppIntent)->Unit
) {
    val titleHeight = remember { 40.dp }
    val titleBackground = remember { Color.White }
    val itemBackground = remember { Color.White }
    val itemHeight = remember { 40.dp }
    val itemGap = remember { 10.dp }
    val context = LocalContext.current

    Column(
        Modifier
            .fillMaxSize()
            .background(MainBackgroundColor)

    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .background(titleBackground)
                .height(titleHeight)
        ) {
            Text("我的", Modifier.align(Center))
        }
        Spacer(modifier = Modifier.height(itemGap))
        Box(
            Modifier
                .fillMaxWidth()
                .background(itemBackground)
                .height(itemHeight)
                .padding(start = 20.dp)
        ) {
            Text("应用版本：${context.getString(R.string.version_name)}", Modifier.align(CenterStart))
        }
        Spacer(modifier = Modifier.height(itemGap))
        Box(
            Modifier
                .fillMaxWidth()
                .background(itemBackground)
                .height(itemHeight)
                .padding(start = 20.dp)
        ) {
            Text("SDK 版本：${TiCloudRTC.getVersion()}", Modifier.align(CenterStart))
        }
        Box(
            Modifier
                .fillMaxSize()
                .padding(bottom = 30.dp, start = 20.dp, end = 20.dp)
        ) {
            Button(
                onClick = {
                    handleIntent(AppIntent.Logout)
                },
                Modifier
                    .align(BottomCenter)
                    .height(40.dp)
                    .fillMaxWidth(),
            ) {
                Text("退出登录", color = Color.White)
            }
        }
    }
}

@Preview
@Composable
fun PreviewMinePage() {
    App_composeTheme {
        MinePage(viewModel(),{})
    }
}