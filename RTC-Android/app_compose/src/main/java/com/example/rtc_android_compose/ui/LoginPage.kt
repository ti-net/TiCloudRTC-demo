package com.example.rtc_android_compose.ui

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.common.AppIntent
import com.example.common.AppUiState
import com.example.common.AppViewModel
import com.example.rtc_android_compose.BuildConfig
import com.example.rtc_android_compose.R
import com.example.rtc_android_compose.ui.theme.App_composeTheme
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LoginPage(
    mainViewModel: AppViewModel,
    onLoginSuccess: () -> Unit = {}
) {
    val context = LocalContext.current
    var strPlatform by remember { mutableStateOf(BuildConfig.LOGIN_ENVIRONMENT_VALUE[0]) }
    var enterpriseId by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var textFieldHeight = remember{30.dp}

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 60.dp, start = 20.dp, end = 20.dp)
            .background(Color.White),
        horizontalAlignment = CenterHorizontally
    ) {
        Image(painter = painterResource(id = R.mipmap.app_compose_ic_launcher),
            contentDescription = "",
            modifier = Modifier
                .shadow(elevation = 20.dp, shape = RoundedCornerShape(20))
                .combinedClickable(
                    onLongClick = {
                        enterpriseId = context.getString(R.string.specified_enterprise_id)
                        username = context.getString(R.string.specified_username)
                        password = context.getString(R.string.specified_password)
                    }
                ) {}
        )
        Text(context.getString(R.string.app_name),Modifier.padding(top = 30.dp), fontSize = 24.sp)
        Text("让客户联络效率更高，体验更好", fontSize = 12.sp)
        SpecimenSpinners(
            title = "选择环境",
            specimens = specimen,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 30.dp)

        ) { index, _ ->
            strPlatform = BuildConfig.LOGIN_ENVIRONMENT_VALUE[index]
        }
        TextField(
            value = strPlatform,
            placeholder = { Text(context.getString(R.string.app_compose_platform_url)) },
            onValueChange = { strPlatform = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp)
                .background(Color.White)
            ,
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color.Transparent
            )
        )
        TextField(
            value = enterpriseId,
            placeholder = { Text(context.getString(R.string.app_compose_enterprise_id)) },
            onValueChange = { enterpriseId = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp)
                .background(Color.White),
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color.Transparent
            )
        )

        TextField(
            value = username,
            placeholder = { Text(context.getString(R.string.app_compose_username)) },
            onValueChange = { username = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp)
                .background(Color.White),
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color.Transparent
            )
        )

        TextField(
            value = password,
            placeholder = { Text(context.getString(R.string.app_compose_password)) },
            onValueChange = { password = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp)
                .background(Color.White),
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color.Transparent
            )

        )
        Button(modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp)
            .height(50.dp), onClick = {
            mainViewModel.viewModelScope.launch {
                mainViewModel.intentChannel.send(
                    AppIntent.Login(
                        context,
                        platformUrl = strPlatform,
                        enterpriseId = enterpriseId,
                        usernameOrUserId = username,
                        passwordOrAccessToken = password
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
            lifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                mainViewModel.getLoginMessageFromLocalStore(context)
                    .collectLatest { loginMessage ->
                        enterpriseId = loginMessage.enterpriseId
                        username = loginMessage.usernameOrUserId
                        password = loginMessage.passwordOrAccessToken
                    }
            }

        }
        lifecycleOwner.lifecycleScope.launch {
            lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                mainViewModel.appUiState.collect {
                    Log.i("app_compose", "login page collected state: $it")
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
                            onLoginSuccess()
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

//This is the sample data
val specimen = ArrayList<Specimen>().apply {
    BuildConfig.LOGIN_ENVIRONMENT_NAME.forEachIndexed { index, value ->
        add(Specimen(plantId = index, plantName = value))
    }
}


//this is the model
data class Specimen(
    var plantId: Int = 0,
    var plantName: String = "",
    var specimenId: String = "",
    var location: String = "",
    var description: String = "",
    var datePlanted: String = "",
    var latitude: String = "",
    var longitude: String = ""
) {
    override fun toString(): String {
        return "$plantName $description $location"
    }
}

@Preview
@Composable
fun PreviewSpecimenSpinners() {
    SpecimenSpinners(title = "选择环境", specimens = specimen, modifier = Modifier.fillMaxWidth())
}

//this is the composable funstion
@Composable
fun SpecimenSpinners(
    modifier: Modifier = Modifier,
    title: String = "",
    specimens: List<Specimen>,
    onItemSelected: (index: Int, value: String) -> Unit = { _, _ -> }
) {

    var specimenText by remember { mutableStateOf(if (specimens.isNotEmpty()) specimens.first().plantName else Specimen().plantName) }
    var expanded by remember { mutableStateOf(false) }

    Box {
        OutlinedTextField(
            value = specimenText,
            onValueChange = { },
            label = {
                Text(title)
            },
            modifier = modifier,
            trailingIcon = { Icon(Icons.Outlined.ArrowDropDown, null) },
            readOnly = true,
            shape = RoundedCornerShape(6.dp),
            leadingIcon = {},
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp)
        ) {
            specimens.forEachIndexed { index, specimen ->
                DropdownMenuItem(
                    modifier = Modifier
                        .padding(start = 20.dp, end = 20.dp)
                        .fillMaxWidth(),
                    onClick = {
                        expanded = false
                        specimenText = specimen.toString()
                        onItemSelected(index, specimenText)
                    }) {
                    Text(text = specimen.toString())
                }
            }
        }
        Spacer(
            modifier = Modifier
                .padding(10.dp)
                .matchParentSize()
                .background(Color.Transparent)
                .clickable(
                    onClick = { expanded = !expanded }
                )
        )
    }
}

@Preview
@Composable
fun PreviewLoginPage() {
    App_composeTheme {
        LoginPage(viewModel())
    }
}
