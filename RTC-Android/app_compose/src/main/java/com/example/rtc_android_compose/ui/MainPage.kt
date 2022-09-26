package com.example.rtc_android_compose.ui

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.common.AppUiState
import com.example.common.AppViewModel
import com.example.rtc_android_compose.R
import com.example.rtc_android_compose.ui.theme.App_composeTheme
import com.tinet.ticloudrtc.ErrorCode
import kotlinx.coroutines.launch

@Composable
fun MainPage(
    mainViewModel: AppViewModel,
    navigateToLogin: () -> Unit = {},
    navigateToCalling: () -> Unit = {}
) {
    LaunchedEffect(mainViewModel) {
        launch {
            if (mainViewModel.isRtcClientInit().not()) navigateToLogin()
        }
    }
    MainPageContent(
        mainViewModel = mainViewModel,
        navigateToLogin = navigateToLogin,
        navigateToCalling = navigateToCalling
    )
}

data class BottomNavItem(
    val noSelectIconRes: Int,
    val selectedIconRes: Int,
    val label: String,
    val pageRote: String,
)

@Composable
fun MainPageContent(
    mainViewModel: AppViewModel,
    navigateToLogin: () -> Unit = {},
    navigateToCalling: () -> Unit = {}
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val mainNavController = rememberNavController()


    var selectedItem by remember { mutableStateOf(0) }
    val item = remember {
        mutableStateListOf(
            BottomNavItem(
                R.mipmap.icon_main_keyboard,
                R.mipmap.icon_main_keyboard_select,
                context.getString(R.string.app_compose_dial_panel),
                NavRoute.DIAL
            ),
            BottomNavItem(
                R.mipmap.icon_main_mine,
                R.mipmap.icon_main_mine_select,
                context.getString(R.string.app_compose_mine),
                NavRoute.MINE
            )
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            BottomNavigation(backgroundColor = Color.White) {
                item.forEachIndexed { index, bottomNavItem ->
                    BottomNavigationItem(
                        icon = {
                            Image(
                                painterResource(
                                    id = if (selectedItem == index) bottomNavItem.selectedIconRes
                                    else bottomNavItem.noSelectIconRes
                                ),
                                null
                            )
                        },
                        label = { Text(bottomNavItem.label) },
                        selected = selectedItem == index,
                        onClick = {
                            selectedItem = index
                            mainNavController.navigate(bottomNavItem.pageRote) {
                                launchSingleTop = true
                                restoreState = true

                            }
                        })
                }
            }
        },
    ) { innerPaddingModifier ->
        NavHost(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPaddingModifier),
            navController = mainNavController,
            startDestination = NavRoute.DIAL
        ) {
            composable(NavRoute.DIAL) {
                DialPage(
                    mainViewModel = mainViewModel
                )
            }
            composable(NavRoute.MINE) {
                MinePage(
                    mainViewModel = mainViewModel
                )
            }
        }
    }



    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.lifecycleScope.launch {
            lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                mainViewModel.appUiState.collect {
                    when (it) {
                        is AppUiState.OnInnerSdkError -> {
                            if (it.errorCode == ErrorCode.ERR_CALL_FAILED_PARAMS_INCORRECT) {
                                Toast.makeText(
                                    context,
                                    """
                                        sdk 内部错误
                                        errorCode: ${it.errorCode}
                                        errorMessage: ${it.errorMessage}
                                    """.trimIndent(),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        is AppUiState.LogoutFailed -> Toast.makeText(
                            context,
                            "退出登录失败：${it.errorMsg}",
                            Toast.LENGTH_SHORT
                        ).show()
                        is AppUiState.LogoutSuccess -> navigateToLogin()
                        is AppUiState.CallFailed -> Toast.makeText(
                            context,
                            "外呼失败：${it.errorMsg}",
                            Toast.LENGTH_SHORT
                        ).show()
                        is AppUiState.OnCallStart -> navigateToCalling()
                        is AppUiState.OnCallFailure -> Toast.makeText(
                            context,
                            "外呼错误：${it.errorMsg}",
                            Toast.LENGTH_SHORT
                        ).show()
                        is AppUiState.OnRefreshTokenFailed -> {
                            Toast.makeText(
                                context,
                                it.errorMsg,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        is AppUiState.OnAccessTokenHasExpired -> {
                            Toast.makeText(
                                context,
                                "access token 已过期",
                                Toast.LENGTH_SHORT
                            ).show()
                            navigateToLogin()
                        }
                        else -> {}
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewMainPage() {
    App_composeTheme {
        MainPage(viewModel())
    }
}
