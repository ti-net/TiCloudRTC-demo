package com.example.rtc_android_compose.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.common.AppIntent
import com.example.common.MainActivityViewModel
import com.example.rtc_android_compose.BuildConfig
import com.tinet.ticloudrtc.TiCloudRTC
import kotlinx.coroutines.launch

@Composable
fun MinePage(
    mainViewModel: MainActivityViewModel,
    mainNavController: NavController,
    navController: NavController
){
    Column(
        modifier = Modifier.fillMaxSize()
    ){
        Text("我的")
        Text("应用版本：${BuildConfig.VERSION_NAME}")
        Text("SDK 版本：${TiCloudRTC.getVersion()}")
        Button(onClick = {
            mainViewModel.viewModelScope.launch {
                mainViewModel.intentChannel.send(AppIntent.Logout)
            }
        }){
            Text("退出登录")
        }
    }
}