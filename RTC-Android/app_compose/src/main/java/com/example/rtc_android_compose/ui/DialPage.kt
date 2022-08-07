package com.example.rtc_android_compose.ui

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.common.MainActivityViewModel

@Composable
fun DialPage(
    mainViewModel: MainActivityViewModel,
    mainNavController: NavController,
    subNavController: NavController
){
    Text("dial page")
}