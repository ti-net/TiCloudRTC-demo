package com.example.rtc_android_compose

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.common.AppViewModel
import com.example.rtc_android_compose.ui.CallingPage
import com.example.rtc_android_compose.ui.LoginPage
import com.example.rtc_android_compose.ui.MainPage
import com.example.rtc_android_compose.ui.NavRoute
import com.example.rtc_android_compose.ui.theme.App_composeTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        WindowCompat.setDecorFitsSystemWindows(window,false)
        setContent {

            val lifecycleOwner = LocalLifecycleOwner.current
            val localView = LocalView.current

            App_composeTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    val navControl = rememberNavController()
                    val mainViewModel = viewModel<AppViewModel>()
                    NavHost(navControl, startDestination = NavRoute.MAIN) {
                        composable(NavRoute.MAIN) {
                            MainPage(
                                mainViewModel = mainViewModel,
                                navigateToLogin = { navControl.navigate(NavRoute.LOGIN) },
                                navigateToCalling = { navControl.navigate(NavRoute.CALLING) }
                            )
                        }
                        composable(NavRoute.LOGIN) {
                            LoginPage(
                                mainViewModel = mainViewModel,
                                onLoginSuccess = { navControl.navigateUp() },
                                handleIntent = { mainViewModel.handleIntent(it) }
                            )
                        }
                        composable(NavRoute.CALLING) {
                            CallingPage(
                                mainViewModel = mainViewModel,
                                onBackToMain = { navControl.navigateUp() },
                                handleIntent = { mainViewModel.handleIntent(it) }
                            )
                        }
                    }
                }

            }
        }
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) return true
        return super.onKeyUp(keyCode, event)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) return true
        return super.onKeyDown(keyCode, event)
    }
}


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RequestPermissionDialog() {
    val permissions = mutableListOf(Manifest.permission.RECORD_AUDIO)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) permissions.add(Manifest.permission.BLUETOOTH_CONNECT)
    val multiplePermissionsState = rememberMultiplePermissionsState(permissions = permissions)

    if (multiplePermissionsState.allPermissionsGranted.not()) {
        LaunchedEffect(permissions) {
            multiplePermissionsState.launchMultiplePermissionRequest()
        }
    }
}


