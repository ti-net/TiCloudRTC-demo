package com.example.rtc_android_compose.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.common.AppIntent
import com.example.common.MainActivityViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DialPage(
    mainViewModel: MainActivityViewModel,
    mainNavController: NavController,
    subNavController: NavController
) {

    var phoneNum by remember { mutableStateOf("") }
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(phoneNum)
        Row(modifier = Modifier.fillMaxWidth()) {
            Button(onClick = { phoneNum += "1" }) { Text("1") }
            Button(onClick = { phoneNum += "2" }) { Text("2") }
            Button(onClick = { phoneNum += "3" }) { Text("3") }
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            Button(onClick = { phoneNum += "4" }) { Text("4") }
            Button(onClick = { phoneNum += "5" }) { Text("5") }
            Button(onClick = { phoneNum += "6" }) { Text("6") }
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            Button(onClick = { phoneNum += "7" }) { Text("7") }
            Button(onClick = { phoneNum += "8" }) { Text("8") }
            Button(onClick = { phoneNum += "9" }) { Text("9") }
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            Button(onClick = { phoneNum += "*" }) { Text("*") }
            Button(onClick = { phoneNum += "0" }) { Text("0") }
            Button(onClick = { phoneNum += "#" }) { Text("#") }
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            Button(onClick = {
                mainViewModel.viewModelScope.launch {
                    mainViewModel.intentChannel.send(AppIntent.Call(
                        tel = phoneNum,
                        clid = "",
                        userField = "",
                        type = 6
                    ))
                }
            }) { Text("外呼") }
            Button(modifier = Modifier.combinedClickable(onLongClick = {
                phoneNum = ""
            }) { }, onClick = { phoneNum = phoneNum.dropLast(1) }) { Text("delete") }
        }

    }


}